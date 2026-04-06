package com.lms.www.management.dashboard.dto;

import lombok.Data;

@Data
public class StudentExamResponseSaveDTO {
    private Long examQuestionId;
    private Long selectedOptionId;
    private String descriptiveAnswer;
    private String codingSubmissionCode;
}
