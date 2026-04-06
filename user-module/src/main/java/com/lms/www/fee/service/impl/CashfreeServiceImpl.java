package com.lms.www.fee.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import com.lms.www.fee.service.CashfreeService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.lms.www.fee.payment.config.CashfreeConfig;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashfreeServiceImpl implements CashfreeService {

    private final CashfreeConfig cashfreeConfig;
    private final RestTemplate restTemplate;

    private String getBaseUrl() {
        return cashfreeConfig.getBaseUrl() != null ? cashfreeConfig.getBaseUrl() : "https://sandbox.cashfree.com/pg";
    }

    @Override
    public String createOrder(String orderId, BigDecimal amount, String studentId, String email, String phone) {
        String url = getBaseUrl() + "/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", cashfreeConfig.getClientId());
        headers.set("x-client-secret", cashfreeConfig.getClientSecret());
        headers.set("x-api-version", cashfreeConfig.getApiVersion());
        headers.set("Content-Type", "application/json");

        if (studentId == null || email == null || phone == null) {
            throw new IllegalArgumentException("Student details (ID, Email, Phone) are required for payment");
        }

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", studentId);
        customerDetails.put("customer_phone", phone);
        customerDetails.put("customer_email", email);
        customerDetails.put("customer_name", "Student " + studentId);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order_id", orderId);
        requestBody.put("order_amount", amount);
        requestBody.put("order_currency", "INR");
        requestBody.put("customer_details", customerDetails);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Calling Cashfree Order API for order {}", orderId);
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

    @Override
    public boolean verifyWebhookSignature(String signature, String payload) {
        try {
            String timestamp = extractTimestampFromSignatureHeader(signature);
            String actualSignature = extractSignatureFromHeader(signature);

            String data = timestamp + payload;

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(cashfreeConfig.getWebhookSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(hash);

            boolean match = computedSignature.equals(actualSignature);
            if (!match) {
                log.warn("Cashfree Webhook Webhook Signature Verification Failed. Expected {}, Got {}", actualSignature,
                        computedSignature);
            }
            return match;

        } catch (Exception e) {
            log.error("Error during Cashfree webhook signature verification", e);
            return false;
        }
    }

    private String extractTimestampFromSignatureHeader(String signatureHeader) {
        // Cashfree signature format: t=161xxx, v1=abcxyz
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
