package com.lms.www.fee.admin.controller;

import com.lms.www.fee.admin.service.AdminSettingsService;
import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.MasterSettingsRequest;
import com.lms.www.fee.dto.SystemSettingsResponse;
import com.lms.www.fee.enums.MasterSettingType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final AdminSettingsService settingsService;

    // ================= GLOBAL CONFIG =================

    @PostMapping("/config")
    @PreAuthorize("hasAuthority('CONFIG_UPDATE')")
    public ResponseEntity<Void> updateGlobalConfig(@RequestParam String key, @RequestParam String value) {
        settingsService.updateGlobalConfig(key, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/config/{key}")
    @PreAuthorize("hasAuthority('CONFIG_VIEW')")
    public ResponseEntity<String> getGlobalConfig(@PathVariable String key) {
        return ResponseEntity.ok(settingsService.getGlobalConfig(key));
    }

    // ================= MASTER SETTINGS (SINGLE) =================

    @PostMapping("/master")
    @PreAuthorize("hasAuthority('SETTING_CREATE')")
    public ResponseEntity<MasterSettingResponse> upsertMasterSetting(
            @RequestParam MasterSettingType type,
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(settingsService.upsertSetting(type, key, value, description));
    }

    @GetMapping("/master/{type}")
    @PreAuthorize("hasAuthority('SETTING_VIEW')")
    public ResponseEntity<List<MasterSettingResponse>> getSettingsByType(@PathVariable MasterSettingType type) {
        return ResponseEntity.ok(settingsService.getSettingsByType(type));
    }

    // ================= FULL SETTINGS (BULK) =================

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('SETTING_CREATE')")
    public ResponseEntity<Map<String, String>> updateAllSettings(@Valid @RequestBody MasterSettingsRequest settings) {
        settingsService.updateAllSettings(settings);
        return ResponseEntity.ok(Map.of("status", "success", "message", "All settings updated"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SETTING_VIEW')")
    public ResponseEntity<List<MasterSettingResponse>> getAllActiveSettings() {
        return ResponseEntity.ok(settingsService.getSettingsByActive());
    }

    // ================= SYSTEM CONFIG (HELPERS) =================

    @GetMapping("/system")
    @PreAuthorize("hasAuthority('SETTING_VIEW')")
    public ResponseEntity<SystemSettingsResponse> getSystemSettings() {
        SystemSettingsResponse response = new SystemSettingsResponse();
        response.setPaymentLinkDays(settingsService.getPaymentLinkDaysBeforeDue());
        response.setReminderOffsets(settingsService.getReminderOffsets());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/system/payment-link-days")
    @PreAuthorize("hasAuthority('SETTING_CREATE')")
    public ResponseEntity<Map<String, Object>> setPaymentLinkDays(@RequestBody Map<String, Integer> body) {
        Integer days = body.get("days");
        if (days == null) return ResponseEntity.badRequest().body(Map.of("error", "Missing days"));
        settingsService.setPaymentLinkDaysBeforeDue(days);
        return ResponseEntity.ok(Map.of("success", true, "days", days));
    }

    @PostMapping("/system/reminder-offsets")
    @PreAuthorize("hasAuthority('SETTING_CREATE')")
    public ResponseEntity<Map<String, Object>> setReminderOffsets(@RequestBody Map<String, List<Integer>> body) {
        List<Integer> offsets = body.get("offsets");
        if (offsets == null) return ResponseEntity.badRequest().body(Map.of("error", "Missing offsets"));
        settingsService.setReminderOffsets(offsets);
        return ResponseEntity.ok(Map.of("success", true, "offsets", offsets));
    }
}
