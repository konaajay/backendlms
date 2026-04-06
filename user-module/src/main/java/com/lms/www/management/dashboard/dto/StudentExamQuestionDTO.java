package com.lms.www.management.dashboard.dto;

import java.util.List;

import com.lms.www.management.enums.ProgrammingLanguage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentExamQuestionDTO {
    private Long examQuestionId;
    private Long questionId;
    private String questionText;
    private String questionImageUrl;
    private String questionType;
    private String contentType;
    private ProgrammingLanguage programmingLanguage;
    private Double marks;
    private Integer questionOrder;
    private List<StudentQuestionOptionDTO> options;
}
