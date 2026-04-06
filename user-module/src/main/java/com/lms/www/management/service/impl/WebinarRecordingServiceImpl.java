package com.lms.www.management.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.enums.WebinarStatus;
import com.lms.www.management.model.Webinar;
import com.lms.www.management.model.WebinarRecording;
import com.lms.www.management.repository.WebinarRecordingRepository;
import com.lms.www.management.repository.WebinarRepository;
import com.lms.www.management.service.WebinarRecordingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WebinarRecordingServiceImpl implements WebinarRecordingService {

    private final WebinarRecordingRepository recordingRepository;
    private final WebinarRepository webinarRepository;

    @Override
    public WebinarRecording uploadRecording(Long webinarId,
                                             String recordingUrl,
                                             MultipartFile videoFile,
                                             Integer durationMinutes,
                                             Long uploadedBy) {

        if ((recordingUrl == null || recordingUrl.trim().isEmpty()) && (videoFile == null || videoFile.isEmpty())) {
            throw new IllegalArgumentException("Either recording URL or video file must be provided");
        }

        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("Recording duration must be positive");
        }

        if (uploadedBy == null) {
            throw new IllegalArgumentException("Uploader must be specified");
        }

        Webinar webinar = webinarRepository.findById(webinarId)
                .orElseThrow(() -> new RuntimeException("Webinar not found with id: " + webinarId));

        if (webinar.getStatus() != WebinarStatus.COMPLETED) {
            throw new IllegalStateException("Recordings can only be uploaded for COMPLETED webinars");
        }

        String finalRecordingUrl = recordingUrl;

        // Handle device file upload
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();

                Path uploadPath = Paths.get("uploads/webinars/");
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(videoFile.getInputStream(), filePath);

                finalRecordingUrl = "/uploads/webinars/" + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Failed to store video file", e);
            }
        }

        WebinarRecording recording = WebinarRecording.builder()
                .webinar(webinar)
                .recordingUrl(finalRecordingUrl)
                .durationMinutes(durationMinutes)
                .uploadedBy(uploadedBy)
                .build();

        return recordingRepository.save(recording);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarRecording> getRecordingsByWebinar(Long webinarId) {

        if (!webinarRepository.existsById(webinarId)) {
            throw new RuntimeException("Webinar not found with id: " + webinarId);
        }

        return recordingRepository.findByWebinar_WebinarId(webinarId);
    }

    @Override
    @Transactional(readOnly = true)
    public WebinarRecording getRecordingById(Long recordingId) {

        return recordingRepository.findById(recordingId)
                .orElseThrow(() -> new RuntimeException("Recording not found with id: " + recordingId));
    }

    @Override
    public void deleteRecording(Long recordingId) {

        if (!recordingRepository.existsById(recordingId)) {
            throw new RuntimeException("Recording not found with id: " + recordingId);
        }

        recordingRepository.deleteById(recordingId);
    }
    
    @Override
    public WebinarRecording updateRecording(Long recordingId,
                                             String recordingUrl,
                                             MultipartFile videoFile,
                                             Integer durationMinutes) {

        WebinarRecording recording = recordingRepository.findById(recordingId)
                .orElseThrow(() -> new RuntimeException("Recording not found with id: " + recordingId));

        if (durationMinutes != null && durationMinutes <= 0) {
            throw new IllegalArgumentException("Recording duration must be positive");
        }

        String finalUrl = recording.getRecordingUrl();

        if (recordingUrl != null && !recordingUrl.trim().isEmpty()) {
            finalUrl = recordingUrl;
        }

        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();

                Path uploadPath = Paths.get("uploads/webinars/");
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(videoFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                finalUrl = "/uploads/webinars/" + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Failed to store video file", e);
            }
        }

        if (durationMinutes != null) {
            recording.setDurationMinutes(durationMinutes);
        }

        recording.setRecordingUrl(finalUrl);

        return recordingRepository.save(recording);
    }
}