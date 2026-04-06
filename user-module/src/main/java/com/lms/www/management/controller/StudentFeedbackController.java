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
@RequestMapping("/api/student/feedback")
@RequiredArgsConstructor
public class StudentFeedbackController {

    private final SessionFeedbackService sessionFeedbackService;

    @PostMapping("/submit")
    public ResponseEntity<SessionFeedback> submitFeedback(
            @RequestParam Long sessionId,
            @RequestParam Long studentId,
            @RequestParam Long instructorId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {

        SessionFeedback feedback = sessionFeedbackService.submitFeedback(sessionId, studentId, instructorId, rating,
                comment);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/average/{studentId}")
    public ResponseEntity<Double> getMyAverageFeedback(@PathVariable Long studentId) {
        Double average = sessionFeedbackService.getAverageRatingForUser(studentId);
        return ResponseEntity.ok(average);
    }
}