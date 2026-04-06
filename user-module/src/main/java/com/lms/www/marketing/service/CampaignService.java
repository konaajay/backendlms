package com.lms.www.marketing.service;

import com.lms.www.marketing.dto.CampaignReportDTO;
import com.lms.www.marketing.model.Campaign;
import com.lms.www.marketing.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * CampaignService - unified campaign management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final CampaignRepository campaignRepository;

    /**
     * Get a single campaign entity by ID.
     */
    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with ID: " + id));
    }

    /**
     * Get a high-level marketing summary.
     */
    public Map<String, Object> getMarketingSummary() {
        long totalCampaigns = campaignRepository.count();
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCampaigns", totalCampaigns);
        summary.put("status", "ACTIVE");
        return summary;
    }

    /**
     * Get all campaign entities.
     */
    public List<Campaign> getAllCampaignEntities() {
        return campaignRepository.findAll();
    }

    /**
     * Save/Update a campaign.
     */
    public Campaign saveCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    /**
     * Delete a campaign.
     */
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }

    /**
     * Get a per-campaign report DTO.
     */
    public CampaignReportDTO getCampaignReport(Long campaignId) {
        Campaign campaign = getCampaignById(campaignId);
        CampaignReportDTO report = new CampaignReportDTO();
        report.setCampaignId(campaign.getCampaignId());
        report.setCampaignName(campaign.getCampaignName());
        // Map other fields as needed
        return report;
    }

    public Map<String, Object> getGlobalAnalyticsReport() {
        List<Campaign> campaigns = getAllCampaignEntities();
        Map<String, Object> report = new HashMap<>();
        report.put("campaignCount", campaigns.size());
        report.put("generatedAt", java.time.LocalDateTime.now());
        return report;
    }
}