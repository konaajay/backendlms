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
public class PenaltyResponse {
    private Long id;
    private Long installmentId;
    private BigDecimal amount;
    private Boolean waived;
    private String reason;
}
