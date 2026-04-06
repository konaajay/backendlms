package com.lms.www.fee.payment.service;

public interface WebhookService {
    void handleCashfreeWebhook(String payload, String signature);
}
