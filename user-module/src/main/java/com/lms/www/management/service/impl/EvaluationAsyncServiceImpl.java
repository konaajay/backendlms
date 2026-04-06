package com.lms.www.management.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.service.EvaluationAsyncService;
import com.lms.www.management.service.ExamAttemptService;

@Service
public class EvaluationAsyncServiceImpl implements EvaluationAsyncService {

    private final ExamAttemptService examAttemptService;

    public EvaluationAsyncServiceImpl(ExamAttemptService examAttemptService) {
        this.examAttemptService = examAttemptService;
    }

    @Override
    @Async("evaluationExecutor")
    public void evaluateAttemptAsync(Long attemptId) {

        try {
            // 🔥 Set status to EVALUATING before starting
            ExamAttempt attempt = examAttemptService.getAttemptByIdForSystem(attemptId);
            attempt.setStatus("EVALUATING");
            examAttemptService.updateAttemptStatus(attempt);

            // Run evaluation
            examAttemptService.evaluateAttempt(attemptId);

        } catch (Exception e) {

            // 🔥 If evaluation fails → mark FAILED
            ExamAttempt attempt = examAttemptService.getAttemptByIdForSystem(attemptId);
            attempt.setStatus("FAILED");
            examAttemptService.updateAttemptStatus(attempt);

        }
    }
}