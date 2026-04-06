package com.lms.www.fee.controller;

import com.lms.www.fee.dto.SystemSettingsResponse;
import com.lms.www.fee.service.SystemSettingService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService settingService;

    @GetMapping
    @PreAuthorize("hasAuthority('SETTING_VIEW')")
    public ResponseEntity<SystemSettingsResponse> getSettings() {
        SystemSettingsResponse response = new SystemSettingsResponse();
        response.setPaymentLinkDays(settingService.getPaymentLinkDaysBeforeDue());
        response.setReminderOffsets(settingService.getReminderOffsets());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment-link-days")
    @PreAuthorize("hasAuthority('SETTING_CREATE')")
    public ResponseEntity<Map<String, Object>> setPaymentLinkDays(@RequestBody Map<String, Integer> body) {
        Integer days = body.get("days");
        if (days == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing 'days' field"));
        }
        settingService.setPaymentLinkDaysBeforeDue(days);
        return ResponseEntity.ok(Map.of("success", true, "days", days));
    }

    @PostMapping("/reminder-offsets")
    @PreAuthorize("hasAuthority('SETTING_CREATE')")
    public ResponseEntity<Map<String, Object>> setReminderOffsets(
            @RequestBody Map<String, List<Integer>> body) {
        List<Integer> offsets = body.get("offsets");
        if (offsets == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing 'offsets' field"));
        }
        settingService.setReminderOffsets(offsets);
        return ResponseEntity.ok(Map.of("success", true, "offsets", offsets));
    }
}