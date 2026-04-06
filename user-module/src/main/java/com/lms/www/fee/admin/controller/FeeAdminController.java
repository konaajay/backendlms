package com.lms.www.fee.admin.controller;

import com.lms.www.fee.admin.service.FeeAdminService;
import com.lms.www.fee.dto.MasterSettingRequest;
import com.lms.www.fee.dto.MasterSettingResponse;
import com.lms.www.fee.dto.RefundRuleRequest;
import com.lms.www.fee.dto.RefundRuleResponse;
import com.lms.www.fee.enums.MasterSettingType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee/admin")
@RequiredArgsConstructor
public class FeeAdminController {

    private final FeeAdminService adminService;

    @PostMapping("/config")
    @PreAuthorize("hasAuthority('CONFIG_UPDATE')")
    public ResponseEntity<Void> updateGlobalConfig(@RequestParam String key, @RequestParam String value) {
        adminService.updateGlobalConfig(key, value);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/config/{key}")
    @PreAuthorize("hasAuthority('CONFIG_VIEW')")
    public ResponseEntity<String> getGlobalConfig(@PathVariable String key) {
        return ResponseEntity.ok(adminService.getGlobalConfig(key));
    }

    @PostMapping("/setting")
    @PreAuthorize("hasAuthority('SETTING_CREATE')")
    public ResponseEntity<MasterSettingResponse> upsertMasterSetting(@RequestBody MasterSettingRequest request) {
        return ResponseEntity.ok(adminService.upsertMasterSetting(request));
    }

    @GetMapping("/setting/{type}")
    @PreAuthorize("hasAuthority('SETTING_VIEW')")
    public ResponseEntity<List<MasterSettingResponse>> getSettingsByType(@PathVariable MasterSettingType type) {
        return ResponseEntity.ok(adminService.getSettingsByType(type));
    }

    @PostMapping("/refund-rule")
    @PreAuthorize("hasAuthority('REFUND_RULE_CREATE')")
    public ResponseEntity<RefundRuleResponse> createRefundRule(@RequestBody RefundRuleRequest request) {
        return ResponseEntity.ok(adminService.createRefundRule(request));
    }

    @GetMapping("/refund-rule/active")
    @PreAuthorize("hasAuthority('REFUND_RULE_VIEW')")
    public ResponseEntity<List<RefundRuleResponse>> getActiveRefundRules() {
        return ResponseEntity.ok(adminService.getActiveRefundRules());
    }
}
