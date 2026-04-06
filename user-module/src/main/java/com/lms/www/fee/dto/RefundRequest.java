package com.lms.www.fee.dto;

import java.math.BigDecimal;

public class RefundRequest {
    private Long allocationId;
    private BigDecimal refundAmount;
    private String refundType;
    private String refundMode;
    private String refundReason;

    public RefundRequest() {}

    public Long getAllocationId() { return allocationId; }
    public void setAllocationId(Long allocationId) { this.allocationId = allocationId; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public String getRefundType() { return refundType; }
    public void setRefundType(String refundType) { this.refundType = refundType; }

    public String getRefundMode() { return refundMode; }
    public void setRefundMode(String refundMode) { this.refundMode = refundMode; }

    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }

    // Aliases for and compatibility
    public BigDecimal getAmount() { return refundAmount; }
    public String getReason() { return refundReason; }
}
