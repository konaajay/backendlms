package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExamProctoring;
import com.lms.www.management.service.ExamProctoringService;

@RestController
@RequestMapping("/api/exams/{examId}/proctoring")
public class ExamProctoringController {

    private final ExamProctoringService examProctoringService;

    public ExamProctoringController(ExamProctoringService examProctoringService) {
        this.examProctoringService = examProctoringService;
    }

    // Create / update proctoring rules (DRAFT only)
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasAnyAuthority('EXAM_PROCTORING_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamProctoring> saveProctoring(
            @PathVariable Long examId,
            @RequestBody ExamProctoring proctoring) {

        return ResponseEntity.ok(
                examProctoringService.saveProctoring(examId, proctoring)
        );
    }

    // Get proctoring rules (read-only)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXAM_PROCTORING_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamProctoring> getProctoring(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examProctoringService.getProctoringByExamId(examId)
        );
    }
}