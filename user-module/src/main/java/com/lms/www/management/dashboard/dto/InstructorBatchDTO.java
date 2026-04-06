package com.lms.www.management.dashboard.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorBatchDTO {

    private Long batchId;
    private String batchName;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer studentsCount;

    private String courseName; // optional
}