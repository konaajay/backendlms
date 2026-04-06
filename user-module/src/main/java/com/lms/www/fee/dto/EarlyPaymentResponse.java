package com.lms.www.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EarlyPaymentResponse {
    private Long id;
    private Long studentId;
    private List<Long> installmentIds;
    private BigDecimal totalOriginalAmount;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal finalAmount;
    private String cashfreeOrderId;
    private String paymentSessionId;
    private String status;
    private LocalDateTime linkCreatedAt;
    private LocalDateTime linkExpiry;

    public EarlyPaymentResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public List<Long> getInstallmentIds() { return installmentIds; }
    public void setInstallmentIds(List<Long> installmentIds) { this.installmentIds = installmentIds; }

    public BigDecimal getTotalOriginalAmount() { return totalOriginalAmount; }
    public void setTotalOriginalAmount(BigDecimal totalOriginalAmount) { this.totalOriginalAmount = totalOriginalAmount; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getCashfreeOrderId() { return cashfreeOrderId; }
    public void setCashfreeOrderId(String cashfreeOrderId) { this.cashfreeOrderId = cashfreeOrderId; }

    public String getPaymentSessionId() { return paymentSessionId; }
    public void setPaymentSessionId(String paymentSessionId) { this.paymentSessionId = paymentSessionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLinkCreatedAt() { return linkCreatedAt; }
    public void setLinkCreatedAt(LocalDateTime linkCreatedAt) { this.linkCreatedAt = linkCreatedAt; }

    public LocalDateTime getLinkExpiry() { return linkExpiry; }
    public void setLinkExpiry(LocalDateTime linkExpiry) { this.linkExpiry = linkExpiry; }

    public static EarlyPaymentResponseBuilder builder() {
        return new EarlyPaymentResponseBuilder();
    }

    public static class EarlyPaymentResponseBuilder {
        private EarlyPaymentResponse instance = new EarlyPaymentResponse();

        public EarlyPaymentResponseBuilder id(Long id) { instance.setId(id); return this; }
        public EarlyPaymentResponseBuilder studentId(Long studentId) { instance.setStudentId(studentId); return this; }
        public EarlyPaymentResponseBuilder installmentIds(List<Long> ids) { instance.setInstallmentIds(ids); return this; }
        public EarlyPaymentResponseBuilder totalOriginalAmount(BigDecimal amount) { instance.setTotalOriginalAmount(amount); return this; }
        public EarlyPaymentResponseBuilder discountType(String type) { instance.setDiscountType(type); return this; }
        public EarlyPaymentResponseBuilder discountValue(BigDecimal value) { instance.setDiscountValue(value); return this; }
        public EarlyPaymentResponseBuilder finalAmount(BigDecimal amount) { instance.setFinalAmount(amount); return this; }
        public EarlyPaymentResponseBuilder cashfreeOrderId(String id) { instance.setCashfreeOrderId(id); return this; }
        public EarlyPaymentResponseBuilder paymentSessionId(String id) { instance.setPaymentSessionId(id); return this; }
        public EarlyPaymentResponseBuilder status(String status) { instance.setStatus(status); return this; }
        public EarlyPaymentResponseBuilder linkCreatedAt(LocalDateTime dt) { instance.setLinkCreatedAt(dt); return this; }
        public EarlyPaymentResponseBuilder linkExpiry(LocalDateTime dt) { instance.setLinkExpiry(dt); return this; }
        public EarlyPaymentResponse build() { return instance; }
    }
}
