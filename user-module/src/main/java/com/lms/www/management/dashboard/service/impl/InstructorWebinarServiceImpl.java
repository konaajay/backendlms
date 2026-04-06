package com.lms.www.management.dashboard.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.dashboard.service.InstructorWebinarService;
import com.lms.www.management.enums.QuestionStatus;
import com.lms.www.management.enums.WebinarStatus;
import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.Webinar;
import com.lms.www.management.model.WebinarAttendance;
import com.lms.www.management.model.WebinarQuestion;
import com.lms.www.management.model.WebinarRecording;
import com.lms.www.management.repository.WebinarAttendanceRepository;
import com.lms.www.management.repository.WebinarQuestionRepository;
import com.lms.www.management.repository.WebinarRecordingRepository;
import com.lms.www.management.repository.WebinarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorWebinarServiceImpl implements InstructorWebinarService {

    private final WebinarRepository webinarRepository;
    private final WebinarAttendanceRepository webinarAttendanceRepository;
    private final WebinarQuestionRepository webinarQuestionRepository;
    private final WebinarRecordingRepository webinarRecordingRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Webinar> getInstructorWebinars(Long instructorId, Pageable pageable) {
        return webinarRepository.findByTrainerId(instructorId, pageable);
    }

    @Override
    @Transactional
    public Webinar createWebinar(Long instructorId, Webinar webinar) {
        webinar.setTrainerId(instructorId);
        webinar.setStatus(WebinarStatus.SCHEDULED);
        return webinarRepository.save(webinar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarAttendance> getAttendeesWithLazyLoadFix(Long instructorId, Long webinarId) {
        validateWebinarOwnership(instructorId, webinarId);
        return webinarAttendanceRepository.findByWebinarIdWithRegistration(webinarId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarQuestion> getQuestionsWithLazyLoadFix(Long instructorId, Long webinarId) {
        validateWebinarOwnership(instructorId, webinarId);
        return webinarQuestionRepository.findQuestionsWithWebinarFetched(webinarId);
    }

    @Override
    @Transactional
    public Webinar updateWebinar(Long instructorId, Long webinarId, Webinar updateRequest) {
        Webinar existing = validateWebinarOwnership(instructorId, webinarId);

        if (existing.getStatus() == WebinarStatus.CANCELLED) {
            throw new IllegalStateException("Webinar already cancelled");
        }

        existing.setTitle(updateRequest.getTitle());
        existing.setDescription(updateRequest.getDescription());
        existing.setStartTime(updateRequest.getStartTime());
        existing.setDurationMinutes(updateRequest.getDurationMinutes());
        existing.setMaxParticipants(updateRequest.getMaxParticipants());

        return webinarRepository.save(existing);
    }

    @Override
    @Transactional
    public void cancelWebinar(Long instructorId, Long webinarId) {
        Webinar existing = validateWebinarOwnership(instructorId, webinarId);

        if (existing.getStatus() == WebinarStatus.CANCELLED) {
            throw new IllegalStateException("Webinar already cancelled");
        }

        existing.setStatus(WebinarStatus.CANCELLED);
        webinarRepository.save(existing);
    }

    @Override
    @Transactional
    public WebinarQuestion answerQuestion(Long instructorId, Long webinarId, Long questionId, String answer) {

        validateWebinarOwnership(instructorId, webinarId);

        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be empty");
        }

        WebinarQuestion question = webinarQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getWebinar().getWebinarId().equals(webinarId)) {
            throw new IllegalArgumentException("Question does not belong to this webinar");
        }

        if (QuestionStatus.ANSWERED.equals(question.getStatus())) {
            throw new IllegalStateException("Question already answered");
        }

        question.setAnswer(answer);
        question.setStatus(QuestionStatus.ANSWERED);
        question.setAnsweredAt(LocalDateTime.now());

        return webinarQuestionRepository.save(question);
    }

    @Override
    @Transactional
    public WebinarRecording uploadRecording(Long instructorId, Long webinarId, MultipartFile file) {

        Webinar webinar = validateWebinarOwnership(instructorId, webinarId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // 👉 In real system: upload to S3 / local storage
        // Here we just store file name as reference
        WebinarRecording recording = new WebinarRecording();
        recording.setWebinar(webinar);
        recording.setUploadedBy(instructorId);
        // recording.setFileName(file.getOriginalFilename());
        // recording.setUploadedAt(LocalDateTime.now());

        return webinarRecordingRepository.save(recording);
    }

    private Webinar validateWebinarOwnership(Long instructorId, Long webinarId) {
        Webinar webinar = webinarRepository.findById(webinarId)
                .orElseThrow(() -> new ResourceNotFoundException("Webinar not found"));

        if (!instructorId.equals(webinar.getTrainerId())) {
            throw new AccessDeniedException("Unauthorized to access this webinar");
        }

        return webinar;
    }
}