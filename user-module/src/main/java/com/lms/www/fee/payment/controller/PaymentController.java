package com.lms.www.fee.payment.controller;

import com.lms.www.fee.dto.*;
import com.lms.www.fee.payment.service.PaymentService;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserContext userContext;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW') or hasAuthority('ROLE_PARENT')")
    public ResponseEntity<StudentFeePaymentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentSecure(id));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW_SELF')")
    public ResponseEntity<List<StudentFeePaymentResponse>> getMyPayments() {
        return ResponseEntity.ok(paymentService.getMyPayments());
    }

    @GetMapping("/early/me")
    @PreAuthorize("hasAuthority('EARLY_PAYMENT_VIEW_SELF')")
    public ResponseEntity<List<EarlyPaymentResponse>> getMyActiveEarlyPayments() {
        return ResponseEntity.ok(paymentService.getActiveEarlyPaymentsSecure(userContext.getCurrentUserId()));
    }

    @PostMapping("/initiate")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE') or hasAuthority('ROLE_PARENT')")
    public ResponseEntity<InitiatePaymentResponse> initiate(@Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.initiatePaymentSecure(request));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAuthority('PAYMENT_VERIFY') or hasAuthority('ROLE_PARENT')")
    public ResponseEntity<VerifyPaymentResponse> verify(@Valid @RequestBody VerifyPaymentRequest request) {
        return ResponseEntity.ok(paymentService.verifyPaymentSecure(request));
    }

    @GetMapping("/{id}/receipt")
    @PreAuthorize("hasAuthority('PAYMENT_RECEIPT_VIEW') or hasAuthority('ROLE_PARENT') or hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Long id) {
        byte[] pdf = paymentService.getReceiptSecure(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt_" + id + ".pdf")
                .body(pdf);
    }
}
