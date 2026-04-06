package com.lms.www.fee.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InstallmentPlanResponse {
    private Long id;
    private Long allocationId;
    private Integer installmentNumber;
    private BigDecimal installmentAmount;
    private LocalDate dueDate;
    private BigDecimal paidAmount;
    private String status;
    private String label;

    // Legacy fields for backward compatibility
    private Integer number;
    private BigDecimal amount;
}
