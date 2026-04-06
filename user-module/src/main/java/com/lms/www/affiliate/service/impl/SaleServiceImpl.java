package com.lms.www.affiliate.service.impl;

import com.lms.www.affiliate.dto.AffiliateAdminSaleResponse;
import com.lms.www.affiliate.dto.AffiliateSaleDTO;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateLead;
import com.lms.www.affiliate.entity.AffiliateLink;
import com.lms.www.affiliate.entity.AffiliateSale;
import com.lms.www.affiliate.repository.AffiliateLeadRepository;
import com.lms.www.affiliate.repository.AffiliateLinkRepository;
import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.repository.AffiliateSaleRepository;
import com.lms.www.affiliate.service.AffiliateLeadService;
import com.lms.www.affiliate.service.CommissionEngine;
import com.lms.www.affiliate.service.ReferralService;
import com.lms.www.affiliate.service.SaleService;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleServiceImpl implements SaleService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateLinkRepository linkRepository;
    private final AffiliateLeadRepository leadRepository;
    private final AffiliateSaleRepository saleRepository;
    private final AffiliateLeadService leadService;
    private final CommissionEngine commissionEngine;
    private final ReferralService referralService;
    private final UserContext userContext;

    @Override
    @Transactional
    public AffiliateSale convertLeadToEnrollment(Long leadId, Long studentId, BigDecimal batchPrice) {
        log.info("[SaleService] Attempting to convert lead {} for student {}", leadId, studentId);

        if (leadId == null)
            throw new IllegalArgumentException("Lead ID required");
        AffiliateLead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));

        if (lead.getStatus() == AffiliateLead.LeadStatus.ENROLLED) {
            log.warn("[SaleService] Lead {} is already enrolled.", leadId);
            throw new IllegalStateException("Lead already enrolled");
        }

        if (lead.getAffiliate() == null) {
            log.error("[SaleService] Lead {} has no affiliate associated.", leadId);
            throw new IllegalStateException("No affiliate associated with this lead. Conversion aborted.");
        }

        // Idempotency: Avoid duplicate sale for same student + course
        if (saleRepository.existsByStudentIdAndCourseId(studentId, lead.getCourseId())) {
            log.warn("Duplicate sale blocked for student {} and course {}", studentId, lead.getCourseId());
            throw new IllegalStateException("Sale already exists for this enrollment");
        }

        Long affiliateId = lead.getAffiliate().getId();
        if (affiliateId == null)
            throw new RuntimeException("Affiliate ID is null for lead " + leadId);
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
                .orElseThrow(() -> new RuntimeException("Affiliate with ID " + affiliateId + " no longer exists."));

        log.info("[SaleService] Updating lead status to ENROLLED for lead {}", leadId);
        lead.setStudentId(studentId);
        leadService.updateLeadStatus(leadId, AffiliateLead.LeadStatus.ENROLLED, "SYSTEM", "Enrollment triggered");

        log.info("[SaleService] Calculating discounts and commission for lead {}", leadId);

        AffiliateLink link = null;
        if (lead.getReferralCode() != null && !lead.getReferralCode().isBlank()) {
            link = linkRepository.findByReferralCode(lead.getReferralCode()).orElse(null);
        }

        // Strict Rule: Discount ONLY from active, valid AffiliateLink
        BigDecimal discRate = BigDecimal.ZERO;
        if (link != null && link.getStatus() == AffiliateLink.LinkStatus.ACTIVE &&
                (link.getExpiresAt() == null || link.getExpiresAt().isAfter(LocalDateTime.now()))) {

            // Security: Validate link belongs to the requested batch
            if (link.getBatchId() != null && link.getBatchId().equals(lead.getBatchId())) {
                discRate = link.getStudentDiscountValue() != null ? link.getStudentDiscountValue() : BigDecimal.ZERO;
                log.info("Applying referralCode={}, discount={}%, batch={}", link.getReferralCode(), discRate,
                        lead.getBatchId());
            } else {
                log.warn("[SaleService] Link {} belongs to batch {} but lead is for batch {}. Skipping discount.",
                        link.getReferralCode(), link.getBatchId(), lead.getBatchId());
            }
        } else if (link != null) {
            log.warn("[SaleService] Link {} found but is INACTIVE or EXPIRED.", link.getReferralCode());
        }

        BigDecimal discountAmount = batchPrice
                .multiply(discRate.divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP))
                .setScale(4, RoundingMode.HALF_UP);
        BigDecimal finalOrderAmount = batchPrice.subtract(discountAmount);

        // Commission calculation using CommissionEngine
        BigDecimal commissionAmount = commissionEngine.calculateCommission(finalOrderAmount, affiliate,
                lead.getCourseId());

        log.info("[SaleService] Creating sale record for lead {}", leadId);
        AffiliateSale sale = AffiliateSale.builder()
                .affiliate(affiliate)
                .leadId(leadId)
                .studentId(studentId)
                .courseId(lead.getCourseId())
                .batchId(lead.getBatchId())
                .orderId("ENR-" + leadId + "-" + System.currentTimeMillis())
                .originalAmount(batchPrice)
                .discountAmount(discountAmount)
                .orderAmount(finalOrderAmount)
                .commissionAmount(commissionAmount)
                .status(AffiliateSale.SaleStatus.APPROVED)
                .build();

        if (sale == null)
            throw new IllegalStateException("Sale object was not built correctly");
        AffiliateSale savedSale = saleRepository.save(sale);
        log.info("[SaleService] Sale {} saved successfully. Awaiting daily commission job.", savedSale.getId());

        // Don't auto-create account anymore, student will join via dashboard if
        // interested
        log.info("[SaleService] Checking referral code for student {} (no auto-create)", studentId);
        try {
            referralService.getOrCreateReferralCode(studentId, lead.getCourseId(), false);
        } catch (Exception e) {
            log.warn("[SaleService] Non-fatal: Referral lookup skipping for student {}: {}", studentId, e.getMessage());
        }

        return savedSale;
    }

    @Override
    @Transactional
    public void approveCommission(Long saleId, String approvedBy) {
        if (saleId == null)
            throw new IllegalArgumentException("Sale ID required");
        AffiliateSale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale record not found"));

        if (sale.getStatus() != AffiliateSale.SaleStatus.PENDING) {
            throw new IllegalStateException("Commission is already " + sale.getStatus());
        }

        sale.setStatus(AffiliateSale.SaleStatus.APPROVED);
        saleRepository.save(sale);
        log.info("Commission approved for sale: {}. Will be credited in next daily job.", sale.getOrderId());
    }

    @Override
    @Transactional
    public void cancelSale(Long saleId, String reason) {
        if (saleId == null)
            throw new IllegalArgumentException("Sale ID required");
        AffiliateSale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale record not found"));

        if (sale.getStatus() == AffiliateSale.SaleStatus.CANCELLED)
            return;

        if (sale.getStatus() == AffiliateSale.SaleStatus.CREDITED) {
            log.warn("Sale {} is already CREDITED. Reversal logic not implemented.", saleId);
        }

        sale.setStatus(AffiliateSale.SaleStatus.CANCELLED);
        saleRepository.save(sale);

        Long leadId = sale.getLeadId();
        if (leadId != null) {
            leadRepository.findById(leadId).ifPresent(lead -> {
                lead.setStatus(AffiliateLead.LeadStatus.LOST);
                leadRepository.save(lead);
            });
        }
    }

    @Override
    @Transactional
    public void markSaleAsPaid(Long saleId, String referenceId) {
        if (saleId == null)
            throw new IllegalArgumentException("Sale ID required");
        AffiliateSale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Sale record not found"));

        if (sale.getStatus() != AffiliateSale.SaleStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED sales can be marked as PAID");
        }

        sale.setStatus(AffiliateSale.SaleStatus.PAID);
        saleRepository.save(sale);
        log.info("Sale {} marked as PAID manually.", saleId);
    }

    @Override
    public List<AffiliateAdminSaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(s -> AffiliateAdminSaleResponse.builder()
                        .id(s.getId())
                        .orderId(s.getOrderId())
                        .affiliateId(s.getAffiliate().getId())
                        .affiliateName(s.getAffiliate().getName())
                        .courseId(s.getCourseId())
                        .batchId(s.getBatchId())
                        .originalAmount(s.getOriginalAmount())
                        .discountAmount(s.getDiscountAmount())
                        .orderAmount(s.getOrderAmount())
                        .commissionAmount(s.getCommissionAmount())
                        .status(s.getStatus())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<AffiliateSaleDTO> getSalesByUserId(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found for userId: " + userId));
        return saleRepository.findByAffiliate(affiliate).stream()
                .map(s -> AffiliateSaleDTO.builder()
                        .orderId(s.getOrderId())
                        .courseId(s.getCourseId())
                        .batchId(s.getBatchId())
                        .orderAmount(s.getOrderAmount())
                        .commissionAmount(s.getCommissionAmount())
                        .status(s.getStatus())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<AffiliateSaleDTO> getSalesSecure() {
        Long userId = userContext.getCurrentUserId();
        return getSalesByUserId(userId);
    }

    @Override
    public String exportSalesCsv() {
        List<AffiliateSale> sales = saleRepository.findAll();
        StringBuilder csv = new StringBuilder(
                "ID,AffiliateID,LeadID,OrderID,OriginalPrice,Discount,FinalPrice,Commission,Status,CreatedAt\n");
        for (AffiliateSale sale : sales) {
            csv.append(sale.getId()).append(",")
                    .append(sale.getAffiliate().getId()).append(",")
                    .append(sale.getLeadId()).append(",")
                    .append(sale.getOrderId()).append(",")
                    .append(sale.getOriginalAmount()).append(",")
                    .append(sale.getDiscountAmount()).append(",")
                    .append(sale.getOrderAmount()).append(",")
                    .append(sale.getCommissionAmount()).append(",")
                    .append(sale.getStatus()).append(",")
                    .append(sale.getCreatedAt()).append("\n");
        }
        return csv.toString();
    }
}
