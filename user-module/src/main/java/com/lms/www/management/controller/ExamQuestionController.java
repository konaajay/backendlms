package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.model.ExamQuestion;
import com.lms.www.management.service.ExamQuestionService;

@RestController
@RequestMapping("/api/exam-sections/{examSectionId}/questions")
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    public ExamQuestionController(ExamQuestionService examQuestionService) {
        this.examQuestionService = examQuestionService;
    }

    // ================= ADD QUESTIONS =================
    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXAM_QUESTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<ExamQuestion>> addQuestions(
            @PathVariable Long examSectionId,
            @RequestBody List<ExamQuestion> questions) {

        return ResponseEntity.ok(
                examQuestionService.addQuestions(examSectionId, questions));
    }

    // ================= GET QUESTIONS =================
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Map<String, Object>>> getQuestions(
            @PathVariable Long examSectionId) {

        return ResponseEntity.ok(
                examQuestionService.getQuestionsForSection(examSectionId));
    }

    // ================= UPDATE MARKS / ORDER =================
    @PutMapping("/{examQuestionId}")
    @PreAuthorize("hasAnyAuthority('EXAM_QUESTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable Long examSectionId,
            @PathVariable Long examQuestionId,
            @RequestBody ExamQuestion request) {

        examQuestionService.updateExamQuestion(
                examSectionId, examQuestionId, request);

        return ResponseEntity.noContent().build();
    }

    // ================= REMOVE QUESTION =================
    @DeleteMapping("/{examQuestionId}")
    @PreAuthorize("hasAnyAuthority('EXAM_QUESTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> removeQuestion(
            @PathVariable Long examQuestionId) {

        examQuestionService.removeExamQuestion(examQuestionId);
        return ResponseEntity.noContent().build();
    }
}