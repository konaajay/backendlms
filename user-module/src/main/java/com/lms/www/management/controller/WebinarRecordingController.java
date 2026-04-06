package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.WebinarRecording;
import com.lms.www.management.service.WebinarRecordingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webinar-recordings")
@RequiredArgsConstructor
public class WebinarRecordingController {

    private final WebinarRecordingService recordingService;

    // =========================================================
    // 🎥 UPLOAD RECORDING (URL or FILE)
    // =========================================================
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('WEBINAR_RECORDING_UPLOAD')")
    public ResponseEntity<WebinarRecording> uploadRecording(
            @RequestParam Long webinarId,
            @RequestParam(required = false) String recordingUrl,
            @RequestParam(required = false) MultipartFile videoFile,
            @RequestParam Integer durationMinutes,
            @RequestParam Long uploadedBy) {

        WebinarRecording recording = recordingService.uploadRecording(
                webinarId,
                recordingUrl,
                videoFile,
                durationMinutes,
                uploadedBy);

        return ResponseEntity.status(201).body(recording);
    }

    // =========================================================
    // 🎥 GET RECORDINGS BY WEBINAR
    // =========================================================
    @GetMapping("/webinar/{webinarId}")
    @PreAuthorize("hasAuthority('WEBINAR_RECORDING_VIEW')")
    public ResponseEntity<List<WebinarRecording>> getRecordingsByWebinar(@PathVariable Long webinarId) {

        List<WebinarRecording> recordings = recordingService.getRecordingsByWebinar(webinarId);
        return ResponseEntity.ok(recordings);
    }

    // =========================================================
    // 🎥 GET RECORDING BY ID
    // =========================================================
    @GetMapping("/{recordingId}")
    @PreAuthorize("hasAuthority('WEBINAR_RECORDING_VIEW')")
    public ResponseEntity<WebinarRecording> getRecordingById(@PathVariable Long recordingId) {

        WebinarRecording recording = recordingService.getRecordingById(recordingId);
        return ResponseEntity.ok(recording);
    }

    // =========================================================
    // ❌ DELETE RECORDING
    // =========================================================
    @DeleteMapping("/{recordingId}")
    @PreAuthorize("hasAuthority('WEBINAR_RECORDING_DELETE')")
    public ResponseEntity<String> deleteRecording(@PathVariable Long recordingId) {

        recordingService.deleteRecording(recordingId);
        return ResponseEntity.ok("Webinar recording deleted successfully");
    }
    
    @PutMapping(value = "/{recordingId}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('WEBINAR_RECORDING_UPLOAD')")
    public ResponseEntity<WebinarRecording> updateRecording(
            @PathVariable Long recordingId,
            @RequestParam(required = false) String recordingUrl,
            @RequestParam(required = false) MultipartFile videoFile,
            @RequestParam(required = false) Integer durationMinutes) {

        WebinarRecording recording = recordingService.updateRecording(
                recordingId,
                recordingUrl,
                videoFile,
                durationMinutes);

        return ResponseEntity.ok(recording);
    }
}