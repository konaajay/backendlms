package com.lms.www.marketing.controller;

import com.lms.www.common.dto.ApiResponse;
import com.lms.www.marketing.dto.LeadCaptureRequest;
import com.lms.www.marketing.model.Lead;
import com.lms.www.marketing.service.LeadService;
import com.lms.www.community.service.CommunityService;
import com.lms.www.affiliate.util.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UnifiedLeadController {

    private static final Logger log = LoggerFactory.getLogger(UnifiedLeadController.class);

    private final LeadService leadService;
    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> captureLead(
            @Valid @RequestBody LeadCaptureRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = NetworkUtils.getClientIp(httpRequest);

        // Avoid logging full email (PII safe)
        log.debug("Lead capture request received");

        Lead lead = leadService.captureLead(request, ipAddress);

        // Move this later to async/event (not controller)
        try {
            communityService.addLeadToCommunity(
                    lead.getId(),
                    request.getCourseId(),
                    request.getBatchId());
        } catch (Exception e) {
            log.error("Community join failed for leadId={}", lead.getId());
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        Map.of("leadId", lead.getId()),
                        "Lead captured successfully"));
    }
}