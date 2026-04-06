package com.lms.www.affiliate.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.affiliate.dto.*;
import com.lms.www.affiliate.entity.*;
import com.lms.www.affiliate.service.*;

@RestController
@RequestMapping("/api/v1/admin/affiliates")
@CrossOrigin(origins = "*")
public class AffiliateAdminController {

    private final AffiliateService affiliateService;
    private final AffiliateLeadService leadService;
    private final SaleService saleService;

    public AffiliateAdminController(
            AffiliateService affiliateService,
            AffiliateLeadService leadService,
            SaleService saleService) {
        this.affiliateService = affiliateService;
        this.leadService = leadService;
        this.saleService = saleService;
    }

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // ================= AFFILIATE =================

    @PostMapping
    @PreAuthorize("hasAuthority('AFFILIATE_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Affiliate> createAffiliate(@Valid @RequestBody CreateAffiliateRequest request) {
        return ResponseEntity.ok(affiliateService.createAffiliate(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('AFFILIATE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AffiliateAdminResponse>> getAllAffiliates() {
        return ResponseEntity.ok(affiliateService.getAllAffiliatesWithMetrics());
    }

    @PutMapping("/{id}/settings")
    @PreAuthorize("hasAuthority('AFFILIATE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Affiliate> updateAffiliateSettings(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAffiliateSettingsRequest request) {
        return ResponseEntity.ok(affiliateService.updateAffiliateSettings(id, request));
    }

    // ================= METRICS & LINKS (Per-Affiliate) =================

    @GetMapping("/{id}/metrics")
    @PreAuthorize("hasAuthority('AFFILIATE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<com.lms.www.affiliate.dto.AffiliateMetricsResponse> getAffiliateMetrics(@PathVariable Long id) {
        return ResponseEntity.ok(affiliateService.getAffiliateMetricsByAffiliateId(id));
    }

    @GetMapping("/{id}/links")
    @PreAuthorize("hasAuthority('AFFILIATE_LINK_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<com.lms.www.affiliate.dto.AffiliateLinkDTO>> getAffiliateLinks(@PathVariable Long id) {
        return ResponseEntity.ok(affiliateService.getAffiliateLinksByAffiliateId(id));
    }

    // ================= LINKS =================

    @PostMapping("/links")
    @PreAuthorize("hasAuthority('AFFILIATE_LINK_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateLinkResponse> generateLink(@Valid @RequestBody GenerateLinkRequest request) {

        AffiliateLink link = affiliateService.generateLink(
                request.getAffiliateId(),
                request.getCourseId(),
                request.getBatchId(),
                request.getCommissionValue(),
                request.getStudentDiscountValue(),
                null,
                request.getExpiresAt());

        String linkUrl = frontendUrl + "/course-overview/" + request.getCourseId() + "?ref=" + link.getReferralCode() + "&batchId=" + request.getBatchId();

        return ResponseEntity.ok(
                AffiliateLinkResponse.builder()
                        .link(linkUrl)
                        .affiliateCode(link.getReferralCode())
                        .batchId(request.getBatchId())
                        .build());
    }

    // ================= LEADS =================

    @GetMapping("/leads")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AffiliateLeadDTO>> getLeads() {
        return ResponseEntity.ok(leadService.getAdminLeads());
    }

    @PostMapping("/leads/{id}/status")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateLeadDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeadStatusRequest request) {

        return ResponseEntity.ok(
                leadService.updateLeadStatus(id,
                        request.getStatus(),
                        request.getChangedBy(),
                        request.getReason()));
    }

    @PostMapping("/leads/{id}/notes")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_NOTE_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateLeadDTO> addNote(
            @PathVariable Long id,
            @Valid @RequestBody LeadNoteRequest request) {

        return ResponseEntity.ok(
                leadService.addLeadNote(id,
                        request.getNote(),
                        request.getCreatedBy()));
    }

    @PostMapping("/leads/{id}/onboard")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateLeadDTO> onboardLead(
            @PathVariable Long id,
            jakarta.servlet.http.HttpServletRequest request) {
        return ResponseEntity.ok(leadService.convertToStudent(id, request));
    }

    @GetMapping("/leads/{id}/notes")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<LeadNoteDTO>> getNotes(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadNotes(id));
    }

    @GetMapping("/leads/by-student/{studentId}")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateLeadDTO> getLeadByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(leadService.getLeadByStudentId(studentId));
    }

    @GetMapping("/leads/by-email")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateLeadDTO> getLeadByEmail(
            @RequestParam String email,
            @RequestParam Long batchId) {

        return ResponseEntity.ok(
                leadService.getLeadByEmailAndBatch(email, batchId));
    }

    @GetMapping("/leads/export")
    @PreAuthorize("hasAuthority('AFFILIATE_LEAD_EXPORT') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> exportLeads() {
        return ResponseEntity.ok(leadService.exportLeadsCsv());
    }

    // ================= SALES =================

    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('AFFILIATE_SALE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AffiliateAdminSaleResponse>> getSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @PostMapping("/leads/{id}/convert")
    @PreAuthorize("hasAuthority('AFFILIATE_SALE_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AffiliateSale> convert(
            @PathVariable Long id,
            @Valid @RequestBody LeadConversionRequest request) {

        return ResponseEntity.ok(
                saleService.convertLeadToEnrollment(
                        id,
                        request.getStudentId(),
                        request.getBatchPrice()));
    }

    @PostMapping("/sales/{id}/approve")
    @PreAuthorize("hasAuthority('AFFILIATE_SALE_APPROVE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> approve(
            @PathVariable Long id,
            @RequestParam String approvedBy) {

        saleService.approveCommission(id, approvedBy);
        return ResponseEntity.ok("Commission approved");
    }

    @PostMapping("/sales/{id}/cancel")
    @PreAuthorize("hasAuthority('AFFILIATE_SALE_CANCEL') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> cancel(
            @PathVariable Long id,
            @RequestParam String reason) {

        saleService.cancelSale(id, reason);
        return ResponseEntity.ok("Sale cancelled");
    }

    @PostMapping("/sales/{id}/pay")
    @PreAuthorize("hasAuthority('AFFILIATE_SALE_PAY') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> markAsPaid(
            @PathVariable Long id,
            @RequestParam String referenceId) {

        saleService.markSaleAsPaid(id, referenceId);
        return ResponseEntity.ok("Marked as paid");
    }

    @GetMapping("/sales/export")
    @PreAuthorize("hasAuthority('AFFILIATE_SALE_EXPORT') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> exportSales() {
        return ResponseEntity.ok(saleService.exportSalesCsv());
    }

    // ================= TRACKING =================

    @PostMapping("/lead")
    public ResponseEntity<AffiliateLeadDTO> submitLead(@Valid @RequestBody CreateLeadRequest request) {
        return ResponseEntity.ok(leadService.createLead(
                request.getName(),
                request.getMobile(),
                request.getEmail(),
                request.getCourseId(),
                request.getBatchId(),
                request.getReferralCode(),
                request.getIpAddress()));
    }

    @PostMapping("/click")
    public ResponseEntity<Void> trackClick(@RequestBody TrackClickRequest request) {
        affiliateService.trackClick(
                request.getAffiliateCode(),
                request.getBatchId(),
                request.getIpAddress(),
                request.getUserAgent());
        return ResponseEntity.ok().build();
    }
}