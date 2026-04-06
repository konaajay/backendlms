package com.lms.www.affiliate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lms.www.affiliate.dto.*;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateLead;
import com.lms.www.affiliate.entity.AffiliateLink;

public interface AffiliateService {
    Affiliate createAffiliate(CreateAffiliateRequest request);
    Affiliate updateAffiliateSettings(Long affiliateId, UpdateAffiliateSettingsRequest request);
    Optional<Affiliate> getAffiliateById(Long id);
    Optional<Affiliate> getAffiliateByUserId(Long userId);
    Optional<Affiliate> getAffiliateByCode(String code);
    List<Affiliate> getAllAffiliates();
    List<AffiliateAdminResponse> getAllAffiliatesWithMetrics();

    // ── Wallet Config ──────────────────────────────────────────────────────────
    com.lms.www.affiliate.entity.WalletConfig getWalletConfig();
    com.lms.www.affiliate.entity.WalletConfig updateWalletConfig(com.lms.www.affiliate.entity.WalletConfig config);

    AffiliateLink generateLink(Long affiliateId, Long courseId, Long batchId,
            java.math.BigDecimal commission, java.math.BigDecimal discount, String customCode, LocalDateTime expiresAt);
    Optional<AffiliateLink> getLinkByReferralCode(String code);
    List<AffiliateLinkDTO> getAffiliateLinks(Long userId);
    List<AffiliateLinkDTO> getAffiliateLinksByAffiliateId(Long affiliateId);

    void trackClick(String affiliateCode, Long batchId, String ipAddress, String userAgent);

    AffiliateMetricsResponse getAffiliateMetrics(Long userId);
    AffiliateMetricsResponse getAffiliateMetricsByAffiliateId(Long affiliateId);
    AffiliateMetricsResponse getAffiliateMetrics(Affiliate affiliate);
    List<AffiliateLead> getAffiliateLeads(Long userId);

    AffiliateDashboardResponse getDashboardDetails(Long userId, String email);

    AffiliateLead createLead(String name, String mobile, String email, Long courseId, Long batchId, String referralCode, String ipAddress);

    Affiliate registerStudentAsAffiliate(com.lms.www.affiliate.dto.RegisterAffiliateRequest request);

    void requestWithdrawal(Long affiliateId, java.math.BigDecimal amount);

    void processCommission(Long buyerUserId, String referralCode, java.math.BigDecimal coursePrice, Long saleId);

    void updateBankDetails(Long userId, com.lms.www.affiliate.dto.BankDetailsDTO bankInfo);

    // Secure Methods
    AffiliateDashboardResponse getDashboardDetailsSecure();
    java.util.Optional<AffiliateDTO> getProfileSecure();
    void updateBankDetailsSecure(com.lms.www.affiliate.dto.BankDetailsDTO bankInfo);
    List<AffiliateLinkDTO> getAffiliateLinksSecure();
    List<AffiliateLead> getAffiliateLeadsSecure();
    AffiliateMetricsResponse getAffiliateMetricsSecure();
    Affiliate registerStudentAsAffiliateSecure(com.lms.www.affiliate.dto.RegisterAffiliateRequest request);
}
