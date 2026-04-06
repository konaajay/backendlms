package com.lms.www.fee.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeeTypeRequest {
    private String name;
    private String description;
    private Boolean active;
    private Boolean mandatory;
    private Boolean refundable;
    private Boolean oneTime;
    private Integer displayOrder;
}
