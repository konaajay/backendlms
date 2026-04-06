package com.lms.www.affiliate.controller;

import com.lms.www.affiliate.entity.AffiliatePayout;
import com.lms.www.affiliate.service.PayoutService;
import com.lms.www.config.CustomUserDetails;

import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PayoutController {

    private final PayoutService service;

    // ===== ADMIN: VIEW ANY AFFILIATE PAYOUTS =====
    @GetMapping("/admin/{affiliateId}")
    @PreAuthorize("hasAuthority('PAYOUT_VIEW_ALL')")
    public ResponseEntity<List<AffiliatePayout>> getPayoutsByAffiliate(
            @PathVariable Long affiliateId) {

        return ResponseEntity.ok(
                service.getPayoutsByAffiliateId(affiliateId));
    }

    // ===== AFFILIATE: VIEW OWN PAYOUTS =====
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PAYOUT_VIEW_OWN')")
    public ResponseEntity<List<AffiliatePayout>> getMyPayouts(
            @AuthenticationPrincipal CustomUserDetails user) {

        return ResponseEntity.ok(
                service.getPayoutsByUserId(user.getId()));
    }

    // ===== AFFILIATE: REQUEST PAYOUT =====
    @PostMapping("/me/request")
    @PreAuthorize("hasAuthority('PAYOUT_REQUEST')")
    public ResponseEntity<AffiliatePayout> requestPayout(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam @DecimalMin("1.0") BigDecimal amount) {

        AffiliatePayout payout = service.requestPayout(user.getId(), amount);
        return ResponseEntity.ok(payout);
    }
}