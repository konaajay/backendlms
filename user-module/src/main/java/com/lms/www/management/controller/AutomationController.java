package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.AutomationControl;
import com.lms.www.management.service.AutomationControlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alerts/automation")
@RequiredArgsConstructor
public class AutomationController {

    private final AutomationControlService automationControlService;

    @GetMapping
    // @PreAuthorize("hasAuthority('ALERT_VIEW')")
    public ResponseEntity<List<AutomationControl>> getAllControls() {
        return ResponseEntity.ok(automationControlService.getAllControls());
    }

    @PutMapping("/{ruleName}/toggle")
    // @PreAuthorize("hasAuthority('ALERT_EDIT')") // Using a common edit permission
    public ResponseEntity<AutomationControl> toggleControl(@PathVariable String ruleName) {
        return ResponseEntity.ok(automationControlService.toggleControl(ruleName));
    }
}