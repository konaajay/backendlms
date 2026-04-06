package com.lms.www.management.instructor.dto;

import com.lms.www.management.enums.TargetType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InstructorCertificateRequestDTO {

    @NotNull
    private TargetType targetType;

    @NotNull
    private Long targetId;

    @NotBlank
    private String eventTitle;

    @Min(0)
    private Double score;
}
