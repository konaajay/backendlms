package com.lms.www.fee.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountCreateRequest {
    private String name;
    private String email;
    private String password;
    private String role; // e.g., ROLE_USER, ROLE_ADMIN
    
    // Optional Fee Details
    private BigDecimal feeAmount;
    private Long courseId;
    private Long batchId;
    private Long feeStructureId;

    // Manual Getters/Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public Long getFeeStructureId() { return feeStructureId; }
    public void setFeeStructureId(Long id) { this.feeStructureId = id; }
}
