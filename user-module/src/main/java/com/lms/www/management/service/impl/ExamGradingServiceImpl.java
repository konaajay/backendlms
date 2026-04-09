package com.lms.www.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamGrading;
import com.lms.www.management.repository.ExamGradingRepository;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.service.ExamGradingService;

@Service
@Transactional
public class ExamGradingServiceImpl implements ExamGradingService {

    private final ExamGradingRepository examGradingRepository;
    private final ExamRepository examRepository;

    public ExamGradingServiceImpl(
            ExamGradingRepository examGradingRepository,
            ExamRepository examRepository) {
        this.examGradingRepository = examGradingRepository;
        this.examRepository = examRepository;
    }

    @Override
    public ExamGrading saveGrading(Long examId, ExamGrading grading) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found with id: " + examId));

        // 🔒 ENTERPRISE RULE: grading rules locked after publish
        /*
        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException(
                    "Exam grading rules can be modified only when exam is in DRAFT state");
        }
        */

        grading.setExamId(examId);

        // UPSERT (idempotent & safe)
        return examGradingRepository.findByExamId(examId)
                .map(existing -> {
                    grading.setGradingId(existing.getGradingId());
                    return examGradingRepository.save(grading);
                })
                .orElseGet(() -> examGradingRepository.save(grading));
    }

    @Override
    public ExamGrading getGradingByExamId(Long examId) {
        return examGradingRepository.findByExamId(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam grading not found for examId: " + examId));
    }
}
