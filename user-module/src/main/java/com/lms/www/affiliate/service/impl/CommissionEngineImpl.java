package com.lms.www.affiliate.service.impl;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.CommissionType;
import com.lms.www.affiliate.entity.CommissionRule;
import com.lms.www.affiliate.repository.CommissionRuleRepository;
import com.lms.www.affiliate.service.CommissionEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Pure calculation engine for affiliate commissions.
 * NO database writes, NO wallet operations.
 */
@Service
@RequiredArgsConstructor
public class CommissionEngineImpl implements CommissionEngine {

    private final CommissionRuleRepository ruleRepository;

    @Override
    public BigDecimal calculateCommission(BigDecimal orderAmount, Affiliate affiliate, Long courseId) {

        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid order amount");
        }

        if (affiliate.getCommissionType() == null || affiliate.getCommissionValue() == null) {
            throw new IllegalStateException("Commission not configured for affiliate: " + affiliate.getId());
        }

        // 1. Handle FIXED commission with safety cap
        if (affiliate.getCommissionType() == CommissionType.FIXED) {
            return affiliate.getCommissionValue().min(orderAmount);
        }

        // 2. Handle PERCENTAGE commission
        BigDecimal baseRate = affiliate.getCommissionValue()
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        // 3. Apply override rules
        Optional<CommissionRule> ruleOpt = ruleRepository.findByCourseIdAndActiveTrue(courseId);

        if (ruleOpt.isPresent()) {
            CommissionRule rule = ruleOpt.get();

            BigDecimal ruleRate = Optional.ofNullable(rule.getAffiliatePercent())
                    .orElse(BigDecimal.ZERO)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            // Logic: if bonus -> add to base, else -> override base
            if (rule.isBonus()) {
                baseRate = baseRate.add(ruleRate);
            } else {
                baseRate = ruleRate;
            }
        }

        // 4. Safety validations
        if (baseRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid commission rate calculated");
        }

        // Cap commission at 100% (1.0)
        if (baseRate.compareTo(BigDecimal.ONE) > 0) {
            baseRate = BigDecimal.ONE;
        }

        return orderAmount.multiply(baseRate).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    @Deprecated
    public void creditCommissionToAffiliateWallet(BigDecimal orderAmount, Affiliate affiliate, Long courseId, Long saleId) {
        // This method is deprecated and should be removed from the interface once all callers are updated.
        // Responsibilities moved to CommissionService.
        throw new UnsupportedOperationException("creditCommissionToAffiliateWallet moved to CommissionService");
    }
}
