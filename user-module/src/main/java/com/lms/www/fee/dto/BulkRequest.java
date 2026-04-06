package com.lms.www.fee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkRequest {
    @NotNull
    private Long allocationId;
    
    @NotNull
    private List<Long> installmentIds;
}
