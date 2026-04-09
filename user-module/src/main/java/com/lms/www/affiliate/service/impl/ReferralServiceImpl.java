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
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
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
    private final com.lms.www.affiliate.service.AffiliateService affiliateService;
    private final com.lms.www.security.UserContext userContext;
    private final StudentFeeAllocationRepository allocationRepository;
    private final com.lms.www.repository.UserRepository userRepository;

    public ReferralServiceImpl(
            AffiliateRepository affiliateRepository,
            AffiliateSaleRepository saleRepository,
            AffiliateLinkRepository linkRepository,
            CommissionRuleRepository ruleRepository,
            com.lms.www.affiliate.service.AffiliateService affiliateService,
            com.lms.www.security.UserContext userContext,
            StudentFeeAllocationRepository allocationRepository,
            com.lms.www.repository.UserRepository userRepository) {
        this.affiliateRepository = affiliateRepository;
        this.saleRepository = saleRepository;
        this.linkRepository = linkRepository;
        this.ruleRepository = ruleRepository;
        this.affiliateService = affiliateService;
        this.userContext = userContext;
        this.allocationRepository = allocationRepository;
        this.userRepository = userRepository;
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

        // 1. Ensure Student Affiliate Account exists (Auto-register if missing)
        Affiliate affiliate = affiliateRepository.findByUserId(currentUserId).orElseGet(() -> {
            log.info("Auto-registering student {} as affiliate to support referral links", currentUserId);
            com.lms.www.model.User user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("Logged in user not found in DB: " + currentUserId));

            com.lms.www.affiliate.dto.RegisterAffiliateRequest regReq = new com.lms.www.affiliate.dto.RegisterAffiliateRequest();
            regReq.setUserId(currentUserId);
            regReq.setName((user.getFirstName() != null ? user.getFirstName() : "") + " " + (user.getLastName() != null ? user.getLastName() : ""));
            regReq.setEmail(user.getEmail());
            regReq.setMobile(user.getPhone() != null ? user.getPhone() : "0000000000");

            return affiliateService.registerStudentAsAffiliate(regReq);
        });

        if (affiliate == null) {
            log.warn("Affiliate profile could not be created/found for user {}", currentUserId);
            return null;
        }

        // 2. Specific Course Eligibility (Using Fee Allocation instead of Sales)
        if (courseId != null) {
            StudentFeeAllocation allocation = allocationRepository.findByUserId(currentUserId).stream()
                    .filter(a -> a.getCourseId().equals(courseId))
                    .findFirst()
                    .orElse(null);

            if (allocation == null) {
                log.warn("Refusal: User {} tried to refer course {} without active enrollment.", currentUserId, courseId);
                return null;
            }

            Long batchId = allocation.getBatchId();

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
                        return allocationRepository.findByUserId(userId).stream()
                                .anyMatch(a -> a.getCourseId().equals(courseId));
                    }
                    return true;
                }).orElse(true); // If we have a valid referral code but no linked User ID (professional
                                 // partner), allow it.
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchasedCourseResponse> getPurchasedCourses(Long studentId) {
        Affiliate affiliate = affiliateRepository.findByUserId(studentId).orElse(null);
        List<StudentFeeAllocation> allocations = allocationRepository.findByUserId(studentId);

        return allocations.stream()
                .map(allocation -> {
                    String referralCode = null;
                    boolean hasLink = false;

                    if (affiliate != null) {
                        // Optimizing lookup: find existing link for this batch
                        Optional<com.lms.www.affiliate.entity.AffiliateLink> link = linkRepository
                                .findByAffiliateAndBatchId(affiliate, allocation.getBatchId());
                        if (link.isPresent()) {
                            referralCode = link.get().getReferralCode();
                            hasLink = true;
                        }
                    }

                    return PurchasedCourseResponse.builder()
                            .courseId(allocation.getCourseId())
                            .batchId(allocation.getBatchId())
                            .courseName(allocation.getCourseName() != null ? allocation.getCourseName() : "Course " + allocation.getCourseId())
                            .purchaseAmount(allocation.getPayableAmount())
                            .purchaseDate(allocation.getAllocationDate().atStartOfDay())
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
