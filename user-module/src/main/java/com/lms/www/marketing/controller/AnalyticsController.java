package com.lms.www.marketing.controller;

import com.lms.www.marketing.model.CampaignPerformance;
import com.lms.www.marketing.service.CampaignService;
import com.lms.www.marketing.service.PerformanceService;
import com.lms.www.marketing.service.MarketingAnalyticsService;
import com.lms.www.tracking.model.TrafficEvent;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/marketing/admin/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final CampaignService campaignService;
    private final PerformanceService performanceService;
    private final MarketingAnalyticsService marketingAnalyticsService;

    // =========================
    // PUBLIC TRACKING
    // =========================
    @PostMapping("/public/track")
    public ResponseEntity<?> trackEvent(@Valid @RequestBody TrafficEvent event) {
        return ResponseEntity.ok(marketingAnalyticsService.recordEvent(event));
    }

    // =========================
    // ANALYTICS (VIEW)
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_ANALYTICS_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/sources")
    public ResponseEntity<?> getSources() {
        return ResponseEntity.ok(marketingAnalyticsService.getTrafficSourceStats());
    }

    @PreAuthorize("hasAuthority('MARKETING_ANALYTICS_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/funnel")
    public ResponseEntity<?> getFunnel() {
        return ResponseEntity.ok(marketingAnalyticsService.getFunnelStats());
    }

    @PreAuthorize("hasAuthority('MARKETING_ANALYTICS_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/conversion-rate")
    public ResponseEntity<?> getConversionRate() {
        return ResponseEntity.ok(marketingAnalyticsService.getConversionRate());
    }

    @PreAuthorize("hasAuthority('MARKETING_ANALYTICS_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/campaigns")
    public ResponseEntity<?> getCampaignStats() {
        return ResponseEntity.ok(marketingAnalyticsService.getCampaignStats());
    }

    @PreAuthorize("hasAuthority('MARKETING_ANALYTICS_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/mediums")
    public ResponseEntity<?> getMediums() {
        return ResponseEntity.ok(marketingAnalyticsService.getMediumStats());
    }

    // =========================
    // CAMPAIGN (VIEW)
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_CAMPAIGN_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/campaign/{id}")
    public ResponseEntity<?> getCampaignAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @PreAuthorize("hasAuthority('MARKETING_CAMPAIGN_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/campaign/{id}/performance")
    public ResponseEntity<List<CampaignPerformance>> getPerformance(@PathVariable Long id) {
        return ResponseEntity.ok(performanceService.getByCampaign(id));
    }

    // =========================
    // SUMMARY
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_ANALYTICS_SUMMARY') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        return ResponseEntity.ok(campaignService.getMarketingSummary());
    }
}