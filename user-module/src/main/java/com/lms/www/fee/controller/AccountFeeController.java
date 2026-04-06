package com.lms.www.fee.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.fee.dto.StudentFeeAllocationResponse;
import com.lms.www.fee.dto.StudentLedgerResponse;
import com.lms.www.fee.dto.ApiResponse;
import com.lms.www.fee.service.StudentFeeAllocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account/fees")
@RequiredArgsConstructor
public class AccountFeeController {

    private final StudentFeeAllocationService allocationService;

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('FEE_ACCOUNT_SUMMARY_VIEW')")
    public ResponseEntity<List<StudentFeeAllocationResponse>> getFeeSummary() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    @GetMapping("/ledger/{userId}")
    @PreAuthorize("hasAuthority('FEE_ACCOUNT_LEDGER_VIEW') or hasRole('ACCOUNTANT')")
    public ResponseEntity<ApiResponse<StudentLedgerResponse>> getStudentLedger(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.getStudentLedger(userId)));
    }
}