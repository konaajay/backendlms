package com.lms.www.fee.controller;

import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.dto.PaymentLinkResponse;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.service.StudentInstallmentPlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/installment")
@RequiredArgsConstructor
public class AdminInstallmentController {

    private final StudentInstallmentPlanService installmentService;

    @PostMapping("/{id}/generate-link")
    @PreAuthorize("hasAuthority('INSTALLMENT_CREATE')")
    public ResponseEntity<PaymentLinkResponse> generatePaymentLink(@PathVariable Long id) {
        StudentInstallmentPlan plan = installmentService.generatePaymentLink(id);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(FeeMapper.toPaymentLinkResponse(plan));
    }
}