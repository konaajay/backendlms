package com.lms.www.management.instructor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InstructorEvaluationRequestDTO {

    @NotNull
    @Min(0)
    private Double marksAwarded;

    @Size(max = 500)
    private String feedback;
}
