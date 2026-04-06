package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.Exam;

public interface ExamService {

    Exam createExam(Exam exam);

    Exam publishExam(Long examId);

    Exam closeExam(Long examId);

    Exam getExamById(Long examId);

    List<Exam> getAllExams();

    // DELETE OPERATIONS
    void softDeleteExam(Long examId);

    void restoreExam(Long examId);

    void hardDeleteExam(Long examId);

    List<Exam> getSoftDeletedExams();
}
