package com.lms.www.affiliate.controller;

import com.lms.www.affiliate.dto.*;
import com.lms.www.affiliate.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/affiliate")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AffiliatePortalController {

    private final SaleService saleService;
    private final AffiliateLeadService leadService;
    private final AffiliateService affiliateService;

    // ================= DASHBOARD =================


    @GetMapping("/me/dashboard")
    @PreAuthorize("hasAuthority('AFFILIATE_PORTAL_VIEW')")
    public ResponseEntity<AffiliateDashboardResponse> getDashboard() {
        return ResponseEntity.ok(affiliateService.getDashboardDetailsSecure());
    }

    // ================= PROFILE =================

    @GetMapping("/me/profile")
    @PreAuthorize("hasAuthority('AFFILIATE_PORTAL_VIEW')")
    public ResponseEntity<AffiliateDTO> getProfile() {
        return affiliateService.getProfileSecure()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/me/bank-details")
    @PreAuthorize("hasAuthority('AFFILIATE_BANK_UPDATE')")
    public ResponseEntity<Void> updateBankDetails(@Valid @RequestBody BankDetailsDTO bankInfo) {
        affiliateService.updateBankDetailsSecure(bankInfo);
        return ResponseEntity.ok().build();
    }

    // ================= SALES =================

    @GetMapping("/me/sales")
    @PreAuthorize("hasAuthority('AFFILIATE_SALE_VIEW_OWN')")
    public ResponseEntity<List<AffiliateSaleDTO>> getSales() {
        return ResponseEntity.ok(saleService.getSalesSecure());
    }

    // ================= LEADS =================

    @GetMapping("/me/leads")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_VIEW_OWN')")
    public ResponseEntity<List<AffiliateLeadDTO>> getLeads() {
        return ResponseEntity.ok(leadService.getLeadsSecure());
    }

    // ================= LINKS =================

    @GetMapping("/me/links")
    @PreAuthorize("hasAuthority('AFFILIATE_PORTAL_VIEW')")
    public ResponseEntity<List<AffiliateLinkDTO>> getMyLinks() {
        return ResponseEntity.ok(affiliateService.getAffiliateLinksSecure());
    }

    // Removal of redundant mapToDTO as it's now handled in the service/mapper
}