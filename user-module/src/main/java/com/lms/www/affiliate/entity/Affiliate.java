package com.lms.www.affiliate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliates", indexes = {
        @Index(name = "idx_affiliate_user_id", columnList = "user_id"),
        @Index(name = "idx_affiliate_email", columnList = "email"),
        @Index(name = "idx_affiliate_username", columnList = "username"),
        @Index(name = "idx_affiliate_status", columnList = "status"),
        @Index(name = "idx_affiliate_type", columnList = "type")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Affiliate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AffiliateType type;

    @Column(name = "referral_code", nullable = false, unique = true, length = 20)
    private String referralCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 15)
    private String mobile;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AffiliateStatus status = AffiliateStatus.ACTIVE;

    @Column(name = "commission_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommissionType commissionType = CommissionType.PERCENTAGE;

    @Column(name = "commission_value", precision = 19, scale = 4, nullable = false)
    private BigDecimal commissionValue;

    @Column(name = "student_discount_value", precision = 19, scale = 4)
    private BigDecimal studentDiscountValue;

    @Column(name = "cookie_days")
    private Integer cookieDays;

    @Column(name = "min_payout", precision = 19, scale = 4)
    private BigDecimal minPayout;

    @Column(name = "withdrawal_enabled")
    @Builder.Default
    private boolean withdrawalEnabled = true;

    // Bank Details for Payouts
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_number", length = 50)
    private String accountNumber;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "account_holder_name", length = 100)
    private String accountHolderName;

    @Column(name = "upi_id", length = 100)
    private String upiId;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Manual accessors to resolve Lombok processing issues
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public AffiliateStatus getStatus() { return status; }
    public void setStatus(AffiliateStatus status) { this.status = status; }
    public CommissionType getCommissionType() { return commissionType; }
    public void setCommissionType(CommissionType commissionType) { this.commissionType = commissionType; }
    public BigDecimal getCommissionValue() { return commissionValue; }
    public void setCommissionValue(BigDecimal commissionValue) { this.commissionValue = commissionValue; }
    public AffiliateType getType() { return type; }
    public void setType(AffiliateType type) { this.type = type; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
}