package com.lms.www.affiliate.controller;

import com.lms.www.affiliate.dto.*;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.CommissionType;
import com.lms.www.affiliate.service.AffiliateService;
import com.lms.www.affiliate.service.ReferralService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.affiliate.entity.AffiliateLead;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/student/affiliate")
public class StudentAffiliateController {

    private final AffiliateService affiliateService;
    private final ReferralService referralService;

    public StudentAffiliateController(AffiliateService affiliateService, ReferralService referralService) {
        this.affiliateService = affiliateService;
        this.referralService = referralService;
    }

    // ===== DASHBOARD (Consolidated View) =====
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_VIEW')")
    public ResponseEntity<AffiliateDashboardResponse> getDashboard() {
        return ResponseEntity.ok(affiliateService.getDashboardDetailsSecure());
    }

    // ===== ACCOUNT VIEW (Account details & Wallet info) =====
    @GetMapping("/account")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_VIEW')")
    public ResponseEntity<AffiliateDashboardResponse> getAccountView() {
        return getDashboard();
    }

    // ===== MARKETING MANAGER VIEW (Leads & Metrics) =====
    @GetMapping("/marketing-manager")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_VIEW')")
    public ResponseEntity<List<AffiliateLeadDTO>> getMarketingManagerView() {
        return ResponseEntity.ok(affiliateService.getAffiliateLeadsSecure().stream()
                .map(this::mapToLeadDTO)
                .collect(Collectors.toList()));
    }

    // ===== PROFILE =====
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_VIEW')")
    public ResponseEntity<StudentAffiliateResponse> getMyAffiliateProfile() {
        StudentAffiliateResponse response = affiliateService.getProfileSecure()
                .map(dto -> StudentAffiliateResponse.builder()
                        .id(dto.getId())
                        .userId(dto.getUserId())
                        .name(dto.getName())
                        .code(dto.getCode())
                        .referralCode(dto.getReferralCode())
                        .commissionValue(dto.getCommissionValue())
                        .commissionType(dto.getCommissionType() != null ? CommissionType.valueOf(dto.getCommissionType().toUpperCase()) : null)
                        .status(dto.getStatus())
                        .build())
                .orElse(null);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // ===== PURCHASED COURSES (Available for Referral) =====
    @GetMapping("/me/purchased-courses")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_VIEW')")
    public ResponseEntity<List<PurchasedCourseResponse>> getMyPurchasedCourses() {
        return ResponseEntity.ok(referralService.getPurchasedCoursesSecure());
    }

    // ===== REGISTER =====
    @PostMapping("/register")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_REGISTER')")
    public ResponseEntity<StudentAffiliateResponse> registerAffiliate(
            @Valid @RequestBody RegisterAffiliateRequest request) {
        Affiliate affiliate = affiliateService.registerStudentAsAffiliateSecure(request);
        return ResponseEntity.ok(mapToResponse(affiliate));
    }

    // ===== JOIN PROGRAM =====
    @PostMapping("/me/join")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_JOIN')")
    public ResponseEntity<String> joinReferralProgram(@RequestParam Long courseId) {
        String code = referralService.getOrCreateReferralCodeSecure(courseId, true);
        if (code == null) {
            return ResponseEntity.badRequest().body("Purchase required for this course");
        }
        return ResponseEntity.ok(code);
    }

    // ===== GET REFERRAL CODE =====
    @GetMapping("/me/referral-code")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_VIEW')")
    public ResponseEntity<String> getReferralCode(@RequestParam Long courseId) {
        String code = referralService.getOrCreateReferralCodeSecure(courseId, false);
        if (code == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(code);
    }

    // ===== METRICS =====
    @GetMapping("/me/metrics")
    @PreAuthorize("hasAuthority('STUDENT_AFFILIATE_VIEW')")
    public ResponseEntity<AffiliateMetricsResponse> getMyMetrics() {
        return ResponseEntity.ok(affiliateService.getAffiliateMetricsSecure());
    }

    // ===== HELPERS =====
    private StudentAffiliateResponse mapToResponse(Affiliate a) {
        return StudentAffiliateResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .name(a.getName())
                .code(a.getReferralCode())
                .referralCode(a.getReferralCode())
                .commissionValue(a.getCommissionValue())
                .commissionType(a.getCommissionType())
                .status(a.getStatus() != null ? a.getStatus().name() : null)
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
                .status(lead.getStatus() != null ? lead.getStatus().name() : "NEW")
                .createdAt(lead.getCreatedAt())
                .build();
    }
}