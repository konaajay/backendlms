package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExamSettings;
import com.lms.www.management.service.ExamSettingsService;

@RestController
@RequestMapping("/api/exams/{examId}/settings")
public class ExamSettingsController {

    private final ExamSettingsService examSettingsService;

    public ExamSettingsController(ExamSettingsService examSettingsService) {
        this.examSettingsService = examSettingsService;
    }

    // Create / update exam settings (DRAFT only)
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasAnyAuthority('EXAM_SETTINGS_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamSettings> saveSettings(
            @PathVariable Long examId,
            @RequestBody ExamSettings settings) {

        return ResponseEntity.ok(
                examSettingsService.saveSettings(examId, settings)
        );
    }

    // Get exam settings (read-only)
    @GetMapping
    @PreAuthorize("hasAnyAuthority('EXAM_SETTINGS_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamSettings> getSettings(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examSettingsService.getSettingsByExamId(examId)
        );
    }

    // Toggle MCQ option shuffle
    @PatchMapping("/shuffle-options")
    @PreAuthorize("hasAnyAuthority('EXAM_SETTINGS_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamSettings> updateShuffleOptions(
            @PathVariable Long examId,
            @RequestParam Boolean shuffle) {

        return ResponseEntity.ok(
                examSettingsService.updateShuffleOptions(examId, shuffle)
        );
    }
}