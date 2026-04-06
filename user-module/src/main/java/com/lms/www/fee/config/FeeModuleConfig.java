package com.lms.www.fee.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties(prefix = "fee")
@Validated
public class FeeModuleConfig {

    private Installment installment = new Installment();
    private OrderPrefix orderPrefix = new OrderPrefix();
    private Receipt receipt = new Receipt();
    private Defaults defaults = new Defaults();
    private EarlyPayment earlyPayment = new EarlyPayment();

    @Data
    public static class Installment {
        private int min = 1;
        private int max = 24;
    }

    @Data
    public static class OrderPrefix {
        @NotBlank private String normal = "ORD_";
        @NotBlank private String early = "EARLY_";
        @NotBlank private String manual = "MANUAL-";
    }

    @Data
    public static class Receipt {
        @NotBlank private String prefix = "REC-";
    }

    @Data
    public static class Defaults {
        @NotBlank private String currency = "INR";
    }

    @Data
    public static class EarlyPayment {
        private int linkExpiryDays = 1;
    }
}
