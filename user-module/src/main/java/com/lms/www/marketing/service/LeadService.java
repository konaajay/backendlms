package com.lms.www.marketing.service;

import com.lms.www.marketing.dto.LeadCaptureRequest;
import com.lms.www.marketing.model.Lead;
import com.lms.www.marketing.repository.LeadRepository;
import com.lms.www.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * LeadService - handles lead capture and retrieval.
 * Controllers route lead capture through this service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;
    private final CommunityService communityService;
    private final java.util.Map<String, LocalDateTime> rateLimitMap = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Captures a lead from a public form or tracked link, tagged with the caller's IP.
     * Used by UnifiedLeadController.
     */
    public Lead captureLead(LeadCaptureRequest request, String ipAddress) {
        // Rate limiting
        String mobile = request.getMobile() != null ? request.getMobile() : "NO_MOBILE";
        String email = request.getEmail() != null ? request.getEmail() : "NO_EMAIL";
        String rateLimitKey = (ipAddress != null ? ipAddress : "NO_IP") + "_" + mobile + "_" + email;
        LocalDateTime lastRequest = rateLimitMap.get(rateLimitKey);
        if (lastRequest != null && lastRequest.plusMinutes(1).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Too many requests. Please try again after a minute.");
        }
        rateLimitMap.put(rateLimitKey, LocalDateTime.now());

        // Duplicate Check
        java.util.Optional<Lead> existing = leadRepository.findByEmailAndBatchId(request.getEmail(), request.getBatchId());
        if (existing.isPresent()) {
            log.warn("[MarketingLeadService] DUPLICATE LEAD: Email {} for batch {}", request.getEmail(), request.getBatchId());
            return existing.get();
        }

        Lead lead = Lead.builder()
                .name(request.getName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .phone(request.getMobile()) // sync phone for legacy data
                .courseId(request.getCourseId())
                .batchId(request.getBatchId())
                .source(request.getSource())
                .utmSource(request.getUtmSource())
                .utmMedium(request.getUtmMedium())
                .utmCampaign(request.getUtmCampaign())
                .utmContent(request.getUtmContent())
                .ipAddress(ipAddress)
                .createdAt(LocalDateTime.now())
                .build();

        Lead savedLead = leadRepository.save(lead);

        // Trigger community join asynchronously
        final Long finalSavedLeadId = savedLead.getId();
        final Long finalCourseIdForAsync = savedLead.getCourseId();
        final Long finalBatchIdForAsync = savedLead.getBatchId();
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                communityService.addLeadToCommunity(finalSavedLeadId, finalCourseIdForAsync, finalBatchIdForAsync);
            } catch (Exception e) {
                log.error("Failed to add marketing lead to community: {}", e.getMessage());
            }
        });

        return savedLead;
    }

    /**
     * Get all captured leads.
     */
    public java.util.List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }
}
