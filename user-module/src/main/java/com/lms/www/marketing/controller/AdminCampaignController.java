package com.lms.www.marketing.controller;

import com.lms.www.marketing.model.Campaign;
import com.lms.www.marketing.service.CampaignService;
import com.lms.www.marketing.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/marketing/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {

    private final CampaignService campaignService;
    private final LeadService leadService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaignEntities());
    }

    @GetMapping("/leads")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<com.lms.www.marketing.model.Lead>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        return ResponseEntity.status(201).body(campaignService.saveCampaign(campaign));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Campaign> updateCampaign(@PathVariable Long id, @RequestBody Campaign campaign) {
        campaign.setCampaignId(id);
        return ResponseEntity.ok(campaignService.saveCampaign(campaign));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<?> getSummary() {
        return ResponseEntity.ok(campaignService.getMarketingSummary());
    }
}
