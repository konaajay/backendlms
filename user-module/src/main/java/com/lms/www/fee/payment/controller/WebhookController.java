package com.lms.www.fee.payment.controller;

import com.lms.www.fee.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fee-management/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PaymentService paymentService;

    @PostMapping("/cashfree")
    public ResponseEntity<Void> handleCashfreeWebhook(
            @RequestBody String payload,
            @RequestHeader("x-webhook-signature") String signature) {
        log.info("Received Cashfree webhook: {}", payload);
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
