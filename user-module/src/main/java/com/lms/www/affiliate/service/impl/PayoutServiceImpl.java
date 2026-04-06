package com.lms.www.affiliate.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliatePayout;
import com.lms.www.affiliate.repository.AffiliatePayoutRepository;

import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.service.PayoutService;
import com.lms.www.affiliate.service.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutServiceImpl implements PayoutService {

    private final AffiliatePayoutRepository payoutRepository;
    private final AffiliateRepository affiliateRepository;
    private final WalletService walletService;

    @Override
    public List<AffiliatePayout> getPayouts(Affiliate affiliate) {
        return payoutRepository.findByAffiliate(affiliate);
    }

    @Override
    public List<AffiliatePayout> getPayoutsByAffiliateId(Long affiliateId) {
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found: " + affiliateId));
        if (affiliate == null)
            throw new IllegalStateException("Affiliate cannot be null");
        return payoutRepository.findByAffiliate(affiliate);
    }

    @Override
    public List<AffiliatePayout> getPayoutsByUserId(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found for user: " + userId));
        return payoutRepository.findByAffiliateId(affiliate.getId());
    }

    @Override
    @Transactional
    public AffiliatePayout requestPayout(Long userId, BigDecimal amount) {
        log.info("[PayoutService] Delegating payout request for user {} amount {}", userId, amount);
        walletService.requestPayout(userId, amount);

        // Retrieve the latest PENDING payout for this user
        return payoutRepository.findTopByAffiliate_UserIdAndStatusOrderByCreatedAtDesc(
                userId, AffiliatePayout.PayoutStatus.PENDING);
    }

    @Override
    @Transactional
    public void approvePayout(Long payoutId, String processedBy) {
        log.info("[PayoutService] Delegating payout approval for id {} by {}", payoutId, processedBy);
        walletService.approvePayout(payoutId);
    }

    @Override
    @Transactional
    public void rejectPayout(Long payoutId, String reason) {
        log.info("[PayoutService] Delegating payout rejection for id {} reason {}", payoutId, reason);
        walletService.rejectPayout(payoutId, reason);
    }
}
