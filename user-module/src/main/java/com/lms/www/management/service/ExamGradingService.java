package com.lms.www.management.service;

import com.lms.www.management.model.ExamGrading;

public interface ExamGradingService {

    ExamGrading saveGrading(Long examId, ExamGrading grading);

    ExamGrading getGradingByExamId(Long examId);
}
