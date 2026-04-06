package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeTypeResponse {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private boolean mandatory;
    private boolean refundable;
    private boolean oneTime;
    private Integer displayOrder;
}
