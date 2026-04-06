package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.dashboard.dto.StudentExamAttemptResultDTO;
import com.lms.www.management.dashboard.dto.StudentExamResponseSaveDTO;
import com.lms.www.management.dashboard.dto.StudentExamSectionDTO;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.ExamResponse;
import com.lms.www.management.service.StudentExamService;
import com.lms.www.management.util.SecurityUtil;
import com.lms.www.fee.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/exams")
@RequiredArgsConstructor
public class StudentExamController {

    private final StudentExamService studentExamService;
    private final SecurityUtil securityUtil;

    // =========================
    // VIEW EXAMS
    // =========================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXAM_STUDENT_VIEW','EXAM_ATTEMPT_VIEW')")
    public ResponseEntity<ApiResponse<List<Exam>>> getAvailableExams() {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.getAvailableExamsForStudent(studentId)));
    }

    @GetMapping("/{examId}")
    @PreAuthorize("hasAnyAuthority('EXAM_STUDENT_VIEW','EXAM_ATTEMPT_VIEW')")
    public ResponseEntity<ApiResponse<Exam>> getExamDetails(@PathVariable Long examId) {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.getExamDetails(examId, studentId)));
    }

    @GetMapping("/{examId}/questions")
    // @PreAuthorize("hasAuthority('EXAM_STUDENT_VIEW')")
    public ResponseEntity<ApiResponse<List<StudentExamSectionDTO>>> getExamQuestions(@PathVariable Long examId) {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.getExamQuestions(examId, studentId)));
    }

    @GetMapping("/{examId}/active-attempt")
    @PreAuthorize("hasAuthority('EXAM_ATTEMPT_VIEW')")
    public ResponseEntity<ApiResponse<ExamAttempt>> getActiveAttempt(@PathVariable Long examId) {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.getActiveAttempt(examId, studentId)));
    }

    // =========================
    // START EXAM
    // =========================
    @PostMapping("/{examId}/start")
    @PreAuthorize("hasAuthority('EXAM_ATTEMPT_START')")
    public ResponseEntity<ApiResponse<ExamAttempt>> startExamAttempt(@PathVariable Long examId) {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.startExamAttempt(examId, studentId)));
    }

    // =========================
    // SAVE RESPONSE
    // =========================
    @PostMapping("/{examId}/save")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_SAVE')")
    public ResponseEntity<ApiResponse<ExamResponse>> saveExamResponse(
            @PathVariable Long examId,
            @RequestBody StudentExamResponseSaveDTO saveDTO) {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.saveExamResponse(examId, studentId, saveDTO)));
    }

    // =========================
    // SUBMIT EXAM
    // =========================
    @PostMapping("/{examId}/submit")
    @PreAuthorize("hasAuthority('EXAM_ATTEMPT_SUBMIT')")
    public ResponseEntity<ApiResponse<StudentExamAttemptResultDTO>> submitExamAttempt(@PathVariable Long examId) {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.submitExamAttempt(examId, studentId)));
    }

    // =========================
    // VIEW ATTEMPTS
    // =========================
    @GetMapping("/attempts")
    @PreAuthorize("hasAuthority('EXAM_ATTEMPT_VIEW')")
    public ResponseEntity<ApiResponse<List<StudentExamAttemptResultDTO>>> getStudentExamAttempts() {
        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(ApiResponse.success(studentExamService.getStudentExamAttempts(studentId)));
    }
}