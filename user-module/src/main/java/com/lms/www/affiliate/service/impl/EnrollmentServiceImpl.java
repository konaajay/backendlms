package com.lms.www.affiliate.service.impl;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateSale;
import com.lms.www.affiliate.repository.AffiliateSaleRepository;
import com.lms.www.affiliate.dto.PricingResponseDTO;
import com.lms.www.affiliate.service.*;
import com.lms.www.management.model.Course;
import com.lms.www.management.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final ReferralService referralService;
    private final AffiliateSaleRepository saleRepository;
    private final CommissionEngine commissionEngine;
    private final AffiliateLeadService leadService;
    private final PricingService pricingService;
    private final CourseService courseService;

    @Override
    @Transactional
    public void processEnrollment(Long studentId, Long courseId, Long batchId, BigDecimal amount, String referralCode) {
        log.info("Processing enrollment for student={}, course={}, referralCode={}", studentId, courseId, referralCode);

        // Don't auto-create account anymore, student will join via dashboard if
        // interested
        String myCode = referralService.getOrCreateReferralCode(studentId, courseId, false);
        log.info("Student {} referral code lookup (null if not joined): {}", studentId, myCode);

        // 2. Handle Referral Commission if a referral code was used
        if (referralCode != null && !referralCode.isBlank()) {
            processReferralCredit(studentId, courseId, batchId, amount, referralCode);
        }
    }

    private void processReferralCredit(Long studentId, Long courseId, Long batchId, BigDecimal amount,
            String referralCode) {
        Optional<Affiliate> referrerOpt = referralService.getReferrerByCode(referralCode);

        if (referrerOpt.isEmpty()) {
            log.warn("Invalid referral code used: {}", referralCode);
            return;
        }

        Affiliate referrer = referrerOpt.get();

        // 1. Identify the Lead and mark as ENROLLED to increase counters
        try {
            com.lms.www.affiliate.dto.AffiliateLeadDTO lead = leadService.getLeadByStudentId(studentId);

            if (lead != null) {
                leadService.updateLeadStatus(lead.getId(), com.lms.www.affiliate.entity.AffiliateLead.LeadStatus.ENROLLED,
                        "SYSTEM", "Automated conversion via purchase");
                log.info("Lead {} converted to ENROLLED for student {}", lead.getId(), studentId);
            } else {
                log.warn("No lead found for student ID {} to convert to ENROLLED.", studentId);
            }
        } catch (Exception e) {
            log.warn("Could not auto-convert lead status for student {}: {}", studentId, e.getMessage());
        }

        // Anti-Fraud: User cannot refer themselves
        if (referrer.getUserId() != null && referrer.getUserId().equals(studentId)) {
            log.warn("Self-referral detected for student {}. Skipping commission.", studentId);
            return;
        }

        // Eligibility: Referrer must have purchased the course first
        if (!referralService.isEligibleToRefer(referrer.getUserId(), courseId)) {
            log.warn("Referrer {} has not purchased course {} yet. Skipping commission.", referrer.getId(), courseId);
            return;
        }

        // Idempotency: Avoid duplicate commission for same student + course
        if (saleRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.warn("Duplicate sale blocked for student {} and course {}", studentId, courseId);
            return;
        }

        // 1. Determine Pricing Breakdown
        BigDecimal originalAmount = amount;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalPaidAmount = amount;

        try {
            Course course = courseService.getCourseById(courseId);
            if (course != null && course.getCourseFee() != null) {
                BigDecimal baseFee = BigDecimal.valueOf(course.getCourseFee());
                PricingResponseDTO pricing = pricingService.calculatePrice(courseId, referralCode, baseFee);
                
                originalAmount = pricing.getOriginalPrice();
                discountAmount = pricing.getDiscountAmount();
                finalPaidAmount = pricing.getFinalPrice(); // Trust the pricing service
                
                // If the provided 'amount' doesn't match our finalPaidAmount, 
                // it means there might be other discounts or manual adjustments.
                // For now, we trust the 'amount' as the absolute source of truth for payment.
                if (amount.compareTo(finalPaidAmount) != 0) {
                   log.info("Payment amount {} differs from calculated referral price {}. Using payment amount.", amount, finalPaidAmount);
                   finalPaidAmount = amount;
                }
            }
        } catch (Exception e) {
            log.warn("Could not calculate precise pricing breakdown for sale: {}", e.getMessage());
        }

        AffiliateSale sale = AffiliateSale.builder()
                .affiliate(referrer)
                .studentId(studentId)
                .courseId(courseId)
                .batchId(batchId)
                .orderId("REF-" + studentId + "-" + System.currentTimeMillis())
                .originalAmount(originalAmount)
                .discountAmount(discountAmount)
                .orderAmount(finalPaidAmount)
                .commissionAmount(commissionEngine.calculateCommission(finalPaidAmount, referrer, courseId))
                .status(AffiliateSale.SaleStatus.APPROVED)
                .build();

        saleRepository.save(sale);
        log.info("Sale record created (Referral) - Paid: {}, Commission: {}", finalPaidAmount, sale.getCommissionAmount());
    }
}
