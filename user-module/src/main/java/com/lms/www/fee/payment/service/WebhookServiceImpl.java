package com.lms.www.fee.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.www.fee.payment.gateway.CashfreeGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookService {

    private final CashfreeGateway cashfreeGateway;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @Override
    public void handleCashfreeWebhook(String payload, String signature) {
        log.info("Received Cashfree Webhook: signature={}", signature);

        if (!cashfreeGateway.verifyWebhookSignature(payload, signature)) {
            log.error("Invalid Webhook Signature for payload: {}", payload);
            throw new RuntimeException("Invalid Signature");
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode order = root.path("order");
            JsonNode payment = root.path("payment");

            String orderId = order.path("order_id").asText();
            BigDecimal amount = new BigDecimal(payment.path("payment_amount").asText());
            String status = payment.path("payment_status").asText();

            log.info("Processing Webhook for Order: {}, Status: {}", orderId, status);

            if ("SUCCESS".equalsIgnoreCase(status)) {
                paymentService.finalizePayment(
                        orderId,
                        payload,
                        amount,
                        status,
                        LocalDateTime.now()); // In reality, parse paymentTime if needed
            } else {
                log.warn("Webhook received non-success status: {} for Order: {}", status, orderId);
            }

        } catch (Exception e) {
            log.error("Failed to process Cashfree webhook payload", e);
            throw new RuntimeException("Webhook Processing Error: " + e.getMessage());
        }
    }
}
