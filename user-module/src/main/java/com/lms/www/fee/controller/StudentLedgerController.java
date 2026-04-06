package com.lms.www.fee.controller;

import com.lms.www.fee.dto.ApiResponse;
import com.lms.www.fee.dto.StudentFeeDashboardResponse;
import com.lms.www.fee.dto.StudentLedgerResponse;
import com.lms.www.fee.service.StudentLedgerService;
import com.lms.www.security.UserContext;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentLedgerController {

    private final StudentLedgerService ledgerService;
    private final UserContext userContext;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('STUDENT_DASHBOARD_VIEW')")
    public ResponseEntity<ApiResponse<StudentFeeDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        ledgerService.getDashboard(userContext.getCurrentUserId()),
                        "Dashboard fetched successfully"));
    }

    @GetMapping("/ledger")
    @PreAuthorize("hasAuthority('STUDENT_LEDGER_VIEW')")
    public ResponseEntity<ApiResponse<StudentLedgerResponse>> getLedger() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        ledgerService.getLedger(userContext.getCurrentUserId()),
                        "Ledger fetched successfully"));
    }

    @PostMapping("/installment/{id}/create-order")
    @PreAuthorize("hasAuthority('STUDENT_PAYMENT_CREATE')")
    public ResponseEntity<ApiResponse<Map<String, String>>> createOrder(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        ledgerService.createOrder(id, userContext.getCurrentUserId()),
                        "Order created successfully"));
    }
}