package com.lms.www.fee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BulkProcessRequest {
    @NotNull
    private Long allocationId;
    
    @NotEmpty
    private List<Long> installmentIds;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotBlank
    private String paymentMode;
    
    @NotBlank
    private String transactionRef;
    
    private String gatewayResponse;
    private Long recordedBy;
}
