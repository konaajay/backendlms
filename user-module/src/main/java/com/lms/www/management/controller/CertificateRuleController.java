package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.CertificateRule;
import com.lms.www.management.service.CertificateRuleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/certificate-rules")
@RequiredArgsConstructor
public class CertificateRuleController {

    private final CertificateRuleService certificateRuleService;

    // =========================================================
    // CREATE / UPDATE RULE
    // =========================================================
    @PostMapping
    @PreAuthorize("hasAuthority('CERTIFICATE_RULE_CREATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateRule> saveRule(
            @RequestBody CertificateRule rule) {

        return ResponseEntity.ok(
                certificateRuleService.saveRule(rule)
        );
    }

    // =========================================================
    // ENABLE / DISABLE RULE (Changed to PUT)
    // =========================================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CERTIFICATE_RULE_TOGGLE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateRule> updateRule(
            @PathVariable Long id,
            @RequestBody CertificateRule rule) {

        rule.setId(id);

        return ResponseEntity.ok(
                certificateRuleService.saveRule(rule)
        );
    }

    // =========================================================
    // VIEW RULE
    // =========================================================
    @GetMapping("/{targetType}/{targetId}")
    @PreAuthorize("hasAuthority('CERTIFICATE_RULE_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateRule> getRuleByTarget(
            @PathVariable String targetType,
            @PathVariable Long targetId) {

        return ResponseEntity.ok(
                certificateRuleService.getRuleByTarget(targetType, targetId)
        );
    }
    
    
}