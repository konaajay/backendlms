package com.lms.www.management.dashboard.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamAttemptResultDTO {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private Integer attemptNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Double obtainedMarks;
    private Integer totalMarks;
    private Double percentage;
    private Boolean isPassed;
    private java.util.List<com.lms.www.management.model.ExamResponse> responses;
}
