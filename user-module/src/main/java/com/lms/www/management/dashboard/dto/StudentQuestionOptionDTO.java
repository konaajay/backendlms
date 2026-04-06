package com.lms.www.management.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentQuestionOptionDTO {
    private Long optionId;
    private Long questionId;
    private String optionText;
    private String optionImageUrl;
}
