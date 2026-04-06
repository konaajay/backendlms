package com.lms.www.management.dashboard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lms.www.management.instructor.dto.InstructorEvaluationRequestDTO;
import com.lms.www.management.instructor.dto.PendingEvaluationProjection;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.ExamQuestion;

public interface InstructorExamService {

    Exam createExam(Long instructorId, Exam exam);

    Exam updateExam(Long instructorId, Long examId, Exam exam);

    void publishExam(Long instructorId, Long examId);

    Page<Exam> getInstructorExams(Long instructorId, Pageable pageable);

    List<PendingEvaluationProjection> getPendingEvaluations(Long instructorId);

    void evaluateResponse(Long instructorId, Long responseId, InstructorEvaluationRequestDTO request);

    void closeExam(Long instructorId, Long examId);

    void deleteExam(Long instructorId, Long examId);

    ExamQuestion addQuestionToSection(Long instructorId, Long examId, ExamQuestion question);

    List<ExamAttempt> getExamAttempts(Long instructorId, Long examId);
}