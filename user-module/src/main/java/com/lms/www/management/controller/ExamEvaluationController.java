package com.lms.www.management.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.service.ExamAttemptService;
import com.lms.www.management.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/instructor/exams/evaluation")
@RequiredArgsConstructor
public class ExamEvaluationController {

    private final ExamAttemptService examAttemptService;
    private final SecurityUtil securityUtil;

    @GetMapping("/attempts")
    @PreAuthorize("hasAnyAuthority('EXAM_RESPONSE_EVALUATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<ExamAttempt>> getAttemptsForEvaluation() {
        Long instructorId = securityUtil.getUserId();
        return ResponseEntity.ok(examAttemptService.getAttemptsByInstructor(instructorId));
    }

    @GetMapping("/attempts/{attemptId}")
    @PreAuthorize("hasAnyAuthority('EXAM_RESPONSE_EVALUATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamAttempt> getAttemptForEvaluation(@PathVariable Long attemptId) {
        // Using system level check as instructors should be able to see any attempt assigned to their exams
        return ResponseEntity.ok(examAttemptService.getAttemptByIdForSystem(attemptId));
    }
}
