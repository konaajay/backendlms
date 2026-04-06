package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.QuestionSection;
import com.lms.www.management.service.QuestionSectionService;

@RestController
@RequestMapping("/api/sections")
public class QuestionSectionController {

    private final QuestionSectionService questionSectionService;

    public QuestionSectionController(
            QuestionSectionService questionSectionService) {
        this.questionSectionService = questionSectionService;
    }

    // ================= CREATE SECTION =================
    @PostMapping
    @PreAuthorize("hasAnyAuthority('QUESTION_SECTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<QuestionSection> createSection(
            @RequestBody QuestionSection request) {

        return ResponseEntity.ok(
                questionSectionService.createSection(
                        request.getSectionName(),
                        request.getSectionDescription(),
                        request.getShuffleQuestions()
                )
        );
    }

    // ================= GET ALL SECTIONS =================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('QUESTION_SECTION_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<QuestionSection>> getAllSections() {
        return ResponseEntity.ok(
                questionSectionService.getAllSections()
        );
    }

    // ================= GET SECTION BY ID =================
    @GetMapping("/{sectionId}")
    @PreAuthorize("hasAnyAuthority('QUESTION_SECTION_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<QuestionSection> getSection(
            @PathVariable Long sectionId) {

        return ResponseEntity.ok(
                questionSectionService.getSectionById(sectionId)
        );
    }

    // ================= DELETE SECTION =================
    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAnyAuthority('QUESTION_SECTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long sectionId) {

        questionSectionService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }
}