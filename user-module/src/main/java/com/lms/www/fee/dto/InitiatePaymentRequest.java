package com.lms.www.fee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InitiatePaymentRequest {
    @NotNull
    private Long allocationId;
    
    private List<Long> installmentIds;
}
