package com.lms.www.affiliate.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.affiliate.dto.AffiliateAdminResponse;
import com.lms.www.affiliate.dto.AffiliateDashboardResponse;
import com.lms.www.affiliate.dto.AffiliateLeadDTO;
import com.lms.www.affiliate.dto.AffiliateLinkDTO;
import com.lms.www.affiliate.dto.AffiliateMetricsResponse;
import com.lms.www.affiliate.dto.CreateAffiliateRequest;
import com.lms.www.affiliate.dto.RegisterAffiliateRequest;
import com.lms.www.affiliate.dto.UpdateAffiliateSettingsRequest;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateStatus;
import com.lms.www.affiliate.entity.CommissionType;
import com.lms.www.affiliate.entity.AffiliateClick;
import com.lms.www.affiliate.entity.AffiliateLead;
import com.lms.www.affiliate.repository.AffiliateLeadRepository;

import com.lms.www.affiliate.entity.AffiliateLink;
import com.lms.www.affiliate.entity.AffiliateSale;
import com.lms.www.affiliate.entity.AffiliateType;
import com.lms.www.affiliate.entity.WalletConfig;
import com.lms.www.affiliate.repository.AffiliateClickRepository;
// import com.lms.www.affiliate.repository.AffiliateLeadRepository; // Removed
import com.lms.www.affiliate.repository.AffiliateLinkRepository;
import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.repository.AffiliateSaleRepository;
import com.lms.www.affiliate.repository.AffiliateWalletRepository;
import com.lms.www.affiliate.repository.WalletConfigRepository;
import com.lms.www.affiliate.service.AffiliateService;
import com.lms.www.affiliate.service.WalletService;

import com.lms.www.security.UserContext;
import com.lms.www.affiliate.dto.AffiliateDTO;
import com.lms.www.affiliate.dto.BankDetailsDTO;
import com.lms.www.service.AuditLogService;
import com.lms.www.repository.UserRepository;
import com.lms.www.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AffiliateServiceImpl implements AffiliateService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateLinkRepository linkRepository;
    private final AffiliateClickRepository clickRepository;
    private final AffiliateSaleRepository saleRepository;
    private final AffiliateWalletRepository walletRepository;
    private final AffiliateLeadRepository leadRepository;
    private final WalletConfigRepository walletConfigRepository;
    private final WalletService walletService;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;

    @Qualifier("mainAuditLogService")
    private final AuditLogService genericAuditLogService;

    private static final BigDecimal DEFAULT_STUDENT_COMMISSION = new BigDecimal("10.0");
    private static final BigDecimal DEFAULT_AFFILIATE_COMMISSION = new BigDecimal("15.0");

    // ── Wallet Config ──────────────────────────────────────────────────────────

    @Override
    public WalletConfig getWalletConfig() {
        return walletConfigRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("WalletConfig not found"));
    }

    @Override
    @Transactional
    public WalletConfig updateWalletConfig(WalletConfig config) {
        WalletConfig existing = walletConfigRepository.findTopByOrderByIdAsc()
                .orElseGet(() -> new WalletConfig());

        existing.setDefaultMinPayoutAmount(config.getDefaultMinPayoutAmount());
        existing.setMaxPayoutAmount(config.getMaxPayoutAmount());
        existing.setStudentWithdrawalEnabled(config.isStudentWithdrawalEnabled());
        existing.setAffiliateWithdrawalEnabled(config.isAffiliateWithdrawalEnabled());
        existing.setMaxPendingPayouts(config.getMaxPendingPayouts());

        return walletConfigRepository.save(existing);
    }

    // ── Affiliate CRUD ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Affiliate createAffiliate(CreateAffiliateRequest request) {
        if (request.getUserId() != null) {
            Optional<Affiliate> byUserId = affiliateRepository.findByUserId(request.getUserId());
            if (byUserId.isPresent())
                return byUserId.get();
        }
        if (request.getEmail() != null) {
            Optional<Affiliate> byEmail = affiliateRepository.findByEmail(request.getEmail());
            if (byEmail.isPresent())
                return byEmail.get();
        }

        log.info("[AffiliateService] Creating affiliate: {}", request);

        // Map String type to Enum
        AffiliateType affType = AffiliateType.AFFILIATE;
        if (request.getType() != null) {
            try {
                affType = AffiliateType.valueOf(request.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid affiliate type: {}. Defaulting to AFFILIATE", request.getType());
            }
        }

        if (AffiliateType.STUDENT.equals(affType) && request.getUserId() == null) {
            throw new IllegalArgumentException("Student affiliate must have a userId");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required for affiliate creation");
        }

        if (request.getMobile() == null || request.getMobile().isBlank()) {
            throw new IllegalArgumentException("Mobile is required for affiliate creation");
        }

        // Set Business Defaults
        BigDecimal commissionValue = request.getCommissionValue() != null
                ? BigDecimal.valueOf(request.getCommissionValue())
                : (AffiliateType.STUDENT.equals(affType) ? DEFAULT_STUDENT_COMMISSION : DEFAULT_AFFILIATE_COMMISSION);

        if (commissionValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Commission value cannot be negative");
        }

        Affiliate affiliate = Affiliate.builder()
                .userId(request.getUserId())
                .name(request.getName() != null && !request.getName().isEmpty() ? request.getName() : "Affiliate")
                .username(request.getUsername() != null && !request.getUsername().isEmpty()
                        ? request.getUsername()
                        : request.getEmail().split("@")[0])
                .email(request.getEmail())
                .mobile(request.getMobile())
                .commissionType(request.getCommissionType() != null
                        ? request.getCommissionType()
                        : CommissionType.PERCENTAGE)
                .commissionValue(commissionValue)
                .studentDiscountValue(new BigDecimal("10.0"))
                .cookieDays(request.getCookieDays() != null ? request.getCookieDays() : 30)
                .status(com.lms.www.affiliate.entity.AffiliateStatus.ACTIVE)
                .type(affType)
                .minPayout(walletService.getWalletConfig().getDefaultMinPayoutAmount())
                .withdrawalEnabled(affType != AffiliateType.STUDENT)
                .build();

        if (affiliate.getReferralCode() == null || affiliate.getReferralCode().isEmpty()) {
            String code = generateUniqueCode();
            affiliate.setReferralCode(code);
        }

        try {
            Affiliate saved = affiliateRepository.save(affiliate);
            walletService.getWallet(saved.getId());

            // 🔹 Audit Log
            logAudit("CREATE_AFFILIATE", "Affiliate", saved.getId());

            return saved;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Handle race condition: check if it was already created by another thread
            if (request.getUserId() != null) {
                return affiliateRepository.findByUserId(request.getUserId())
                        .orElseThrow(() -> new RuntimeException("Affiliate created but not found", e));
            } else if (request.getEmail() != null) {
                return affiliateRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException("Affiliate created but not found", e));
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public Affiliate registerStudentAsAffiliate(RegisterAffiliateRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }

        Optional<Affiliate> existing = affiliateRepository.findByUserId(request.getUserId());
        if (existing.isPresent()) {
            return existing.get(); // already affiliate
        }

        CreateAffiliateRequest createReq = new CreateAffiliateRequest();
        createReq.setUserId(request.getUserId());
        createReq.setName(request.getName() != null && !request.getName().isBlank() ? request.getName()
                : "Student " + request.getUserId());
        createReq.setEmail(request.getEmail());
        createReq.setMobile(request.getMobile());
        createReq.setType("STUDENT");
        createReq.setCommissionValue(DEFAULT_STUDENT_COMMISSION.doubleValue());

        return createAffiliate(createReq);
    }

    @Override
    @Transactional
    public Affiliate updateAffiliateSettings(Long affiliateId, UpdateAffiliateSettingsRequest request) {
        if (affiliateId == null)
            throw new IllegalArgumentException("Affiliate ID required");
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found: " + affiliateId));

        if (request.getCommissionType() != null)
            affiliate.setCommissionType(request.getCommissionType());
        if (request.getCommissionValue() != null) {
            BigDecimal val = BigDecimal.valueOf(request.getCommissionValue());
            if (val.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Invalid commission");
            affiliate.setCommissionValue(val);
        }
        if (request.getStudentDiscountValue() != null) {
            BigDecimal val = BigDecimal.valueOf(request.getStudentDiscountValue());
            if (val.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Invalid discount");
            affiliate.setStudentDiscountValue(val);
        }
        if (request.getCookieDays() != null)
            affiliate.setCookieDays(request.getCookieDays());
        if (request.getStatus() != null)
            affiliate.setStatus(request.getStatus());

        log.info("[AffiliateService] Updated settings for affiliate {}", affiliateId);
        if (affiliate == null)
            throw new RuntimeException("Affiliate object is null during update");

        Affiliate saved = affiliateRepository.save(affiliate);

        // 🔹 Audit Log
        logAudit("UPDATE_AFFILIATE_SETTINGS", "Affiliate", saved.getId());

        return saved;
    }

    @Override
    public Optional<Affiliate> getAffiliateById(Long id) {
        if (id == null)
            return Optional.empty();
        return affiliateRepository.findById(id);
    }

    @Override
    public Optional<Affiliate> getAffiliateByUserId(Long userId) {
        if (userId == null)
            return Optional.empty();
        return affiliateRepository.findByUserId(userId);
    }

    @Override
    public Optional<Affiliate> getAffiliateByCode(String code) {
        return affiliateRepository.findByReferralCode(code);
    }

    @Override
    public List<Affiliate> getAllAffiliates() {
        return affiliateRepository.findAll().stream()
                .filter(a -> a != null && a.getType() != null && 
                      (a.getType() == AffiliateType.AFFILIATE || a.getType() == AffiliateType.STUDENT))
                .collect(Collectors.toList());
    }

    @Override
    public List<AffiliateAdminResponse> getAllAffiliatesWithMetrics() {
        return getAllAffiliates().stream()
                .<AffiliateAdminResponse>map(a -> {
                    Long leads = leadRepository.countByAffiliateId(a.getId());
                    java.util.List<AffiliateSale.SaleStatus> activeStatuses = java.util.Arrays.asList(
                            AffiliateSale.SaleStatus.PENDING,
                            AffiliateSale.SaleStatus.APPROVED,
                            AffiliateSale.SaleStatus.PAID);
                    Long conversions = saleRepository.countByAffiliateIdAndStatusIn(a.getId(), activeStatuses);
                    BigDecimal revenue = saleRepository.sumRevenueByAffiliateIdAndStatuses(a.getId(), activeStatuses);
                    if (revenue == null)
                        revenue = BigDecimal.ZERO;

                    BigDecimal earned = saleRepository.sumCommissionByAffiliateIdAndStatuses(a.getId(), activeStatuses);
                    if (earned == null)
                        earned = BigDecimal.ZERO;

                    // Aggregate all clicks for all codes of this affiliate
                    java.util.List<String> codes = new java.util.ArrayList<>();
                    if (a.getReferralCode() != null)
                        codes.add(a.getReferralCode());
                    linkRepository.findByAffiliateId(a.getId()).forEach(l -> {
                        if (l.getReferralCode() != null)
                            codes.add(l.getReferralCode());
                    });
                    long clicks = clickRepository.countByAffiliateCodeIn(codes);

                    AffiliateAdminResponse res = new AffiliateAdminResponse();
                    res.setId(a.getId());
                    res.setName(a.getName());
                    res.setEmail(a.getEmail());
                    res.setMobile(a.getMobile());
                    res.setCode(a.getReferralCode());
                    res.setStatus(a.getStatus());
                    res.setTotalLeads(leads);
                    res.setConversions(conversions);
                    res.setTotalClicks(clicks);
                    res.setTotalRevenue(revenue);
                    res.setTotalEarned(earned);
                    res.setBankName(a.getBankName());
                    res.setAccountNumber(a.getAccountNumber());
                    res.setIfscCode(a.getIfscCode());
                    res.setAccountHolderName(a.getAccountHolderName());
                    res.setUpiId(a.getUpiId());
                    return res;
                })
                .collect(Collectors.toList());
    }

    private String generateUniqueCode() {
        for (int i = 0; i < 5; i++) {
            String code = "AFF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            if (affiliateRepository.findByReferralCode(code).isEmpty()
                    && linkRepository.findByReferralCode(code).isEmpty()) {
                return code;
            }
        }
        throw new RuntimeException("Failed to generate unique referral code after 5 attempts");
    }

    // ── Link Management ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public AffiliateLink generateLink(Long affiliateId, Long courseId, Long batchId,
            BigDecimal commission, BigDecimal discount, String customCode, LocalDateTime expiresAt) {

        Affiliate affiliate = validateAffiliate(affiliateId);
        validatePurchase(affiliate, courseId);
        return createOrUpdateLink(affiliate, courseId, batchId, commission, discount, customCode, expiresAt);
    }

    private Affiliate validateAffiliate(Long affiliateId) {
        if (affiliateId == null)
            throw new IllegalArgumentException("Affiliate ID required");
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found"));

        if (affiliate.getStatus() == AffiliateStatus.PENDING_APPROVAL) {
            affiliate.setStatus(AffiliateStatus.ACTIVE);
            affiliateRepository.save(affiliate);
        }

        if (affiliate.getStatus() != AffiliateStatus.ACTIVE) {
            throw new RuntimeException("Affiliate account is not ACTIVE. Status: " + affiliate.getStatus());
        }
        return affiliate;
    }

    private void validatePurchase(Affiliate affiliate, Long courseId) {
        if (affiliate.getType() == AffiliateType.STUDENT && courseId != null) {
            boolean purchased = saleRepository.existsByStudentIdAndCourseId(affiliate.getUserId(), courseId);
            if (!purchased) {
                throw new IllegalStateException("Cannot generate referral for unpurchased course");
            }
        }
    }

    private AffiliateLink createOrUpdateLink(Affiliate affiliate, Long courseId, Long batchId,
            BigDecimal commission, BigDecimal discount, String customCode, LocalDateTime expiresAt) {

        if (customCode != null && !customCode.isBlank()) {
            Optional<AffiliateLink> existingByCode = linkRepository.findByReferralCode(customCode);
            if (existingByCode.isPresent() && !existingByCode.get().getAffiliate().getId().equals(affiliate.getId())) {
                throw new IllegalArgumentException("Referral code already in use: " + customCode);
            }
        }

        return linkRepository.findByAffiliateAndBatchId(affiliate, batchId)
                .map(existing -> {
                    if (existing == null)
                        throw new RuntimeException("Existing link is null");
                    if (commission != null)
                        existing.setCommissionValue(commission);
                    if (discount != null)
                        existing.setStudentDiscountValue(discount);
                    if (customCode != null && !customCode.isBlank()) {
                        existing.setReferralCode(customCode);
                    }
                    if (expiresAt != null) {
                        existing.setExpiresAt(expiresAt);
                    }
                    return linkRepository.save(existing);
                })
                .orElseGet(() -> {
                    BigDecimal finalCommission = commission != null ? commission : affiliate.getCommissionValue();
                    BigDecimal finalDiscount = discount != null ? discount : affiliate.getStudentDiscountValue();

                    // Fallback to absolute defaults if affiliate fields are null
                    if (finalCommission == null) finalCommission = DEFAULT_AFFILIATE_COMMISSION;
                    if (finalDiscount == null) finalDiscount = new BigDecimal("10.0");

                    AffiliateLink link = AffiliateLink.builder()
                            .affiliate(affiliate)
                            .courseId(courseId)
                            .batchId(batchId)
                            .referralCode(
                                    customCode != null && !customCode.isBlank() ? customCode : generateUniqueCode())
                            .commissionValue(finalCommission)
                            .studentDiscountValue(finalDiscount)
                            .expiresAt(expiresAt)
                            .build();

                    if (link == null)
                        throw new RuntimeException("Link object build failed");
                    return linkRepository.save(link);
                });
    }

    @Override
    public Optional<AffiliateLink> getLinkByReferralCode(String code) {
        return linkRepository.findByReferralCode(code);
    }

    @Override
    public List<AffiliateLinkDTO> getAffiliateLinks(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found for user: " + userId));
        return getAffiliateLinks(affiliate);
    }

    @Override
    public List<AffiliateLinkDTO> getAffiliateLinksByAffiliateId(Long affiliateId) {
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found with ID: " + affiliateId));
        return getAffiliateLinks(affiliate);
    }

    private List<AffiliateLinkDTO> getAffiliateLinks(Affiliate affiliate) {
        List<AffiliateLink> links = linkRepository.findByAffiliate(affiliate);

        return links.stream().map(link -> {
            Long clicks = clickRepository.countByBatchIdAndAffiliateCode(link.getBatchId(), link.getReferralCode());
            Long leads = leadRepository.countByLinkId(link.getId());
            Long conversions = leadRepository.countByLinkIdAndStatus(link.getId(), AffiliateLead.LeadStatus.ENROLLED);
            BigDecimal earnings = saleRepository.sumCommissionByAffiliateIdAndBatchIdAndStatuses(
                    Objects.requireNonNull(affiliate.getId(), "Affiliate ID must not be null"),
                    Objects.requireNonNull(link.getBatchId(), "Batch ID must not be null"),
                    java.util.List.of(AffiliateSale.SaleStatus.APPROVED, AffiliateSale.SaleStatus.PAID));
            if (earnings == null)
                earnings = BigDecimal.ZERO;

            return AffiliateLinkDTO.builder()
                    .id(link.getId())
                    .affiliateId(link.getAffiliate().getId())
                    .courseId(link.getCourseId())
                    .batchId(link.getBatchId())
                    .code(link.getReferralCode())
                    .referralCode(link.getReferralCode())
                    .commissionValue(link.getCommissionValue())
                    .studentDiscountValue(link.getStudentDiscountValue())
                    .clicks(clicks)
                    .leads(leads)
                    .conversions(conversions)
                    .earnings(earnings)
                    .status(link.getStatus().name())
                    .createdAt(link.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    // ── Tracking & Conversions ────────────────────────────────────────────────

    @Override
    @Transactional
    public void trackClick(String affiliateCode, Long batchId, String ipAddress, String userAgent) {
        if (affiliateCode == null || affiliateCode.isBlank())
            return;

        log.info("[AffiliateService] Tracking click: code={}, batch={}, ip={}", affiliateCode, batchId, ipAddress);

        String finalGlobalCode = affiliateCode;
        Optional<Affiliate> aff = affiliateRepository.findByReferralCode(affiliateCode);

        if (aff.isEmpty()) {
            // Maybe it was a link-specific code
            aff = linkRepository.findByReferralCode(affiliateCode).map(AffiliateLink::getAffiliate);
        }

        if (aff.isPresent()) {
            finalGlobalCode = aff.get().getReferralCode();
        }

        AffiliateClick click = AffiliateClick.builder()
                .clickedCode(affiliateCode) // actual link clicked
                .affiliateCode(finalGlobalCode) // global affiliate identity
                .batchId(batchId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        if (click == null)
            throw new RuntimeException("Click object build failed");
        clickRepository.save(click);
        log.info("[AffiliateService] Click tracked successfully for code: {}", affiliateCode);
    }

    @Override
    @Transactional
    public AffiliateLead createLead(String name, String mobile, String email, Long courseId, Long batchId,
            String referralCode, String ipAddress) {
        log.info("[AffiliateService] Creating lead for code {}: {}/{}", referralCode, email, batchId);

        // 1. Duplicate check (mobile + batchId)
        Optional<AffiliateLead> existing = leadRepository.findByMobileAndBatchId(mobile, batchId);
        if (existing.isPresent()) {
            log.warn("[AffiliateService] Lead already exists for mobile {} and batch {}", mobile, batchId);
            return existing.get();
        }

        Affiliate affiliate = null;

        // 2. Identify Affiliate from referralCode
        if (referralCode != null && !referralCode.isBlank()) {
            Optional<AffiliateLink> linkOpt = linkRepository.findByReferralCode(referralCode);
            if (linkOpt.isPresent()) {
                AffiliateLink link = linkOpt.get();
                batchId = batchId != null ? batchId : link.getBatchId();
                courseId = courseId != null ? courseId : link.getCourseId();
                affiliate = link.getAffiliate();
            } else {
                affiliate = affiliateRepository.findByReferralCode(referralCode)
                        .orElse(null);
            }
        }

        if (affiliate == null) {
            log.warn("[AffiliateService] Lead creation skipped: Invalid or missing referralCode {}", referralCode);
            throw new IllegalArgumentException("Invalid referral code: " + referralCode);
        }

        AffiliateLead lead = AffiliateLead.builder()
                .name(name)
                .mobile(mobile)
                .email(email)
                .affiliate(affiliate)
                .referralCode(referralCode)
                .courseId(courseId)
                .batchId(batchId)
                .status(AffiliateLead.LeadStatus.NEW)
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .leadSource("AFFILIATE")
                .build();

        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public void processCommission(Long buyerUserId, String referralCode, BigDecimal coursePrice, Long saleId) {
        if (referralCode == null || referralCode.isBlank())
            return;

        // 1. Find Affiliate
        Affiliate affiliate = affiliateRepository.findByReferralCode(referralCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid referral code: " + referralCode));

        // 2. Prevent Self-Referral
        if (buyerUserId != null && buyerUserId.equals(affiliate.getUserId())) {
            log.warn("Self-referral detected for userId: {}. No commission awarded.", buyerUserId);
            return;
        }

        // 3. Calculate Commission
        BigDecimal commission = BigDecimal.ZERO;
        if (affiliate.getCommissionType() == CommissionType.PERCENTAGE) {
            commission = coursePrice.multiply(affiliate.getCommissionValue().divide(new BigDecimal("100.0")));
        } else {
            commission = affiliate.getCommissionValue();
        }

        // 4. Validate Amount
        if (commission.compareTo(BigDecimal.ZERO) <= 0)
            return;

        // 5. Credit Wallet
        walletService.creditCommission(affiliate.getId(), commission, saleId,
                "Referral commission for sale: " + saleId);
        log.info("Processed commission of {} for affiliate {}", commission, affiliate.getReferralCode());
    }

    @Override
    @Transactional
    public void requestWithdrawal(Long affiliateId, BigDecimal amount) {
        if (affiliateId == null)
            throw new IllegalArgumentException("Affiliate ID required");
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found"));

        walletService.requestPayout(affiliate.getUserId(), amount);
        log.info("Withdrawal request processed for affiliate {} for amount: {}", affiliateId, amount);

        // 🔹 Audit Log
        logAudit("REQUEST_WITHDRAWAL", "AffiliateWallet", affiliateId);
    }

    // ── Metrics ───────────────────────────────────────────────────────────────

    @Override
    public AffiliateMetricsResponse getAffiliateMetrics(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found for user: " + userId));
        return getAffiliateMetrics(affiliate);
    }

    @Override
    public AffiliateMetricsResponse getAffiliateMetricsByAffiliateId(Long affiliateId) {
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found with ID: " + affiliateId));
        return getAffiliateMetrics(affiliate);
    }

    @Override
    public AffiliateMetricsResponse getAffiliateMetrics(Affiliate affiliate) {
        if (affiliate == null)
            return new AffiliateMetricsResponse();

        Long totalLeads = leadRepository.countByAffiliateId(affiliate.getId());

        java.util.List<AffiliateSale.SaleStatus> activeStatuses = java.util.Arrays.asList(
                AffiliateSale.SaleStatus.PENDING,
                AffiliateSale.SaleStatus.APPROVED,
                AffiliateSale.SaleStatus.PAID);

        Long converted = saleRepository.countByAffiliateIdAndStatusIn(affiliate.getId(), activeStatuses);

        BigDecimal totalRevenue = saleRepository.sumRevenueByAffiliateIdAndStatuses(affiliate.getId(), activeStatuses);
        if (totalRevenue == null)
            totalRevenue = BigDecimal.ZERO;

        BigDecimal earned = saleRepository.sumCommissionByAffiliateIdAndStatuses(affiliate.getId(), activeStatuses);
        if (earned == null)
            earned = BigDecimal.ZERO;

        BigDecimal balance = walletRepository.findByAffiliateId(affiliate.getId())
                .map(w -> w.getAvailableBalance() != null ? w.getAvailableBalance() : BigDecimal.ZERO)
                .orElse(BigDecimal.ZERO);

        double conversionRateVal = totalLeads > 0 ? (converted.doubleValue() / totalLeads) * 100 : 0.0;

        return AffiliateMetricsResponse.builder()
                .totalLeads(totalLeads)
                .convertedLeads(converted)
                .conversionRate(conversionRateVal)
                .totalEarned(earned)
                .walletBalance(balance)
                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    public List<AffiliateLead> getAffiliateLeads(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found for user: " + userId));
        return leadRepository.findByAffiliateOrderByCreatedAtDesc(affiliate);
    }

    @Override
    @Transactional
    public AffiliateDashboardResponse getDashboardDetails(Long userId, String email) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId).orElse(null);

        if (affiliate == null) {
            log.error("[AffiliateService] Dashboard error: No affiliate profile found for userId: {}", userId);
            throw new RuntimeException("Affiliate profile not found for user: " + userId);
        }

        log.info("--- Dashboard Debug ---");
        log.info("Affiliate ID: {}", affiliate.getId());
        log.info("Total Leads: {}", leadRepository.countByAffiliateId(affiliate.getId()));
        log.info("Total Sales: {}", saleRepository.countByAffiliateId(affiliate.getId()));

        java.util.List<AffiliateSale.SaleStatus> debugStatuses = java.util.Arrays.asList(
                AffiliateSale.SaleStatus.PENDING,
                AffiliateSale.SaleStatus.APPROVED,
                AffiliateSale.SaleStatus.PAID);
        log.info("Revenue (PENDING+APPROVED+PAID): {}", saleRepository.sumRevenueByAffiliateIdAndStatuses(
                affiliate.getId(), debugStatuses));
        log.info("----------------------");

        AffiliateMetricsResponse metrics = getAffiliateMetrics(affiliate);

        // Count all clicks across BOTH the global code and all link-specific codes
        java.util.List<String> codes = new java.util.ArrayList<>();
        if (affiliate.getReferralCode() != null) {
            codes.add(affiliate.getReferralCode());
        }

        java.util.List<AffiliateLink> links = linkRepository.findByAffiliate(affiliate);
        for (AffiliateLink link : links) {
            if (link.getReferralCode() != null)
                codes.add(link.getReferralCode());
        }

        long totalClicks = clickRepository.countByAffiliateCodeIn(codes);
        log.info("[AffiliateService] Aggregating clicks for affiliate {} (Codes: {}) - Found: {}",
                affiliate.getId(), codes, totalClicks);

        // Fetch recent activity (last 5 leads)
        List<AffiliateLeadDTO> recentActivity = leadRepository.findByAffiliateOrderByCreatedAtDesc(affiliate).stream()
                .limit(5)
                .map(this::mapToLeadDTO)
                .collect(Collectors.toList());

        // Map to Dashboard DTO
        return AffiliateDashboardResponse.builder()
                .referralCode(affiliate.getReferralCode() != null && !affiliate.getReferralCode().isEmpty()
                        ? affiliate.getReferralCode()
                        : "N/A")
                .totalReferrals(metrics.getTotalLeads() != null ? metrics.getTotalLeads() : 0L)
                .totalClicks(totalClicks)
                .purchases(metrics.getConvertedLeads() != null ? metrics.getConvertedLeads() : 0L)
                .totalEarnings(metrics.getTotalEarned() != null ? metrics.getTotalEarned() : java.math.BigDecimal.ZERO)
                .totalRevenue(metrics.getTotalRevenue() != null ? metrics.getTotalRevenue() : java.math.BigDecimal.ZERO)
                .walletBalance(
                        metrics.getWalletBalance() != null ? metrics.getWalletBalance() : java.math.BigDecimal.ZERO)
                .name(affiliate.getName() != null && !affiliate.getName().isEmpty() ? affiliate.getName()
                        : (affiliate.getUsername() != null ? affiliate.getUsername() : "Partner"))
                .email(affiliate.getEmail())
                .status(affiliate.getStatus() != null ? affiliate.getStatus().name() : "ACTIVE")
                .recentActivity(recentActivity)
                .activeLinks(getAffiliateLinks(affiliate.getId()))
                .bankName(affiliate.getBankName())
                .accountNumber(affiliate.getAccountNumber())
                .ifscCode(affiliate.getIfscCode())
                .accountHolderName(affiliate.getAccountHolderName())
                .upiId(affiliate.getUpiId())
                .build();
    }

    @Override
    @Transactional
    public void updateBankDetails(Long userId, com.lms.www.affiliate.dto.BankDetailsDTO bankInfo) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found for user: " + userId));

        affiliate.setBankName(bankInfo.getBankName());
        affiliate.setAccountNumber(bankInfo.getAccountNumber());
        affiliate.setIfscCode(bankInfo.getIfscCode());
        affiliate.setAccountHolderName(bankInfo.getAccountHolderName());
        affiliate.setUpiId(bankInfo.getUpiId());

        affiliateRepository.save(affiliate);
        log.info("Bank details updated for affiliate user: {}", userId);
    }

    @Override
    public AffiliateDashboardResponse getDashboardDetailsSecure() {
        Long userId = userContext.getCurrentUserId();
        String email = userContext.getCurrentUserEmail();
        return getDashboardDetails(userId, email);
    }

    @Override
    public Optional<AffiliateDTO> getProfileSecure() {
        Long userId = userContext.getCurrentUserId();
        return getAffiliateByUserId(userId).map(this::mapToAffiliateDTO);
    }

    @Override
    public void updateBankDetailsSecure(BankDetailsDTO bankInfo) {
        Long userId = userContext.getCurrentUserId();
        updateBankDetails(userId, bankInfo);
    }

    @Override
    public List<AffiliateLinkDTO> getAffiliateLinksSecure() {
        Long userId = userContext.getCurrentUserId();
        return getAffiliateLinks(userId);
    }

    @Override
    public List<AffiliateLead> getAffiliateLeadsSecure() {
        Long userId = userContext.getCurrentUserId();
        return getAffiliateLeads(userId);
    }

    @Override
    public AffiliateMetricsResponse getAffiliateMetricsSecure() {
        Long userId = userContext.getCurrentUserId();
        return getAffiliateMetrics(userId);
    }

    @Override
    @Transactional
    public Affiliate registerStudentAsAffiliateSecure(RegisterAffiliateRequest request) {
        request.setUserId(userContext.getCurrentUserId());
        return registerStudentAsAffiliate(request);
    }

    private AffiliateDTO mapToAffiliateDTO(Affiliate a) {
        return AffiliateDTO.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .type(a.getType().name())
                .code(a.getReferralCode())
                .referralCode(a.getReferralCode())
                .name(a.getName())
                .username(a.getUsername())
                .email(a.getEmail())
                .mobile(a.getMobile())
                .status(a.getStatus().name())
                .commissionType(a.getCommissionType().name())
                .commissionValue(a.getCommissionValue())
                .studentDiscountValue(a.getStudentDiscountValue())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private AffiliateLeadDTO mapToLeadDTO(AffiliateLead lead) {
        return AffiliateLeadDTO.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .mobile(lead.getMobile())
                .courseId(lead.getCourseId())
                .batchId(lead.getBatchId())
                .status(lead.getStatus() != null ? lead.getStatus().name() : null)
                .createdAt(lead.getCreatedAt())
                .build();
    }

    private void logAudit(String action, String entityName, Long entityId) {
        try {
            Long currentUserId = userContext.getCurrentUserId();
            if (currentUserId != null) {
                User user = userRepository.findById(currentUserId).orElse(null);
                if (user != null) {
                    String ip = httpServletRequest.getRemoteAddr();
                    genericAuditLogService.log(action, entityName, entityId, user, ip);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to log audit event: {}", e.getMessage());
        }
    }
}
