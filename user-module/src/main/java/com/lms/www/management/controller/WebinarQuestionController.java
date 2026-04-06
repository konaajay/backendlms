package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.WebinarQuestion;
import com.lms.www.management.service.WebinarQuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webinar-questions")
@RequiredArgsConstructor
public class WebinarQuestionController {

    private final WebinarQuestionService questionService;

    // =========================================================
    // ❓ ASK A QUESTION
    // =========================================================
    @PostMapping("/ask")
    @PreAuthorize("hasAuthority('WEBINAR_QUESTION_ASK')")
    public ResponseEntity<WebinarQuestion> askQuestion(@RequestBody Map<String, Object> payload) {

        if (payload.get("webinarId") == null ||
            payload.get("senderId") == null ||
            payload.get("senderName") == null ||
            payload.get("question") == null) {
            throw new IllegalArgumentException("Missing required question fields");
        }

        Long webinarId = Long.valueOf(payload.get("webinarId").toString());
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        String senderName = payload.get("senderName").toString();
        String question = payload.get("question").toString();

        WebinarQuestion webinarQuestion =
                questionService.askQuestion(webinarId, senderId, senderName, question);

        return ResponseEntity.status(201).body(webinarQuestion);
    }

    // =========================================================
    // ✅ ANSWER A QUESTION
    // =========================================================
    @PutMapping("/{id}/answer")
    @PreAuthorize("hasAuthority('WEBINAR_QUESTION_ANSWER')")
    public ResponseEntity<WebinarQuestion> answerQuestion(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {

        if (payload.get("answer") == null) {
            throw new IllegalArgumentException("Answer must not be empty");
        }

        String answer = payload.get("answer").toString();

        WebinarQuestion webinarQuestion =
                questionService.answerQuestion(id, answer);

        return ResponseEntity.ok(webinarQuestion);
    }

    // =========================================================
    // ❓ GET ALL QUESTIONS BY WEBINAR
    // =========================================================
    @GetMapping("/webinar/{webinarId}")
    @PreAuthorize("hasAuthority('WEBINAR_QUESTION_VIEW')")
    public ResponseEntity<List<WebinarQuestion>> getQuestionsByWebinar(@PathVariable Long webinarId) {

        List<WebinarQuestion> questions =
                questionService.getQuestionsByWebinar(webinarId);

        return ResponseEntity.ok(questions);
    }
}