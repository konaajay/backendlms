package com.lms.www.fee.service;

import java.math.BigDecimal;


public interface CashfreeService {

    /**
     * Creates an order with the Cashfree Payment Gateway and returns the generated
     * Payment Session ID.
     *
     * @param orderId   The pre-generated local database order ID.
     * @param amount    The amount to be paid.
     * @param studentId The internal ID of the student.
     * @param email     The student's email address.
     * @param phone     The student's phone number.
     * @return The payment_session_id returned by Cashfree to be passed to the
     *         frontend SDK.
     */
    String createOrder(String orderId, BigDecimal amount, String studentId, String email, String phone);

    /**
     * Verifies the authenticity of the Webhook payload sent by Cashfree using
     * HMAC-SHA256 signature validation.
     *
     * @param signature The `x-webhook-signature` header provided in the HTTP
     *                  request.
     * @param payload   The raw JSON request body as a string.
     * @return true if the signature is valid and authentic, false otherwise.
     */
    boolean verifyWebhookSignature(String signature, String payload);
}
