package com.lms.www.fee.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class InstallmentUpdateRequest {
    @NotNull
    private LocalDate newDueDate;

    @NotBlank
    private String reason;
}
