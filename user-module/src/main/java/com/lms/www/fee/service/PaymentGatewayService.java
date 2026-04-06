package com.lms.www.fee.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayService {

    Map<String, String> createOrder(
            BigDecimal amount,
            String orderId,
            String customerId,
            String customerName,
            String customerEmail,
            String customerPhone);

    boolean verifyWebhookSignature(String payload, String signature);

    String createOrderWithSessionId(String orderId, BigDecimal amount, String valueOf, String studentEmail,
            Object object);

    Map<String, Object> verifyOrderStatus(String orderId);
}
