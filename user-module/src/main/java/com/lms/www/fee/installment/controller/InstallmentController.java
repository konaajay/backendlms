package com.lms.www.fee.installment.controller;

import com.lms.www.fee.dto.InstallmentResponse;
import com.lms.www.fee.installment.service.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
@RequestMapping("/api/v1/fee-management/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentService installmentService;

    @GetMapping("/allocation/{allocationId}")
    @PreAuthorize("hasAuthority('INSTALLMENT_VIEW') or hasAuthority('ROLE_PARENT')")
    public ResponseEntity<List<InstallmentResponse>> getInstallmentsByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(installmentService.getInstallmentsByAllocation(allocationId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_VIEW') or hasAuthority('ROLE_PARENT')")
    public ResponseEntity<InstallmentResponse> getInstallmentById(@PathVariable Long id) {
        return ResponseEntity.ok(installmentService.getInstallmentById(id));
    }

    @PostMapping("/status/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_UPDATE')")
    public ResponseEntity<Void> updateInstallmentStatus(@PathVariable Long id, @RequestParam String status) {
        installmentService.updateInstallmentStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lock/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_LOCK')")
    public ResponseEntity<Void> lockForEarlyPayment(@PathVariable Long id) {
        installmentService.lockForEarlyPayment(id);
        return ResponseEntity.noContent().build();
    }
}
