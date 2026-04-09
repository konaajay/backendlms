package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.service.EvaluationAsyncService;
import com.lms.www.management.service.ExamAttemptService;
import com.lms.www.management.util.SecurityUtil;

@RestController
@RequestMapping("/api/exams/{examId}/attempts")
public class ExamAttemptController {

        private final ExamAttemptService examAttemptService;
        private final EvaluationAsyncService evaluationAsyncService;
        private final SecurityUtil securityUtil;

        public ExamAttemptController(
                        ExamAttemptService examAttemptService,
                        EvaluationAsyncService evaluationAsyncService,
                        SecurityUtil securityUtil) {

                this.examAttemptService = examAttemptService;
                this.evaluationAsyncService = evaluationAsyncService;
                this.securityUtil = securityUtil;
        }

        @PostMapping("/start")
        @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
        public ResponseEntity<ExamAttempt> startAttempt(
                        @PathVariable Long examId,
                        Authentication authentication) {

                Long studentId = extractStudentId(authentication);

                return ResponseEntity.ok(
                                examAttemptService.startAttempt(examId, studentId));
        }

        @PostMapping("/{attemptId}/submit")
        @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
        public ResponseEntity<ExamAttempt> submitAttempt(
                        @PathVariable Long attemptId,
                        Authentication authentication) {

                Long studentId = extractStudentId(authentication);

                ExamAttempt attempt = examAttemptService.submitAttempt(attemptId, studentId);

                evaluationAsyncService.evaluateAttemptAsync(
                                attempt.getAttemptId());

                return ResponseEntity.ok(attempt);
        }

        @PostMapping("/{attemptId}/auto-submit")
        @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
        public ResponseEntity<ExamAttempt> autoSubmitAttempt(
                        @PathVariable Long attemptId,
                        Authentication authentication) {

                Long studentId = extractStudentId(authentication);

                ExamAttempt attempt = examAttemptService.autoSubmitAttempt(attemptId, studentId);

                evaluationAsyncService.evaluateAttemptAsync(
                                attempt.getAttemptId());

                return ResponseEntity.ok(attempt);
        }

        @PostMapping("/{attemptId}/evaluate")
        @PreAuthorize("hasAnyAuthority('EXAM_RESPONSE_EVALUATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
        public ResponseEntity<?> evaluateAttempt(
                        @PathVariable Long attemptId) {

                evaluationAsyncService.evaluateAttemptAsync(attemptId);

                return ResponseEntity.ok(
                                java.util.Map.of("status", "Evaluation started in background"));
        }

        @GetMapping("/{attemptId}")
        @PreAuthorize("hasAnyAuthority('EXAM_ATTEMPT_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
        public ResponseEntity<ExamAttempt> getAttempt(
                        @PathVariable Long attemptId,
                        Authentication authentication) {

                Long studentId = extractStudentId(authentication);

                return ResponseEntity.ok(
                                examAttemptService.getAttemptById(attemptId, studentId));
        }

        @GetMapping("/{attemptId}/result")
        @PreAuthorize("hasAnyAuthority('EXAM_ATTEMPT_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
        public ResponseEntity<?> getResult(
                        @PathVariable Long attemptId,
                        Authentication authentication) {

                Long studentId = extractStudentId(authentication);

                return ResponseEntity.ok(
                                examAttemptService.getResult(attemptId, studentId));
        }

        @GetMapping("/all")
        @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
        public ResponseEntity<java.util.List<ExamAttempt>> getAttemptsByExam(
                        @PathVariable Long examId) {

                return ResponseEntity.ok(
                                examAttemptService.getAttemptsByExam(examId));
        }

        @GetMapping("/{attemptId}/status")
        @PreAuthorize("hasAnyAuthority('EXAM_ATTEMPT_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
        public ResponseEntity<?> getAttemptStatus(
                        @PathVariable Long attemptId,
                        Authentication authentication) {

                Long studentId = extractStudentId(authentication);

                ExamAttempt attempt = examAttemptService.getAttemptById(attemptId, studentId);

                return ResponseEntity.ok(
                                java.util.Map.of("status", attempt.getStatus()));
        }

        private Long extractStudentId(Authentication authentication) {
                return securityUtil.getUserId();
        }
}