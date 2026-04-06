package com.lms.www.management.service;

import com.lms.www.management.model.ExamProctoring;

public interface ExamProctoringService {

    ExamProctoring saveProctoring(Long examId, ExamProctoring proctoring);

    ExamProctoring getProctoringByExamId(Long examId);
}
