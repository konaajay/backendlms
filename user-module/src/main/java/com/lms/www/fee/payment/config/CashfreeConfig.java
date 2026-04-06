package com.lms.www.fee.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties(prefix = "cashfree")
@Validated
public class CashfreeConfig {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String webhookSecret;

    @NotBlank
    private String apiVersion = "2023-08-01";
}
