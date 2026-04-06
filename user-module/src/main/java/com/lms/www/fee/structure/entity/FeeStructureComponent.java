package com.lms.www.fee.structure.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fee_structure_components")
public class FeeStructureComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(name = "fee_type_id", nullable = true)
    private Long feeTypeId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "name")
    private String name;

    @Column(name = "due_date")
    private java.time.LocalDate dueDate;

    @Column(name = "base_amount", precision = 12, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "fixed_flag", nullable = false)
    private Boolean fixedFlag = true;

    @Column(name = "is_fixed_flag", nullable = false)
    private Boolean isFixedFlag = true;

    @Column(name = "refundable", nullable = false)
    private Boolean refundable = true;

    @Column(name = "is_refundable", nullable = false)
    private Boolean isRefundable = true;

    @Column(name = "non_refundable_flag", nullable = false)
    private Boolean nonRefundableFlag = false;

    @Column(name = "installment_allowed", nullable = false)
    private Boolean installmentAllowed = true;

    @Column(name = "mandatory", nullable = false)
    private Boolean mandatory = true;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;

    public FeeStructureComponent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FeeStructure getFeeStructure() { return feeStructure; }
    public void setFeeStructure(FeeStructure feeStructure) { this.feeStructure = feeStructure; }
    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public java.time.LocalDate getDueDate() { return dueDate; }
    public void setDueDate(java.time.LocalDate dueDate) { this.dueDate = dueDate; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public Boolean isActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Boolean getFixedFlag() { return fixedFlag; }
    public void setFixedFlag(Boolean fixedFlag) { this.fixedFlag = fixedFlag; }
    public Boolean getIsFixedFlag() { return isFixedFlag; }
    public void setIsFixedFlag(Boolean isFixedFlag) { this.isFixedFlag = isFixedFlag; }
    public Boolean getRefundable() { return refundable; }
    public void setRefundable(Boolean refundable) { this.refundable = refundable; }
    public Boolean getIsRefundable() { return isRefundable; }
    public void setIsRefundable(Boolean isRefundable) { this.isRefundable = isRefundable; }
    public Boolean getNonRefundableFlag() { return nonRefundableFlag; }
    public void setNonRefundableFlag(Boolean nonRefundableFlag) { this.nonRefundableFlag = nonRefundableFlag; }
    public Boolean getInstallmentAllowed() { return installmentAllowed; }
    public void setInstallmentAllowed(Boolean installmentAllowed) { this.installmentAllowed = installmentAllowed; }
    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }
    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
}
