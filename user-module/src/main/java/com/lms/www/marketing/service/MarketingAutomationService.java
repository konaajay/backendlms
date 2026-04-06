package com.lms.www.marketing.service;

import com.lms.www.marketing.event.MarketingEvents.*;
import com.lms.www.marketing.model.enums.WalletTransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

@Service
public class MarketingAutomationService {
    private static final Logger log = LoggerFactory.getLogger(MarketingAutomationService.class);

    private final WalletService walletService;

    public MarketingAutomationService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void triggerWelcomeReward(Long userId) {
        log.info("Manually triggering Welcome Reward for UserId: {}", userId);
        BigDecimal welcomeAmount = new BigDecimal("20.00");

        walletService.addCredits(
                userId,
                welcomeAmount,
                WalletTransactionType.EARN,
                "SYSTEM",
                "WELCOME_" + userId,
                "Registration welcome bonus (Manual)");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRegistration(UserRegisteredEvent event) {
        log.info("Handling Registration Event for UserId: {}", event.getUserId());
        BigDecimal welcomeAmount = new BigDecimal("20.00");

        walletService.addCredits(
                event.getUserId(),
                welcomeAmount,
                WalletTransactionType.EARN,
                "SYSTEM",
                "WELCOME_" + event.getUserId(),
                "Registration welcome bonus");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchase(PurchaseCompletedEvent event) {
        log.info("Handling Purchase Event for OrderId: {}", event.getOrderId());
        BigDecimal cashback = event.getOrderAmount().multiply(new BigDecimal("0.05"));

        if (cashback.compareTo(BigDecimal.ZERO) > 0) {
            walletService.addCredits(
                    event.getUserId(),
                    cashback,
                    WalletTransactionType.EARN,
                    "CASHBACK",
                    "CASH_" + event.getOrderId(),
                    "Cashback for order #" + event.getOrderId());
        }
    }

    public void triggerEngagementReward(Long userId, String eventType, String eventRef) {
        log.info("Triggering Engagement Reward: {} for UserId: {}", eventType, userId);

        BigDecimal amount = switch (eventType) {
            case "WEBINAR_ATTENDED" -> new BigDecimal("50.00");
            case "DAILY_LOGIN" -> new BigDecimal("5.00");
            case "PROFILE_COMPLETED" -> new BigDecimal("15.00");
            default -> BigDecimal.ZERO;
        };

        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            walletService.addCredits(
                    userId,
                    amount,
                    WalletTransactionType.EARN,
                    "EVENT",
                    eventRef,
                    "Engagement reward for " + eventType);
        }
    }
}
