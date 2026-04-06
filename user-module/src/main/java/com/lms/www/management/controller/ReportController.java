// Redundant - Logic moved to ManagementReportController

package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('REPORT_DASHBOARD_VIEW')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {

        return ResponseEntity.ok(reportService.getDashboardSummary());
    }

    @GetMapping("/stock-summary")
    @PreAuthorize("hasAuthority('REPORT_STOCK_VIEW')")
    public ResponseEntity<List<?>> getStockSummary() {

        return ResponseEntity.ok(reportService.getStockSummary());
    }

    @GetMapping("/purchases")
    @PreAuthorize("hasAuthority('REPORT_PURCHASE_VIEW')")
    public ResponseEntity<List<?>> getPurchaseReport() {

        return ResponseEntity.ok(reportService.getPurchaseReport());
    }

    @GetMapping("/issuance")
    @PreAuthorize("hasAuthority('REPORT_ISSUE_VIEW')")
    public ResponseEntity<List<?>> getIssueReport() {

        return ResponseEntity.ok(reportService.getIssueReport());
    }

    @GetMapping("/loss-damage")
    @PreAuthorize("hasAuthority('REPORT_DAMAGE_VIEW')")
    public ResponseEntity<List<?>> getLossDamageReport() {

        return ResponseEntity.ok(reportService.getLossDamageReport());
    }
}