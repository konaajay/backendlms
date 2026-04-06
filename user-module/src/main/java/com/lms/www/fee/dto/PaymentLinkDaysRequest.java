package com.lms.www.fee.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentLinkDaysRequest {

    @NotNull
    @Min(1)
    @Max(60)
    private Integer days;
}
