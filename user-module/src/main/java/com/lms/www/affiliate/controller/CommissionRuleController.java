package com.lms.www.affiliate.controller;

import com.lms.www.affiliate.entity.CommissionRule;
import com.lms.www.affiliate.service.CommissionRuleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/commission-rules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommissionRuleController {

    private final CommissionRuleService service;

    // ===== VIEW =====
    @GetMapping
    @PreAuthorize("hasAuthority('COMMISSION_RULE_VIEW')")
    public ResponseEntity<List<CommissionRule>> getAllRules() {
        return ResponseEntity.ok(service.getAllRules());
    }

    // ===== CREATE =====
    @PostMapping
    @PreAuthorize("hasAuthority('COMMISSION_RULE_CREATE')")
    public ResponseEntity<CommissionRule> createRule(
            @Valid @RequestBody CommissionRule rule) {

        return ResponseEntity.ok(service.createRule(rule));
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMISSION_RULE_UPDATE')")
    public ResponseEntity<CommissionRule> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody CommissionRule ruleDetails) {

        return service.updateRule(id, ruleDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMMISSION_RULE_DELETE')")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {

        if (service.deleteRule(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}