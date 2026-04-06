package com.lms.www.fee.payment.controller;

import com.lms.www.fee.dto.InstallmentPaymentResponse;
import com.lms.www.fee.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fee/payments/public")
@RequiredArgsConstructor
public class PublicPaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<InstallmentPaymentResponse> getPublicPaymentInfo(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPublicPaymentInfo(orderId));
    }
}
