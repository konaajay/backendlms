package com.lms.www.fee.controller;

import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.MasterSettingsRequest;
import com.lms.www.fee.service.MasterSettingsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fee-management/master-settings")
@RequiredArgsConstructor
public class MasterSettingsController {

    private final MasterSettingsService masterSettingsService;

    // ================= GLOBAL SETTINGS =================

    @PostMapping("/global")
    @PreAuthorize("hasAuthority('MASTER_SETTINGS_GLOBAL_SAVE')")
    public ResponseEntity<MasterSettingResponse> saveGlobalConfig(
            @RequestParam String key,
            @RequestParam String value) {
        return ResponseEntity.ok(masterSettingsService.saveGlobalConfig(key, value));
    }

    @GetMapping("/global")
    @PreAuthorize("hasAuthority('MASTER_SETTINGS_GLOBAL_VIEW')")
    public ResponseEntity<List<MasterSettingResponse>> getAllGlobalConfigs() {
        return ResponseEntity.ok(masterSettingsService.getAllGlobalConfigs());
    }

    @GetMapping("/global/{key}")
    @PreAuthorize("hasAuthority('MASTER_SETTINGS_GLOBAL_VIEW')")
    public ResponseEntity<String> getGlobalSetting(
            @PathVariable String key,
            @RequestParam(required = false) String defaultValue) {
        return ResponseEntity.ok(masterSettingsService.getGlobalSetting(key, defaultValue));
    }

    // ================= MASTER SETTINGS (FULL UPDATE) =================

    @PostMapping
    @PreAuthorize("hasAuthority('MASTER_SETTINGS_UPDATE')")
    public ResponseEntity<Map<String, String>> updateMasterSettings(
            @Valid @RequestBody MasterSettingsRequest settings) {
        masterSettingsService.updateAllSettings(settings);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "All settings updated successfully"
        ));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MASTER_SETTINGS_VIEW')")
    public ResponseEntity<List<MasterSettingResponse>> getMasterSettings() {
        return ResponseEntity.ok(masterSettingsService.getAllSettings());
    }
}