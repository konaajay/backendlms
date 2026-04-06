package com.lms.www.fee.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lms.www.fee.service.PaymentGatewayService;

import org.springframework.core.ParameterizedTypeReference;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashfreePaymentGatewayServiceImpl implements PaymentGatewayService {

    @Value("${cashfree.base-url:https://sandbox.cashfree.com/pg}")
    private String cashfreeBaseUrl;

    @Value("${cashfree.client-id:}")
    private String clientId;

    @Value("${cashfree.client-secret:}")
    private String clientSecret;

    @Value("${cashfree.webhook-secret:}")
    private String webhookSecret;

    private final RestTemplate restTemplate;

    @Override
    public Map<String, String> createOrder(BigDecimal amount, String orderId, String customerId,
            String customerName, String customerEmail, String customerPhone) {
        String paymentSessionId = createOrderWithSessionId(orderId, amount, customerId, customerEmail, customerPhone);
        Map<String, String> order = new HashMap<>();
        order.put("id", orderId);
        order.put("amount", amount.toString());
        order.put("currency", "INR");
        order.put("receipt", orderId);
        order.put("status", "created");
        order.put("payment_session_id", paymentSessionId);
        return order;
    }

    @Override
    public String createOrderWithSessionId(String orderId, BigDecimal amount, String studentId, String email,
            Object phoneObj) {
        String phone = phoneObj != null ? phoneObj.toString() : null;
        String sanitizedPhone = sanitizePhone(phone);
        String url = cashfreeBaseUrl + "/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-client-secret", clientSecret);
        headers.set("x-api-version", "2023-08-01");
        headers.set("Content-Type", "application/json");

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", studentId != null ? studentId : "CUST_001");
        customerDetails.put("customer_phone", sanitizedPhone);
        customerDetails.put("customer_email", email != null ? email : "student@example.com");
        customerDetails.put("customer_name", "Student " + studentId);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order_id", orderId);
        requestBody.put("order_amount", amount);
        requestBody.put("order_currency", "INR");
        requestBody.put("customer_details", customerDetails);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Calling Cashfree Order API for order {} with phone {}", orderId, sanitizedPhone);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("payment_session_id")) {
                return (String) body.get("payment_session_id");
            } else {
                throw new RuntimeException("Failed to retrieve payment_session_id: " + body);
            }
        } catch (Exception e) {
            log.error("Exception calling Cashfree createOrder API", e);
            throw new RuntimeException("Cashfree API Call Failed: " + e.getMessage());
        }
    }

    private String sanitizePhone(String phone) {
        if (phone == null || phone.isBlank())
            return "9999999999";

        // Remove all non-digits
        String digits = phone.replaceAll("\\D", "");

        if (digits.length() == 10)
            return digits;

        // Handle 11 digits starting with 0 (Indian style)
        if (digits.length() == 11 && digits.startsWith("0")) {
            return digits.substring(1);
        }

        // Handle 12 digits starting with 91 (Indian country code)
        if (digits.length() == 12 && digits.startsWith("91")) {
            return digits.substring(2);
        }

        // Catch-all: If it's longer than 10, take the last 10 digits
        if (digits.length() > 10) {
            return digits.substring(digits.length() - 10);
        }

        // If it's shorter than 10 or otherwise invalid, fallback to avoid API rejection
        return digits.length() >= 10 ? digits : "9999999999";
    }

    @Override
    public Map<String, Object> verifyOrderStatus(String orderId) {
        String url = cashfreeBaseUrl + "/orders/" + orderId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-client-secret", clientSecret);
        headers.set("x-api-version", "2023-08-01");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to verify order status for " + orderId, e);
            throw new RuntimeException("Failed to fetch order status from Cashfree: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            String timestamp = extractTimestampFromSignatureHeader(signature);
            String actualSignature = extractSignatureFromHeader(signature);

            String data = timestamp + payload;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(hash);

            boolean match = computedSignature.equals(actualSignature);
            if (!match) {
                log.warn("Cashfree Webhook Signature Verification Failed. Expected {}, Got {}", actualSignature,
                        computedSignature);
            }
            return match;

        } catch (Exception e) {
            log.error("Error during Cashfree webhook signature verification", e);
            return false;
        }
    }

    private String extractTimestampFromSignatureHeader(String signatureHeader) {
        for (String part : signatureHeader.split(",")) {
            if (part.trim().startsWith("t=")) {
                return part.trim().substring(2);
            }
        }
        return "";
    }

    private String extractSignatureFromHeader(String signatureHeader) {
        for (String part : signatureHeader.split(",")) {
            if (part.trim().startsWith("v1=")) {
                return part.trim().substring(3);
            }
        }
        return "";
    }
}
