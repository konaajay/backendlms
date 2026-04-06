package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

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

import com.lms.www.management.enums.WebinarPollStatus;
import com.lms.www.management.model.WebinarPoll;
import com.lms.www.management.model.WebinarPollResponse;
import com.lms.www.management.service.WebinarPollService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webinar-polls")
@RequiredArgsConstructor
public class WebinarPollController {

    private final WebinarPollService pollService;

    // =========================================================
    // 📊 CREATE POLL
    // =========================================================
    @SuppressWarnings("unchecked")
    @PostMapping
    @PreAuthorize("hasAuthority('WEBINAR_POLL_CREATE')")
    public ResponseEntity<WebinarPoll> createPoll(@RequestBody Map<String, Object> payload) {

        if (payload.get("webinarId") == null ||
            payload.get("question") == null ||
            payload.get("options") == null ||
            payload.get("createdBy") == null) {
            throw new IllegalArgumentException("Missing required poll fields");
        }

        Long webinarId = Long.valueOf(payload.get("webinarId").toString());
        String question = payload.get("question").toString();
        List<String> options = (List<String>) payload.get("options");
        Long createdBy = Long.valueOf(payload.get("createdBy").toString());

        WebinarPoll poll = pollService.createPoll(webinarId, question, options, createdBy);
        return ResponseEntity.status(201).body(poll);
    }

    // =========================================================
    // 📊 GET POLLS BY WEBINAR
    // =========================================================
    @GetMapping("/webinar/{webinarId}")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_VIEW')")
    public ResponseEntity<List<WebinarPoll>> getPollsByWebinar(@PathVariable Long webinarId) {

        List<WebinarPoll> polls = pollService.getPollsByWebinar(webinarId);
        return ResponseEntity.ok(polls);
    }

    // =========================================================
    // 🔄 UPDATE POLL STATUS
    // =========================================================
    @PutMapping("/{pollId}/status")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_UPDATE')")
    public ResponseEntity<WebinarPoll> updatePollStatus(
            @PathVariable Long pollId,
            @RequestBody Map<String, String> payload) {

        if (payload.get("status") == null) {
            throw new IllegalArgumentException("Poll status must be provided");
        }

        WebinarPollStatus status = WebinarPollStatus.valueOf(payload.get("status"));
        WebinarPoll poll = pollService.updatePollStatus(pollId, status);

        return ResponseEntity.ok(poll);
    }

    // =========================================================
    // ❌ DELETE POLL
    // =========================================================
    @DeleteMapping("/{pollId}")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_DELETE')")
    public ResponseEntity<String> deletePoll(@PathVariable Long pollId) {

        pollService.deletePoll(pollId);
        return ResponseEntity.ok("Poll deleted successfully");
    }

    // =========================================================
    // ✍️ SUBMIT RESPONSE
    // =========================================================
    @PostMapping("/{pollId}/respond")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_VOTE')")
    public ResponseEntity<WebinarPollResponse> submitResponse(
            @PathVariable Long pollId,
            @RequestBody Map<String, Object> payload) {

        if (payload.get("userId") == null || payload.get("selectedOption") == null) {
            throw new IllegalArgumentException("User ID and selected option are required");
        }

        Long userId = Long.valueOf(payload.get("userId").toString());
        String selectedOption = payload.get("selectedOption").toString();

        WebinarPollResponse response = pollService.submitResponse(pollId, userId, selectedOption);
        return ResponseEntity.status(201).body(response);
    }

    // =========================================================
    // 📈 GET POLL RESULTS
    // =========================================================
    @GetMapping("/{pollId}/results")
    @PreAuthorize("hasAuthority('WEBINAR_POLL_VIEW')")
    public ResponseEntity<Map<String, Long>> getPollResults(@PathVariable Long pollId) {

        Map<String, Long> results = pollService.getPollResults(pollId);
        return ResponseEntity.ok(results);
    }
}