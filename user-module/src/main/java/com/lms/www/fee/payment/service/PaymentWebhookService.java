package com.lms.www.fee.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentWebhookService {
    /**
     * The ONLY authorized point for status transitions to SUCCESS or FAILED.
     * Implements strict state machine (PENDING -> SUCCESS/FAILED).
     * Validates amount and currency against DB record.
     */
    void handleWebhook(String payload, String signature);
    
    /**
     * Process SUCCESS status from verified webhook.
     */
    void processSuccess(String orderId, String gatewayTxnId, BigDecimal gatewayAmount, String currency, String rawResponse, LocalDateTime gatewayTime);
    
    /**
     * Process FAILED or USER_DROPPED status from verified webhook.
     */
    void processFailure(String orderId, String rawResponse);

    /**
     * Synchronize payment status with gateway (pull instead of push).
     */
    void syncPaymentStatus(String orderId);

    /**
     * Record a manual payment with SUCCESS status, updating ledger and installments.
     */
    void recordManualPayment(Long allocationId, Long installmentId, BigDecimal amount, String paymentMode, String transactionRef, String remarks, Long recordedBy);
}
