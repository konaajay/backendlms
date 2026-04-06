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

    /**
     * Captures a lead from a public form or tracked link, tagged with the caller's IP.
     * Used by UnifiedLeadController.
     */
    public Lead captureLead(LeadCaptureRequest request, String ipAddress) {
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

        // Trigger community join
        try {
            communityService.addLeadToCommunity(savedLead.getId(), savedLead.getCourseId(), savedLead.getBatchId());
        } catch (Exception e) {
            log.error("Failed to add marketing lead to community: {}", e.getMessage());
        }

        return savedLead;
    }

    /**
     * Get all captured leads.
     */
    public java.util.List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }
}
