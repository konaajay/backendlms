package com.lms.www.fee.dto;

import java.math.BigDecimal;

public class FeeComponentRequest {
    private String name;
    private Long feeTypeId;
    private BigDecimal amount;
    private Boolean installmentAllowed;
    private Boolean refundable;
    private Boolean mandatory;
    private java.time.LocalDate dueDate;

    public FeeComponentRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Boolean getInstallmentAllowed() { return installmentAllowed; }
    public void setInstallmentAllowed(Boolean installmentAllowed) { this.installmentAllowed = installmentAllowed; }

    public Boolean getRefundable() { return refundable; }
    public void setRefundable(Boolean refundable) { this.refundable = refundable; }

    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }

    public java.time.LocalDate getDueDate() { return dueDate; }
    public void setDueDate(java.time.LocalDate dueDate) { this.dueDate = dueDate; }
}