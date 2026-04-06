package com.lms.www.affiliate.controller;

import com.lms.www.affiliate.dto.AffiliateLeadDTO;
import com.lms.www.affiliate.dto.CreateLeadRequest;
import com.lms.www.affiliate.service.AffiliateLeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/affiliates")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AffiliatePublicController {

    private final AffiliateLeadService affiliateLeadService;

    @PostMapping("/lead")
    public ResponseEntity<AffiliateLeadDTO> submitPublicLead(@Valid @RequestBody CreateLeadRequest request) {
        log.info("[PublicAffiliate] Received public lead submission for email: {}", request.getEmail());
        try {
            AffiliateLeadDTO lead = affiliateLeadService.createLead(
                    request.getName(),
                    request.getMobile(),
                    request.getEmail(),
                    request.getCourseId(),
                    request.getBatchId(),
                    request.getReferralCode(),
                    null // IP address can be extracted from request if needed
            );
            return ResponseEntity.ok(lead);
        } catch (Exception e) {
            log.error("[PublicAffiliate] ERROR: Submission failed for {}. Message: {}", request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Lead submission failed: " + e.getMessage());
        }
    }
}
