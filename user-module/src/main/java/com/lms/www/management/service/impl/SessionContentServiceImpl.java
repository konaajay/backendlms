package com.lms.www.management.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Session;
import com.lms.www.management.model.SessionContent;
import com.lms.www.management.repository.SessionContentRepository;
import com.lms.www.management.repository.SessionRepository;
import com.lms.www.management.service.SessionContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionContentServiceImpl implements SessionContentService {

    private final SessionContentRepository sessionContentRepository;
    private final SessionRepository sessionRepository;

    // ================= CREATE =================
    @Override
    public SessionContent createSessionContent(Long sessionId, SessionContent sessionContent) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found"));

        sessionContent.setSessionId(session.getSessionId());
        return sessionContentRepository.save(sessionContent);
    }

    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public SessionContent getSessionContentById(Long sessionContentId) {

        return sessionContentRepository.findById(sessionContentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session content not found"));
    }

    // ================= GET BY SESSION =================
    @Override
    @Transactional(readOnly = true)
    public List<SessionContent> getContentsBySessionId(Long sessionId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found"));

        // Retrieve both ACTIVE and legacy NULL status records
        return sessionContentRepository
                .findBySessionIdAndStatusIn(session.getSessionId(), List.of("ACTIVE", "PENDING", "PROCESSING", "DONE"));
        // Alternatively, if the repository doesn't have In, I'll update it. 
    }

    // ================= UPDATE =================
    @Override
    public SessionContent updateSessionContent(
            Long sessionContentId,
            SessionContent updatedContent) {

        SessionContent existing =
                sessionContentRepository.findById(sessionContentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Session content not found"));

        if (updatedContent.getTitle() != null)
            existing.setTitle(updatedContent.getTitle());

        if (updatedContent.getDescription() != null)
            existing.setDescription(updatedContent.getDescription());

        if (updatedContent.getContentType() != null)
            existing.setContentType(updatedContent.getContentType());

        if (updatedContent.getFileUrl() != null) {
            existing.setFileUrl(updatedContent.getFileUrl());

                try {
                    String projectRoot = System.getProperty("user.dir");
                    String absolutePath = projectRoot +
                            existing.getFileUrl().replace("/", java.io.File.separator);

                    ProcessBuilder pb = new ProcessBuilder(
                            "C:\\ffmpeg\\bin\\ffprobe.exe",
                            "-v", "error",
                            "-show_entries", "format=duration",
                            "-of", "default=noprint_wrappers=1:nokey=1",
                            absolutePath
                    );

                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    java.io.BufferedReader reader =
                            new java.io.BufferedReader(
                                    new java.io.InputStreamReader(process.getInputStream())
                            );

                    String output = reader.readLine();
                    process.waitFor();

                    if (output != null) {
                        try {
                            double durationSeconds = Double.parseDouble(output);
                            existing.setTotalDuration((int) Math.round(durationSeconds));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid ffprobe output: " + output);
                        }
                    }

                } catch (IOException | InterruptedException e) {
                    System.err.println("FFmpeg not found or failed. Proceeding without duration. Error: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Video duration extraction failed: " + e.getMessage());
                }
        }

        return sessionContentRepository.save(existing);
    }

    // ================= DELETE =================
    @Override
    public void deleteSessionContent(Long sessionContentId) {

        SessionContent content = sessionContentRepository.findById(sessionContentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session content not found"));

        sessionContentRepository.delete(content);
    }
}