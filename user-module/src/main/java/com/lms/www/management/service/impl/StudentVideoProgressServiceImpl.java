package com.lms.www.management.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.ProgressStatus;
import com.lms.www.management.model.SessionContent;
import com.lms.www.management.model.StudentVideoProgress;
import com.lms.www.management.repository.SessionContentRepository;
import com.lms.www.management.repository.StudentVideoProgressRepository;
import com.lms.www.management.service.StudentVideoProgressService;

@Service
public class StudentVideoProgressServiceImpl implements StudentVideoProgressService {

    private final StudentVideoProgressRepository progressRepo;
    private final SessionContentRepository contentRepo;

    private static final double COMPLETION_THRESHOLD = 0.90; // 90%
    private static final int MAX_ALLOWED_SPEED_MULTIPLIER = 3;

    public StudentVideoProgressServiceImpl(
            StudentVideoProgressRepository progressRepo,
            SessionContentRepository contentRepo) {
        this.progressRepo = progressRepo;
        this.contentRepo = contentRepo;
    }

    @Override
    @Transactional
    public Map<String, Object> updateWatchProgress(
            Long studentId,
            Long sessionId,
            Long sessionContentId,
            Long currentPosition) {

        SessionContent content = contentRepo.findById(sessionContentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        if (!"VIDEO".equalsIgnoreCase(content.getContentType())) {
            throw new RuntimeException("Tracking allowed only for VIDEO type");
        }

        Long totalDuration = Optional.ofNullable(content.getTotalDuration())
                .map(Integer::longValue)
                .orElse(0L);

        if (totalDuration <= 0) {
            throw new RuntimeException("Video duration not configured");
        }

        StudentVideoProgress progress = progressRepo
                .findByStudentIdAndSessionContentId(studentId, sessionContentId)
                .orElseGet(() -> {
                    StudentVideoProgress p = new StudentVideoProgress();
                    p.setStudentId(studentId);
                    p.setSessionId(sessionId);
                    p.setSessionContentId(sessionContentId);
                    p.setTotalDurationSnapshot(totalDuration);
                    p.setWatchedDuration(0L);
                    p.setLastWatchedPosition(0L);
                    p.setStatus(ProgressStatus.NOT_STARTED);
                    return progressRepo.save(p);
                });

        if (progress.getStatus() == ProgressStatus.COMPLETED) {
            return buildUpdateResponse(progress, totalDuration, currentPosition);
        }

        LocalDateTime now = LocalDateTime.now();

        if (progress.getLastUpdatedAt() == null) {
            progress.setLastWatchedPosition(currentPosition);
            progress.setLastUpdatedAt(now);
            progress.setStatus(ProgressStatus.IN_PROGRESS);

            double calculatedPercentage = ((double) progress.getWatchedDuration() / totalDuration) * 100;
            progress.setPercentageWatched(Math.min(calculatedPercentage, 100.0));

            progressRepo.save(progress);
            return buildUpdateResponse(progress, totalDuration, currentPosition);
        }

        long secondsElapsedRealTime = Duration.between(progress.getLastUpdatedAt(), now).getSeconds();

        long positionDiff = currentPosition - progress.getLastWatchedPosition();

        if (positionDiff > 0) {

            long maxExpectedDiff = (secondsElapsedRealTime + 5) * MAX_ALLOWED_SPEED_MULTIPLIER;

            if (positionDiff <= maxExpectedDiff) {

                long newWatched = progress.getWatchedDuration() + positionDiff;

                if (newWatched > totalDuration) {
                    newWatched = totalDuration;
                }

                progress.setWatchedDuration(newWatched);
            }
        }

        progress.setLastWatchedPosition(currentPosition);
        progress.setLastUpdatedAt(now);

        if (progress.getWatchedDuration() >= (long) (totalDuration * COMPLETION_THRESHOLD)) {

            progress.setStatus(ProgressStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());
        } else {
            progress.setStatus(ProgressStatus.IN_PROGRESS);
        }

        double calculatedPercentage = ((double) progress.getWatchedDuration() / totalDuration) * 100;
        progress.setPercentageWatched(Math.min(calculatedPercentage, 100.0));

        progress = progressRepo.save(progress);

        return buildUpdateResponse(progress, totalDuration, currentPosition);
    }

    private Map<String, Object> buildUpdateResponse(
            StudentVideoProgress progress,
            Long totalDuration,
            Long currentPosition) {

        Map<String, Object> response = new HashMap<>();

        response.put("watchedDuration", progress.getWatchedDuration());
        response.put("totalDuration", totalDuration);
        response.put("completionStatus", progress.getStatus());
        response.put("currentPosition", currentPosition);

        double percentage = ((double) progress.getWatchedDuration() / totalDuration) * 100;

        response.put("videoPercentage", Math.min(percentage, 100.0));

        return response;
    }

    @Override
    public Map<String, Object> getSessionProgress(
            Long studentId,
            Long sessionId) {

        List<SessionContent> contents = contentRepo.findBySessionIdAndStatus(sessionId, "ACTIVE")
                .stream()
                .filter(c -> "VIDEO".equalsIgnoreCase(c.getContentType()))
                .toList();

        if (contents.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("message", "No video content in this session");
            return empty;
        }

        List<StudentVideoProgress> progressList = progressRepo.findByStudentIdAndSessionId(studentId, sessionId);

        Map<Long, StudentVideoProgress> progressMap = new HashMap<>();
        for (StudentVideoProgress p : progressList) {
            progressMap.put(p.getSessionContentId(), p);
        }

        long totalSessionDuration = 0L;
        long totalWatchedDuration = 0L;

        List<Map<String, Object>> videoDetails = new ArrayList<>();

        for (SessionContent content : contents) {

            long duration = Optional.ofNullable(content.getTotalDuration())
                    .map(Integer::longValue)
                    .orElse(0L);

            totalSessionDuration += duration;

            StudentVideoProgress progress = progressMap.get(content.getSessionContentId());

            long watched = progress != null ? progress.getWatchedDuration() : 0L;

            ProgressStatus status = progress != null ? progress.getStatus() : ProgressStatus.NOT_STARTED;

            totalWatchedDuration += Math.min(watched, duration);

            Map<String, Object> detail = new HashMap<>();
            detail.put("sessionContentId", content.getSessionContentId());
            detail.put("title", content.getTitle());
            detail.put("status", status);
            detail.put("watchedDuration", watched);
            detail.put("totalDuration", duration);
            detail.put("lastWatchedPosition", progress != null ? progress.getLastWatchedPosition() : 0L);

            videoDetails.add(detail);
        }

        double overallPercentage = 0.0;
        if (totalSessionDuration > 0) {
            overallPercentage = ((double) totalWatchedDuration / totalSessionDuration) * 100;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("studentId", studentId);
        result.put("totalSessionDuration", totalSessionDuration);
        result.put("totalWatchedDuration", totalWatchedDuration);
        result.put("overallProgressPercentage",
                Math.min(overallPercentage, 100.0));
        result.put("videos", videoDetails);

        return result;
    }
}