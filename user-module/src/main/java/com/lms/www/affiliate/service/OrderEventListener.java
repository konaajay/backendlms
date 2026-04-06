package com.lms.www.affiliate.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.lms.www.affiliate.event.OrderCompletedEvent;
import com.lms.www.tracking.service.TrackingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final AffiliateService affiliateService;
    private final TrackingService trackingService;
    private final FraudDetectionService fraudDetectionService;

    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {

        String orderId = event.getOrderId();
        log.info("OrderCompletedEvent received for orderId={}", orderId);
        
        // In the Lead-based MVP, sales are manually verified and converted.
        // We do not process sales automatically here anymore.
    }

    private String resolveReferralCode(OrderCompletedEvent event) {

        String referralCode = event.getReferralCode();

        if (referralCode != null && !referralCode.isBlank()) {
            return referralCode;
        }

        if (event.getSessionId() != null) {

            String cachedReferral =
                    trackingService.getReferralFromCache(event.getSessionId());

            if (cachedReferral != null && !cachedReferral.isBlank()) {

                log.info("Referral {} retrieved from cache for session={}",
                        cachedReferral, event.getSessionId());

                return cachedReferral;
            }
        }

        return null;
    }
}