package com.lms.www.management.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.WebinarRecording;

public interface WebinarRecordingService {

    WebinarRecording uploadRecording(Long webinarId,
                                      String recordingUrl,
                                      MultipartFile videoFile,
                                      Integer durationMinutes,
                                      Long uploadedBy);

    List<WebinarRecording> getRecordingsByWebinar(Long webinarId);

    WebinarRecording getRecordingById(Long recordingId);

    void deleteRecording(Long recordingId);
    
    WebinarRecording updateRecording(Long recordingId,
            String recordingUrl,
            MultipartFile videoFile,
            Integer durationMinutes);
}