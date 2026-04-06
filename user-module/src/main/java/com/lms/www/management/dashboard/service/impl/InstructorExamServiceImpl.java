package com.lms.www.management.dashboard.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.dashboard.service.InstructorExamService;
import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.instructor.dto.InstructorEvaluationRequestDTO;
import com.lms.www.management.instructor.dto.PendingEvaluationProjection;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.ExamEvaluationLog;
import com.lms.www.management.model.ExamQuestion;
import com.lms.www.management.model.ExamResponse;
import com.lms.www.management.repository.ExamAttemptRepository;
import com.lms.www.management.repository.ExamEvaluationLogRepository;
import com.lms.www.management.repository.ExamQuestionRepository;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.repository.ExamResponseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorExamServiceImpl implements InstructorExamService {

    private final ExamRepository examRepository;
    private final ExamResponseRepository examResponseRepository;
    private final ExamEvaluationLogRepository examEvaluationLogRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;

    @Override
    @Transactional
    public Exam createExam(Long instructorId, Exam exam) {
        exam.setCreatedBy(instructorId);
        exam.setStatus("DRAFT");
        return examRepository.save(exam);
    }

    @Override
    @Transactional
    public Exam updateExam(Long instructorId, Long examId, Exam exam) {
        Exam existing = validateExamOwnership(instructorId, examId);
        validateNotClosed(existing);

        existing.setTitle(exam.getTitle());
        existing.setTotalMarks(exam.getTotalMarks());
        existing.setPassPercentage(exam.getPassPercentage());
        existing.setDurationMinutes(exam.getDurationMinutes());
        existing.setCertificateEnabled(exam.getCertificateEnabled());

        return examRepository.save(existing);
    }

    @Override
    @Transactional
    public void publishExam(Long instructorId, Long examId) {
        Exam existing = validateExamOwnership(instructorId, examId);
        validateNotClosed(existing);

        if (!"PUBLISHED".equals(existing.getStatus())) {
            existing.setStatus("PUBLISHED");
            examRepository.save(existing);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Exam> getInstructorExams(Long instructorId, Pageable pageable) {
        return examRepository.findByCreatedByAndIsDeletedFalse(instructorId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingEvaluationProjection> getPendingEvaluations(Long instructorId) {
        return examResponseRepository.findPendingEvaluationsByInstructorId(instructorId);
    }

    @Override
    @Transactional
    public void evaluateResponse(Long instructorId, Long responseId, InstructorEvaluationRequestDTO request) {

        boolean isOwner = examResponseRepository.checkInstructorOwnership(responseId, instructorId) > 0;
        if (!isOwner) {
            throw new AccessDeniedException("Unauthorized to evaluate this response");
        }

        ExamResponse res = examResponseRepository.findById(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamResponse not found"));

        // ✅ Prevent double evaluation
        if (res.getMarksAwarded() != null) {
            throw new IllegalStateException("Already evaluated");
        }

        // ✅ Prevent evaluation if exam is closed
        Long examId = examAttemptRepository
                .findById(res.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"))
                .getExamId();

        validateExamOwnership(instructorId, examId);

        Double oldScore = res.getMarksAwarded() != null ? res.getMarksAwarded() : 0.0;

        res.setMarksAwarded(request.getMarksAwarded());
        examResponseRepository.save(res);

        ExamEvaluationLog log = new ExamEvaluationLog();
        log.setAttemptId(res.getAttemptId());
        log.setEvaluatorId(instructorId);
        log.setOldScore(oldScore);
        log.setNewScore(request.getMarksAwarded());
        log.setReason(request.getFeedback());
        log.setUpdatedAt(LocalDateTime.now());

        examEvaluationLogRepository.save(log);
    }

    @Override
    @Transactional
    public void closeExam(Long instructorId, Long examId) {
        Exam existing = validateExamOwnership(instructorId, examId);
        existing.setStatus("CLOSED");
        examRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteExam(Long instructorId, Long examId) {
        Exam existing = validateExamOwnership(instructorId, examId);
        existing.setIsDeleted(true);
        examRepository.save(existing);
    }

    @Override
    @Transactional
    public ExamQuestion addQuestionToSection(Long instructorId, Long examId, ExamQuestion question) {
        Exam exam = validateExamOwnership(instructorId, examId);
        validateNotClosed(exam);

        // ✅ Validate question belongs to exam
        Long questionExamId = examQuestionRepository.findExamIdByQuestionId(question.getQuestionId());

        if (!questionExamId.equals(examId)) {
            throw new IllegalArgumentException("Question does not belong to this exam");
        }

        if (examQuestionRepository.existsByExamSectionIdAndQuestionId(
                question.getExamSectionId(), question.getQuestionId())) {
            throw new IllegalArgumentException("Question already exists in this section");
        }

        return examQuestionRepository.save(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamAttempt> getExamAttempts(Long instructorId, Long examId) {
        validateExamOwnership(instructorId, examId);
        return examAttemptRepository.findByExamId(examId);
    }

    private Exam validateExamOwnership(Long instructorId, Long examId) {
        return examRepository.findByExamIdAndCreatedByAndIsDeletedFalse(examId, instructorId)
                .orElseThrow(() -> new AccessDeniedException("Exam not found or unauthorized"));
    }

    private void validateNotClosed(Exam exam) {
        if ("CLOSED".equals(exam.getStatus())) {
            throw new IllegalStateException("Exam is closed");
        }
    }
}