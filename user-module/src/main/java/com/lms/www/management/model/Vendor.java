package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("code")
    @Column(name = "vendor_code", unique = true, nullable = false)
    private String vendorCode;

    @JsonProperty("companyName")
    @JsonAlias({"name"})
    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "contact_person")
    private String contactPerson;

    @JsonProperty("phone")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @JsonProperty("type")
    @Column(name = "vendor_type")
    private String vendorType;

    @Column(name = "status")
    private String status;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "gst_certificate_path")
    private String gstCertificatePath;

    @Column(name = "bank_proof_path")
    private String bankProofPath;

    @Column(name = "agreement_path")
    private String agreementPath;

    // Flattening Setters for Frontend Compatibility
    @JsonProperty("address")
    public void setAddress(Map<String, String> address) {
        if (address != null) {
            this.streetAddress = address.get("street");
            this.city = address.get("city");
            this.state = address.get("state");
            this.pincode = address.get("pincode");
        }
    }

    @JsonProperty("bankDetails")
    public void setBankDetails(Map<String, String> bankDetails) {
        if (bankDetails != null) {
            this.bankName = bankDetails.get("bankName");
            this.accountNumber = bankDetails.get("accountNumber");
            this.ifscCode = bankDetails.get("ifscCode");
            this.branchName = bankDetails.get("branchName");
            this.paymentTerms = bankDetails.get("paymentTerms");
        }
    }

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "ACTIVE";
        if (this.vendorCode == null || this.vendorCode.isEmpty()) {
            this.vendorCode = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

}