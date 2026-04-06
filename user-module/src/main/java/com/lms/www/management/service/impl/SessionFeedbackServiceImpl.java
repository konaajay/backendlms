package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.Batch;
import com.lms.www.management.model.Session;
import com.lms.www.management.model.SessionFeedback;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.SessionFeedbackRepository;
import com.lms.www.management.repository.SessionRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.SessionFeedbackService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionFeedbackServiceImpl implements SessionFeedbackService {

    private final SessionFeedbackRepository sessionFeedbackRepository;
    private final SessionRepository sessionRepository;
    private final BatchRepository batchRepository;
    private final StudentBatchRepository studentBatchRepository;

    @Override
    @Transactional
    public SessionFeedback submitFeedback(Long sessionId, Long fromUserId, Long toUserId, Integer rating,
            String comment) {

        // 1. Self-Feedback Prevention
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Self feedback is not allowed.");
        }

        // 2. Rating Validation
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        // 3. Time Restriction (within 48 hours of session start time)
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        LocalDateTime sessionStartTime = LocalDateTime.of(session.getStartDate(), session.getStartTime());
        if (LocalDateTime.now().isAfter(sessionStartTime.plusHours(48))) {
            throw new IllegalArgumentException("Feedback can only be submitted within 48 hours of the session.");
        }
        if (LocalDateTime.now().isBefore(sessionStartTime)) {
            throw new IllegalArgumentException("Feedback cannot be submitted before the session starts.");
        }

        Batch batch = batchRepository.findById(session.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found for this session"));

        // 4. Session Validation & Explicit Role Checks
        boolean isFromTrainer = fromUserId.equals(batch.getTrainerId());
        boolean isToTrainer = toUserId.equals(batch.getTrainerId());

        boolean isFromStudent = studentBatchRepository.findByUserId(fromUserId)
                .stream().anyMatch(sb -> sb.getBatchId().equals(batch.getBatchId()) && "ACTIVE".equals(sb.getStatus()));

        boolean isToStudent = studentBatchRepository.findByUserId(toUserId)
                .stream().anyMatch(sb -> sb.getBatchId().equals(batch.getBatchId()) && "ACTIVE".equals(sb.getStatus()));

        if (isFromTrainer && isToStudent) {
            // Valid direction: Trainer -> Student
        } else if (isFromStudent && isToTrainer) {
            // Valid direction: Student -> Trainer
        } else {
            throw new IllegalArgumentException("Invalid feedback direction or users are not enrolled in this session.");
        }

        // 5. Update Logic (if exists -> update, else -> create)
        Optional<SessionFeedback> existingFeedbackOpt = sessionFeedbackRepository
                .findBySessionIdAndFromUserIdAndToUserId(sessionId, fromUserId, toUserId);

        SessionFeedback feedback;
        if (existingFeedbackOpt.isPresent()) {
            feedback = existingFeedbackOpt.get();
            feedback.setRating(rating);
            feedback.setComment(comment);
            // updatedAt is handled by @PreUpdate
        } else {
            feedback = new SessionFeedback();
            feedback.setSessionId(sessionId);
            feedback.setFromUserId(fromUserId);
            feedback.setToUserId(toUserId);
            feedback.setRating(rating);
            feedback.setComment(comment);
        }

        return sessionFeedbackRepository.save(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForUser(Long toUserId) {
        return sessionFeedbackRepository.getAverageRatingForUser(toUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTrainerAverageFromStudents(Long trainerId) {
        // Technically all feedbacks to trainer are only from students because of
        // validation logic,
        // so we can just return the general average.
        return sessionFeedbackRepository.getAverageRatingForUser(trainerId);
    }
}