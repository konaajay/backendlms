package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.WebinarPollResponse;
import com.lms.www.management.service.WebinarPollResponseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webinar-poll-responses")
@RequiredArgsConstructor
public class WebinarPollResponseController {

    private final WebinarPollResponseService responseService;

    // =========================================================
    // ✍️ SUBMIT VOTE
    // =========================================================
    @PostMapping("/vote")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_RESPONSE_SUBMIT')")
    public ResponseEntity<WebinarPollResponse> submitVote(@RequestBody Map<String, Object> payload) {

        if (payload.get("pollId") == null || payload.get("selectedOption") == null) {
            throw new IllegalArgumentException("Poll ID and selected option are required");
        }

        Long pollId = Long.valueOf(payload.get("pollId").toString());
        String selectedOption = payload.get("selectedOption").toString();

        Long userId = payload.get("userId") != null
                ? Long.valueOf(payload.get("userId").toString())
                : null;

        Long participantId = payload.get("participantId") != null
                ? Long.valueOf(payload.get("participantId").toString())
                : null;

        WebinarPollResponse response =
                responseService.submitVote(pollId, userId, participantId, selectedOption);

        return ResponseEntity.status(201).body(response);
    }

    // =========================================================
    // 📋 GET ALL POLL RESPONSES
    // =========================================================
    @GetMapping("/poll/{pollId}")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_RESPONSE_VIEW')")
    public ResponseEntity<List<WebinarPollResponse>> getPollResponses(@PathVariable Long pollId) {

        List<WebinarPollResponse> responses =
                responseService.getPollResponses(pollId);

        return ResponseEntity.ok(responses);
    }

    // =========================================================
    // 📊 GET POLL RESULT STATISTICS
    // =========================================================
    @GetMapping("/poll/{pollId}/results")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_RESPONSE_VIEW')")
    public ResponseEntity<Map<String, Long>> getPollResults(@PathVariable Long pollId) {

        Map<String, Long> results =
                responseService.getPollResultStatistics(pollId);

        return ResponseEntity.ok(results);
    }
}