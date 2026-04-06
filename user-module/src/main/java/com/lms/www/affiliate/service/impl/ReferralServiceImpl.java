package com.lms.www.affiliate.service.impl;

import com.lms.www.affiliate.dto.AffiliateLinkDTO;
import com.lms.www.affiliate.dto.PurchasedCourseResponse;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateLink;
import com.lms.www.affiliate.entity.CommissionRule;
import com.lms.www.affiliate.repository.AffiliateLinkRepository;
import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.repository.AffiliateSaleRepository;
import com.lms.www.affiliate.repository.CommissionRuleRepository;
import com.lms.www.affiliate.service.ReferralService;
import com.lms.www.management.model.Course;
import com.lms.www.management.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReferralServiceImpl implements ReferralService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateSaleRepository saleRepository;
    private final AffiliateLinkRepository linkRepository;
    private final CommissionRuleRepository ruleRepository;
    private final CourseService courseService;
    private final com.lms.www.affiliate.service.AffiliateService affiliateService;
    private final com.lms.www.security.UserContext userContext;

    public ReferralServiceImpl(
            AffiliateRepository affiliateRepository,
            AffiliateSaleRepository saleRepository,
            AffiliateLinkRepository linkRepository,
            CommissionRuleRepository ruleRepository,
            CourseService courseService,
            com.lms.www.affiliate.service.AffiliateService affiliateService,
            com.lms.www.security.UserContext userContext) {
        this.affiliateRepository = affiliateRepository;
        this.saleRepository = saleRepository;
        this.linkRepository = linkRepository;
        this.ruleRepository = ruleRepository;
        this.courseService = courseService;
        this.affiliateService = affiliateService;
        this.userContext = userContext;
    }

    @Override
    @Transactional
    public String getOrCreateReferralCode(Long studentId, Long courseId, boolean forceCreate) {
        // 🔴 Production Fix 4: Security (Check studentId)
        Long currentUserId = userContext.getCurrentUserId();
        if (studentId != null && !studentId.equals(currentUserId)) {
            log.error("Security violation: User {} tried to generate code for User {}", currentUserId, studentId);
            throw new SecurityException("Unauthorized access to another user's referrals");
        }

        try {
            // 1. Ensure Student Affiliate Account exists
            Affiliate affiliate = affiliateRepository.findByUserId(currentUserId).orElse(null);
            if (affiliate == null)
                return null;

            // 2. Specific Course Eligibility
            if (courseId != null) {
                // Production Fix 5: Fallback logic is dangerous. Return null if not eligible.
                com.lms.www.affiliate.entity.AffiliateSale sale = saleRepository
                        .findByStudentIdAndCourseId(currentUserId, courseId)
                        .orElse(null);

                if (sale == null) {
                    log.warn("Refusal: User {} tried to refer course {} without purchase verification.", currentUserId,
                            courseId);
                    return null;
                }

                // Production Fix 6: Null safety for saleOpt.get()
                Long batchId = sale.getBatchId();

                // 🔴 Production Fix 3: Idempotency (Check if link already exists)
                Optional<AffiliateLink> existing = linkRepository.findByAffiliateAndBatchId(affiliate, batchId);
                if (existing.isPresent()) {
                    return existing.get().getReferralCode();
                }

                // 🔴 Production Fix 1: Rule Overrides
                CommissionRule rule = ruleRepository.findByCourseIdAndActiveTrue(courseId).orElse(null);

                BigDecimal commission = (rule != null) ? rule.getAffiliatePercent() : affiliate.getCommissionValue();
                BigDecimal discount = (rule != null) ? rule.getStudentDiscountPercent()
                        : affiliate.getStudentDiscountValue();

                // 🔴 Production Fix 2: Validation for discount
                if (discount == null)
                    discount = BigDecimal.ZERO;
                if (discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(BigDecimal.valueOf(100)) > 0) {
                    log.error("Invalid discount configuration for course {}: {}%", courseId, discount);
                    throw new IllegalArgumentException("Invalid discount configuration");
                }

                String generatedCode = affiliateService.generateLink(affiliate.getId(), courseId, batchId,
                        commission, discount, null, null)
                        .getReferralCode();

                // 🔴 Production Fix 9: Logging success
                log.info("Referral link generated: user={}, course={}, batch={}, code={}", currentUserId, courseId,
                        batchId, generatedCode);
                return generatedCode;
            }

            // Default to global code if no specific course requested
            return affiliate.getReferralCode();
        } catch (Exception e) {
            log.error("Top-level referral code generation error for user {}: {}", currentUserId, e.getMessage());
            return null;
        }
    }

    @Override
    public Optional<Affiliate> getReferrerByCode(String code) {
        if (code == null || code.isBlank())
            return Optional.empty();

        // 1. Direct Affiliate Match (Global Code)
        Optional<Affiliate> aff = affiliateRepository.findByReferralCode(code);
        if (aff.isPresent())
            return aff;

        // 2. Link Match
        return linkRepository.findByReferralCode(code)
                .map(link -> link.getAffiliate());
    }

    @Override
    public boolean isEligibleToRefer(Long userId, Long courseId) {
        // Enforce: ONLY Students must purchase the course to refer it.
        // Professional Affiliates/Instructors are always eligible.
        return affiliateRepository.findByUserId(userId)
                .map(affiliate -> {
                    // Default logic: Only Students are restricted.
                    // Professional partners (Individual, Institutional) are always allowed.
                    if (com.lms.www.affiliate.entity.AffiliateType.STUDENT.equals(affiliate.getType())) {
                        return saleRepository.existsByStudentIdAndCourseId(userId, courseId);
                    }
                    return true;
                }).orElse(true); // If we have a valid referral code but no linked User ID (professional
                                 // partner), allow it.
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchasedCourseResponse> getPurchasedCourses(Long studentId) {
        Affiliate affiliate = affiliateRepository.findByUserId(studentId).orElse(null);

        // 🔴 Production Fix 8: Fetch sales list once (already done, but let's confirm
        // usage)
        List<com.lms.www.affiliate.entity.AffiliateSale> sales = saleRepository.findByStudentId(studentId);

        return sales.stream()
                .map(sale -> {
                    String referralCode = null;
                    boolean hasLink = false;

                    if (affiliate != null) {
                        // Optimizing lookup: find existing link for this batch
                        Optional<com.lms.www.affiliate.entity.AffiliateLink> link = linkRepository
                                .findByAffiliateAndBatchId(affiliate, sale.getBatchId());
                        if (link.isPresent()) {
                            referralCode = link.get().getReferralCode();
                            hasLink = true;
                        }
                    }

                    // 🔴 Production Fix 7: Course name from Service
                    String courseName = "Course " + sale.getCourseId();
                    try {
                        Course course = courseService.getCourseById(sale.getCourseId());
                        if (course != null) {
                            courseName = course.getCourseName();
                        }
                    } catch (Exception e) {
                        log.warn("Could not fetch course name for ID {}: {}", sale.getCourseId(), e.getMessage());
                    }

                    return PurchasedCourseResponse.builder()
                            .courseId(sale.getCourseId())
                            .batchId(sale.getBatchId())
                            .courseName(courseName)
                            .purchaseAmount(sale.getOrderAmount())
                            .purchaseDate(sale.getCreatedAt())
                            .referralCode(referralCode)
                            .hasReferralLink(hasLink)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AffiliateLinkDTO> filterLinksByPurchases(Long studentId, List<AffiliateLinkDTO> links) {
        // Find all unique courseIds the student has purchased
        java.util.Set<Long> purchasedBatchIds = saleRepository.findByStudentId(studentId).stream()
                .map(com.lms.www.affiliate.entity.AffiliateSale::getBatchId)
                .collect(java.util.stream.Collectors.toSet());

        return links.stream()
                .filter(link -> purchasedBatchIds.contains(link.getBatchId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchasedCourseResponse> getPurchasedCoursesSecure() {
        return getPurchasedCourses(userContext.getCurrentUserId());
    }

    @Override
    @Transactional
    public String getOrCreateReferralCodeSecure(Long courseId, boolean forceCreate) {
        return getOrCreateReferralCode(userContext.getCurrentUserId(), courseId, forceCreate);
    }
}
