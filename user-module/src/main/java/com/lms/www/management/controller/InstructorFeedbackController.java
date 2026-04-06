package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.SessionFeedback;
import com.lms.www.management.service.SessionFeedbackService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/instructor/feedback")
@RequiredArgsConstructor
public class InstructorFeedbackController {

    private final SessionFeedbackService sessionFeedbackService;

    @PostMapping("/submit")
    public ResponseEntity<SessionFeedback> submitFeedback(
            @RequestParam Long sessionId,
            @RequestParam Long instructorId,
            @RequestParam Long studentId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {

        SessionFeedback feedback = sessionFeedbackService.submitFeedback(sessionId, instructorId, studentId, rating,
                comment);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/average/{instructorId}")
    public ResponseEntity<Double> getMyAverageFeedback(@PathVariable Long instructorId) {
        Double average = sessionFeedbackService.getTrainerAverageFromStudents(instructorId);
        return ResponseEntity.ok(average);
    }
}