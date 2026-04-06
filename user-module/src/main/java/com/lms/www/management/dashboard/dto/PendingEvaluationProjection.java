package com.lms.www.management.dashboard.dto;

public interface PendingEvaluationProjection {

    Long getResponseId();

    Long getAttemptId();

    String getDescriptiveAnswer();

    String getCodingSubmissionCode();

    String getEvaluationType();

    Long getExamId();

    String getExamTitle();

    Long getQuestionId();

    Double getMarksAwarded(); // optional

    Double getMaxMarks(); // optional
}