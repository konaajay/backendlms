package com.lms.www.fee.controller;

import com.lms.www.fee.dto.StudentLedgerResponse;
import com.lms.www.fee.service.StudentFeeAllocationService;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.fee.dto.ApiResponse;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentFeeController {

    private final StudentFeeAllocationService allocationService;
    private final UserContext userContext;

    @GetMapping("/my-ledger")
    @PreAuthorize("hasAuthority('STUDENT_LEDGER_VIEW') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentLedgerResponse>> getMyLedger() {
        Long userId = userContext.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(allocationService.getStudentLedger(userId)));
    }
}
