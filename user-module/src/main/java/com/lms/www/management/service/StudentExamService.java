package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.dashboard.dto.StudentExamAttemptResultDTO;
import com.lms.www.management.dashboard.dto.StudentExamResponseSaveDTO;
import com.lms.www.management.dashboard.dto.StudentExamSectionDTO;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.ExamResponse;

public interface StudentExamService {

    List<Exam> getAvailableExamsForStudent(Long studentId);

    Exam getExamDetails(Long examId, Long studentId);

    List<StudentExamSectionDTO> getExamQuestions(Long examId, Long studentId);

    ExamAttempt startExamAttempt(Long examId, Long studentId);

    ExamAttempt getActiveAttempt(Long examId, Long studentId);

    ExamResponse saveExamResponse(Long examId, Long studentId, StudentExamResponseSaveDTO saveDTO);

    StudentExamAttemptResultDTO submitExamAttempt(Long examId, Long studentId);

    List<StudentExamAttemptResultDTO> getStudentExamAttempts(Long studentId);

    StudentExamAttemptResultDTO getSpecificAttemptResult(Long examId, Long attemptId, Long studentId);
}