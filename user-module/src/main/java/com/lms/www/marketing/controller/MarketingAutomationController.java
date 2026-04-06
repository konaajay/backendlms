package com.lms.www.marketing.controller;

import com.lms.www.marketing.service.MarketingAutomationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/marketing/automation")
@RequiredArgsConstructor
public class MarketingAutomationController {

    private final MarketingAutomationService automationService;

    // =========================
    // TRIGGER WELCOME
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_AUTOMATION_EXECUTE')")
    @PostMapping("/welcome/{userId}")
    public ResponseEntity<?> triggerWelcome(@PathVariable Long userId) {

        automationService.triggerWelcomeReward(userId);

        return ResponseEntity.ok(Map.of(
                "message", "Welcome reward triggered",
                "userId", userId));
    }

    // =========================
    // TRIGGER EVENT
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_AUTOMATION_EXECUTE')")
    @PostMapping("/event")
    public ResponseEntity<?> triggerEvent(@RequestParam Long userId,
            @RequestParam String eventType,
            @RequestParam String ref) {

        automationService.triggerEngagementReward(userId, eventType, ref);

        return ResponseEntity.ok(Map.of(
                "message", "Event reward triggered",
                "eventType", eventType));
    }
}