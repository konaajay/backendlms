package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationRequest {
    private Long userId;
    private Long feeStructureId;
    private String studentName;
    private String studentEmail;
    private java.math.BigDecimal discountAmount;
}
