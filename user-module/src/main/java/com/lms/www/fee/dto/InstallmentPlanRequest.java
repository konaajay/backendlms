package com.lms.www.fee.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstallmentPlanRequest {
    private Integer installmentNumber;
    
    @JsonAlias({"amount"})
    private BigDecimal installmentAmount;
    
    private LocalDate dueDate;
    private String label;
}
