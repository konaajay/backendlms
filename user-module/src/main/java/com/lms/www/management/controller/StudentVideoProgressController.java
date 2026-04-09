package com.lms.www.management.controller;

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

import com.lms.www.management.service.StudentVideoProgressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class StudentVideoProgressController {

    private final StudentVideoProgressService progressService;

    /**
     * VIDEO HEARTBEAT UPDATE
     * Only STUDENT can update progress.
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('VIDEO_PROGRESS_UPDATE')")
    public ResponseEntity<Map<String, Object>> updateWatchProgress(
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {

        try {

            // 🔒 In production, extract studentId from JWT instead of payload
            Long studentId = Long.valueOf(payload.get("studentId").toString());

            Long sessionId =
                    Long.valueOf(payload.get("sessionId").toString());

            Long sessionContentId =
                    Long.valueOf(payload.get("sessionContentId").toString());

            Long currentPosition =
                    Long.valueOf(payload.get("currentPosition").toString());

            Map<String, Object> result =
                    progressService.updateWatchProgress(
                            studentId,
                            sessionId,
                            sessionContentId,
                            currentPosition
                    );

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", result
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * VIEW SESSION PROGRESS
     * - STUDENT → can view only his own progress
     * - ADMIN / INSTRUCTOR → can view any student's progress
     */
    @GetMapping("/session/{sessionId}/student/{studentId}")
    @PreAuthorize(
            "hasAuthority('VIDEO_PROGRESS_VIEW') and " +
            "(hasRole('ADMIN') or hasRole('INSTRUCTOR') " +
            "or #studentId == authentication.principal.id)"
    )
    public ResponseEntity<Map<String, Object>> getSessionProgress(
            @PathVariable Long sessionId,
            @PathVariable Long studentId) {

        try {

            Map<String, Object> result =
                    progressService.getSessionProgress(studentId, sessionId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", result
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
}