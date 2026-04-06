package com.lms.www.fee.payment.gateway;

import com.lms.www.fee.payment.config.CashfreeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CashfreeGateway {

    private final CashfreeConfig cashfreeConfig;
    private final RestTemplate restTemplate;

    private String getBaseUrl() {
        return cashfreeConfig.getBaseUrl() != null ? cashfreeConfig.getBaseUrl() : "https://sandbox.cashfree.com/pg";
    }

    public Map<String, String> createOrder(BigDecimal amount, String orderId, String customerId, String customerName, String customerEmail, String customerPhone) {
        String url = getBaseUrl() + "/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", cashfreeConfig.getClientId());
        headers.set("x-client-secret", cashfreeConfig.getClientSecret());
        headers.set("x-api-version", cashfreeConfig.getApiVersion());
        headers.set("Content-Type", "application/json");

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", customerId);
        customerDetails.put("customer_phone", customerPhone != null ? customerPhone : "9999999999");
        customerDetails.put("customer_email", customerEmail);
        customerDetails.put("customer_name", customerName);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("order_id", orderId);
        requestBody.put("order_amount", amount);
        requestBody.put("order_currency", "INR");
        requestBody.put("customer_details", customerDetails);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Creating Cashfree Order: {}", orderId);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = response.getBody();

            if (body != null && body.containsKey("payment_session_id")) {
                return Map.of(
                    "payment_session_id", (String) body.get("payment_session_id"),
                    "order_id", (String) body.get("order_id")
                );
            } else {
                throw new RuntimeException("Failed to create Cashfree order: " + body);
            }
        } catch (Exception e) {
            log.error("Cashfree order creation failed", e);
            throw new RuntimeException("Gateway Error: " + e.getMessage());
        }
    }

    public Map<String, Object> verifyOrderStatus(String orderId) {
        String url = getBaseUrl() + "/orders/" + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", cashfreeConfig.getClientId());
        headers.set("x-client-secret", cashfreeConfig.getClientSecret());
        headers.set("x-api-version", cashfreeConfig.getApiVersion());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to verify Cashfree order status: {}", orderId, e);
            return null;
        }
    }

    public boolean verifyWebhookSignature(String payload, String signatureHeader) {
        try {
            String timestamp = "";
            String actualSignature = "";

            for (String part : signatureHeader.split(",")) {
                if (part.trim().startsWith("t=")) timestamp = part.trim().substring(2);
                else if (part.trim().startsWith("v1=")) actualSignature = part.trim().substring(3);
            }

            String data = timestamp + payload;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(cashfreeConfig.getWebhookSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(hash);

            return computedSignature.equals(actualSignature);
        } catch (Exception e) {
            log.error("Webhook signature verification failed", e);
            return false;
        }
    }
}
