package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureResponse {
    private Long id;
    private String name;
    private BigDecimal totalAmount;
    private Integer installmentCount;
}
