package com.lms.www.management.dashboard.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentExamSectionDTO {
    private Long examSectionId;
    private Long sectionId;
    private String sectionName;
    private String sectionDescription;
    private Integer sectionOrder;
    private Boolean shuffleQuestions;
    private List<StudentExamQuestionDTO> questions;
}
