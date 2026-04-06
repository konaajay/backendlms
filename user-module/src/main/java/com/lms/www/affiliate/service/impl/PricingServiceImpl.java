package com.lms.www.affiliate.service.impl;

import com.lms.www.affiliate.dto.PricingResponseDTO;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.CommissionRule;
import com.lms.www.affiliate.repository.CommissionRuleRepository;
import com.lms.www.affiliate.service.PricingService;
import com.lms.www.affiliate.service.ReferralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingServiceImpl implements PricingService {

    private final ReferralService referralService;
    private final CommissionRuleRepository ruleRepository;

    @Override
    public PricingResponseDTO calculatePrice(Long courseId,
                                             String referralCode,
                                             BigDecimal originalPrice) {

        // 🔴 Step 1: Validate price
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid course price");
        }

        // 🔴 Step 2: No referral → no discount
        if (referralCode == null || referralCode.isBlank()) {
            return buildNoDiscount(originalPrice);
        }

        // 🔴 Step 3: Validate referral
        Optional<Affiliate> referrerOpt = referralService.getReferrerByCode(referralCode);

        if (referrerOpt.isEmpty()) {
            log.warn("Invalid referral code: {}", referralCode);
            return buildNoDiscount(originalPrice);
        }

        // 🔴 Step 4: Get rule
        CommissionRule rule = ruleRepository
                .findByCourseIdAndActiveTrue(courseId)
                .orElse(null);

        if (rule == null || rule.getStudentDiscountPercent() == null) {
            return buildNoDiscount(originalPrice);
        }

        BigDecimal discountPercent = rule.getStudentDiscountPercent();

        // 🔴 Step 5: Validate discount %
        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 ||
                discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalStateException("Invalid discount configuration");
        }

        // 🔴 Step 6: Calculate discount
        BigDecimal discountAmount = originalPrice
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal finalPrice = originalPrice.subtract(discountAmount);

        // 🔴 Step 7: Safety check
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        log.info("Pricing calculated: course={}, original={}, discount={}, final={}",
                courseId, originalPrice, discountAmount, finalPrice);

        return PricingResponseDTO.builder()
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .discountPercent(discountPercent)
                .referralCode(referralCode)
                .discountApplied(true)
                .build();
    }

    private PricingResponseDTO buildNoDiscount(BigDecimal price) {
        return PricingResponseDTO.builder()
                .originalPrice(price)
                .discountAmount(BigDecimal.ZERO)
                .finalPrice(price)
                .discountPercent(BigDecimal.ZERO)
                .discountApplied(false)
                .build();
    }
}
