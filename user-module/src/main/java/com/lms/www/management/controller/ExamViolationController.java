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
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExamViolation;
import com.lms.www.management.service.ExamViolationService;
import com.lms.www.management.util.SecurityUtil;


@RestController
@RequestMapping("/api/exam-attempts/{attemptId}/violations")
public class ExamViolationController {

    private final ExamViolationService examViolationService;
    private final SecurityUtil securityUtil;

    public ExamViolationController(ExamViolationService examViolationService, SecurityUtil securityUtil) {
        this.examViolationService = examViolationService;
        this.securityUtil = securityUtil;
    }

    // ================= RECORD VIOLATION =================
    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXAM_VIOLATION_RECORD', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamViolation> recordViolation(
            @PathVariable Long attemptId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        extractStudentId(authentication);

        return ResponseEntity.ok(
                examViolationService.recordViolation(
                        attemptId,
                        request.get("violationType")));
    }

    // ================= GET VIOLATIONS (AUDIT VIEW) =================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXAM_VIOLATION_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<ExamViolation>> getViolations(
            @PathVariable Long attemptId,
            Authentication authentication) {

        extractViewer(authentication);

        return ResponseEntity.ok(
                examViolationService.getViolationsByAttempt(attemptId));
    }

    // ================= GET VIOLATION COUNT (SYSTEM) =================
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getViolationCount(
            @PathVariable Long attemptId,
            Authentication authentication) {

        extractSystem(authentication);

        long count = examViolationService.getViolationCount(attemptId);
        return ResponseEntity.ok(
                Map.of("attemptId", attemptId, "violationCount", count));
    }

    // ================= TEMP ID EXTRACTION =================
    private Long extractStudentId(Authentication authentication) {
        return securityUtil.getUserId();
    }

    private void extractViewer(Authentication authentication) {
        // TEMP – admin / instructor
    }

    private void extractSystem(Authentication authentication) {
        // TEMP – scheduler / system call
    }
}
