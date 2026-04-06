package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.Exam;
import com.lms.www.management.service.ExamService;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXAM_CREATE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        return new ResponseEntity<>(examService.createExam(exam), HttpStatus.CREATED);
    }

    // PUBLISH
    @PutMapping("/{examId}/publish")
    @PreAuthorize("hasAnyAuthority('EXAM_PUBLISH', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Exam> publishExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.publishExam(examId));
    }

    // CLOSE
    @PutMapping("/{examId}/close")
    @PreAuthorize("hasAnyAuthority('EXAM_CLOSE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Exam> closeExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.closeExam(examId));
    }

    // GET
    @GetMapping("/{examId}")
    @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Exam> getExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(
                examService.getAllExams());
    }
    // ============ DELETE APIs ============

    // SOFT DELETE
    @DeleteMapping("/{examId}")
    @PreAuthorize("hasAnyAuthority('EXAM_DELETE', 'ROLE_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<Void> softDelete(@PathVariable Long examId) {
        examService.softDeleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    // RESTORE
    @PutMapping("/{examId}/restore")
    @PreAuthorize("hasAnyAuthority('EXAM_RESTORE', 'ROLE_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<Void> restore(@PathVariable Long examId) {
        examService.restoreExam(examId);
        return ResponseEntity.noContent().build();
    }

    // HARD DELETE
    @DeleteMapping("/{examId}/hard")
    @PreAuthorize("hasAnyAuthority('EXAM_HARD_DELETE', 'ROLE_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<Void> hardDelete(@PathVariable Long examId) {
        examService.hardDeleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasAnyAuthority('EXAM_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<List<Exam>> getSoftDeletedExams() {
        return ResponseEntity.ok(examService.getSoftDeletedExams());
    }
}
