package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExamGrading;
import com.lms.www.management.service.ExamGradingService;

@RestController
@RequestMapping("/api/exams/{examId}/grading")
public class ExamGradingController {

    private final ExamGradingService examGradingService;

    public ExamGradingController(ExamGradingService examGradingService) {
        this.examGradingService = examGradingService;
    }

    // Create / update grading rules (DRAFT only)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXAM_GRADING_UPDATE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamGrading> saveGrading(
            @PathVariable Long examId,
            @RequestBody ExamGrading grading) {

        return ResponseEntity.ok(
                examGradingService.saveGrading(examId, grading)
        );
    }

    // Get grading rules (read-only)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXAM_GRADING_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamGrading> getGrading(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examGradingService.getGradingByExamId(examId)
        );
    }
}