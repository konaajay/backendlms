package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExamResponse;
import com.lms.www.management.service.ExamResponseService;
import com.lms.www.management.util.SecurityUtil;

@RestController
@RequestMapping("/api/exam-attempts/{attemptId}/responses")
public class ExamResponseController {

        private final ExamResponseService examResponseService;
        private final SecurityUtil securityUtil;

        public ExamResponseController(ExamResponseService examResponseService, SecurityUtil securityUtil) {
                this.examResponseService = examResponseService;
                this.securityUtil = securityUtil;
        }

        // ================= SAVE / AUTOSAVE RESPONSE =================
        @PostMapping
        @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
        public ResponseEntity<ExamResponse> saveResponse(
                        @PathVariable Long attemptId,
                        @RequestBody ExamResponse request,
                        Authentication authentication) {

                extractStudentId(authentication);

                return ResponseEntity.ok(
                                examResponseService.saveOrUpdateResponse(
                                                attemptId,
                                                request.getExamQuestionId(),
                                                request.getQuestionId(),
                                                request.getSelectedOptionId(),
                                                request.getDescriptiveAnswer(),
                                                request.getCodingSubmissionCode()));
        }

        // ================= GET RESPONSES =================
        @GetMapping
        @PreAuthorize("hasAnyAuthority('EXAM_ATTEMPT_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
        public ResponseEntity<List<ExamResponse>> getResponses(
                        @PathVariable Long attemptId,
                        Authentication authentication) {

                extractStudentId(authentication);

                return ResponseEntity.ok(
                                examResponseService.getResponsesByAttempt(attemptId));
        }

        // ================= AUTO EVALUATE MCQ =================
        @PostMapping("/auto-evaluate")
        @PreAuthorize("hasAnyAuthority('EXAM_RESPONSE_EVALUATE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
        public ResponseEntity<?> autoEvaluateMcq(
                        @PathVariable Long attemptId) {

                examResponseService.autoEvaluateMcq(attemptId);

                return ResponseEntity.ok(
                                Map.of("status", "MCQ evaluation completed"));
        }

        // ================= MANUAL EVALUATION =================
        @PostMapping("/{responseId}/evaluate")
        @PreAuthorize("hasAnyAuthority('EXAM_RESPONSE_EVALUATE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
        public ResponseEntity<ExamResponse> evaluateResponse(
                        @PathVariable Long attemptId,
                        @PathVariable Long responseId,
                        @RequestParam Double marks,
                        Authentication authentication) {

                extractEvaluatorId(authentication);

                return ResponseEntity.ok(
                                examResponseService.evaluateResponse(
                                                attemptId,
                                                responseId,
                                                marks));
        }

        // ================= CODING RESPONSES =================
        @GetMapping("/coding-responses")
        @PreAuthorize("hasAnyAuthority('EXAM_RESPONSE_EVALUATE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
        public ResponseEntity<List<Map<String, Object>>> getCodingResponses(
                        @PathVariable Long attemptId) {

                return ResponseEntity.ok(
                                examResponseService.getCodingResponsesForEvaluation(attemptId));
        }

        private Long extractStudentId(Authentication authentication) {
                return securityUtil.getUserId();
        }

        private Long extractEvaluatorId(Authentication authentication) {
                return securityUtil.getInstructorId();
        }
}