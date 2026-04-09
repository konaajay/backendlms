package com.lms.www.marketing.controller;

import com.lms.www.marketing.service.MarketingService;
import com.lms.www.affiliate.service.AffiliateLeadService;
import com.lms.www.affiliate.dto.AffiliateLeadDTO;
import jakarta.servlet.http.HttpServletRequest;
import com.lms.www.affiliate.util.NetworkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping("/api/v1/marketing/public/leads")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicLeadController {

    private static final Logger log = LoggerFactory.getLogger(PublicLeadController.class);

    private final MarketingService marketingService;
    private final AffiliateLeadService affiliateLeadService;

    @PostMapping
    public ResponseEntity<Void> captureLead(@RequestBody LeadRequest request, HttpServletRequest httpRequest) {
        String ipAddress = NetworkUtils.getClientIp(httpRequest);
        
        String refCode = request.referralCode() != null && !request.referralCode().isBlank() ? request.referralCode() : request.affiliateCode();

        if (refCode != null && refCode.startsWith("AFF-")) {
            // It's an affiliate lead. Do NOT save in marketing. Save only in Affiliate module.
            // And auto-enroll student.
            AffiliateLeadDTO leadDto = affiliateLeadService.createLead(
                    request.name(),
                    request.phone(),
                    request.email(),
                    request.courseId(),
                    request.batchId(),
                    refCode,
                    ipAddress
            );
            
            try {
                affiliateLeadService.convertToStudent(leadDto.getId(), httpRequest);
                log.info("Successfully created student and enrolled for affiliate lead: {}", leadDto.getId());
            } catch (Exception e) {
                log.error("Failed to auto-enrolled affiliate lead", e);
            }
        } else {
            marketingService.createMarketingLead(
                    request.name(),
                    request.email(),
                    request.phone(),
                    request.courseInterest(),
                    request.utmSource(),
                    request.utmMedium(),
                    request.utmCampaign(),
                    request.referralCode()
            );
        }
        return ResponseEntity.ok().build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LeadRequest(
            String name,
            String email,
            String phone,
            String courseInterest,
            String utmSource,
            String utmMedium,
            String utmCampaign,
            String referralCode,
            String affiliateCode,
            Long courseId,
            Long batchId
    ) {}
}
