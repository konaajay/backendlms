package com.lms.www.fee.payment.controller;

import com.lms.www.fee.dto.*;
import com.lms.www.fee.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/fee-management/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;

    // ================= MANUAL PAYMENTS =================

    @PostMapping("/manual")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<StudentFeePaymentResponse> recordManual(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.recordManual(request));
    }

    // ================= BULK PAYMENTS =================

    @PostMapping("/bulk/calculate")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<BulkCalculationResponse> calculateBulk(@Valid @RequestBody BulkRequest request) {
        return ResponseEntity.ok(paymentService.calculateBulkSecure(request));
    }

    @PostMapping("/bulk/process")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<BulkPaymentResponse> processBulk(@Valid @RequestBody BulkProcessRequest request) {
        return ResponseEntity.ok(paymentService.processBulkSecure(request));
    }

    // ================= EARLY PAYMENTS =================

    @PostMapping("/early/generate-link/{studentId}")
    @PreAuthorize("hasAuthority('EARLY_PAYMENT_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<EarlyPaymentResponse> generateEarlyPaymentLink(
            @PathVariable Long studentId,
            @Valid @RequestBody GenerateEarlyPaymentRequest request) {
        return ResponseEntity.ok(paymentService.generateEarlyPaymentLink(request, studentId));
    }

    @PostMapping("/early/full-link/{studentId}/{allocationId}")
    @PreAuthorize("hasAuthority('EARLY_PAYMENT_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<EarlyPaymentResponse> generateFullPaymentLink(
            @PathVariable Long studentId,
            @PathVariable Long allocationId,
            @Valid @RequestBody GenerateEarlyPaymentRequest request) {
        return ResponseEntity.ok(paymentService.generateFullPaymentLink(request, studentId, allocationId));
    }

    // ================= VIEW & MANAGE =================

    @GetMapping
    @PreAuthorize("hasAuthority('PAYMENT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<StudentFeePaymentResponse>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/allocation/{allocationId}")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<StudentFeePaymentResponse>> getByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(paymentService.getByAllocation(allocationId));
    }

    @PostMapping("/{id}/release-early-locks")
    @PreAuthorize("hasAuthority('EARLY_PAYMENT_LOCK_MANAGE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> releaseLocks(@PathVariable Long id) {
        paymentService.releaseEarlyPaymentLocks(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/early")
    @PreAuthorize("hasAuthority('EARLY_PAYMENT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<EarlyPaymentResponse>> getAllEarlyPayments() {
        return ResponseEntity.ok(paymentService.getAllEarlyPayments());
    }
    @PreAuthorize("hasAuthority('PAYMENT_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync/{orderId}")
    @PreAuthorize("hasAuthority('PAYMENT_SYNC') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Map<String, String>> syncPaymentStatus(@PathVariable String orderId) {
        String status = paymentService.syncPaymentStatus(orderId);
        return ResponseEntity.ok(Map.of("status", status));
    }
}
