package com.lms.www.fee.dto;

import com.lms.www.fee.penalty.entity.PenaltyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeePenaltySlabResponse {
    private Long id;
    private Integer fromDay;
    private Integer toDay;
    private PenaltyType penaltyType;
    private BigDecimal value;
    private boolean active;
}
