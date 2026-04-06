package com.lms.www.fee.dto;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterSettingsRequest {
    private GeneralSettings general;
    private PenaltySettings penalty;
    private NotificationSettings notifications;

    @Data
    public static class GeneralSettings {
        private String currency;
        private String currencySymbol;
        private String taxName;
        private Double taxPercentage;
        private String invoicePrefix;
        private String financialYear;
    }

    @Data
    public static class PenaltySettings {
        private Boolean enabled;
        private BigDecimal amount;
        private String type;
        private BigDecimal maxCap;
        private Boolean sendEmail;
        private String frequency;
        private List<SlabDTO> slabs;

        @Data
        public static class SlabDTO {
            private Integer fromDay;
            private Integer toDay;
            private BigDecimal finePerDay;
        }
    }

    @Data
    public static class NotificationSettings {
        private Boolean creation;
        private Boolean pending;
        private Boolean overdue;
        private Boolean paymentSuccess;
        private Boolean partialPayment;
        private Boolean refundStatus;
    }
}
