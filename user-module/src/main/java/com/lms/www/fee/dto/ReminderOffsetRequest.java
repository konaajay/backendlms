package com.lms.www.fee.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ReminderOffsetRequest {

    @NotEmpty
    private List<@Min(1) @Max(30) Integer> offsets;
}
