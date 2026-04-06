package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.ExamViolation;

public interface ExamViolationService {

    ExamViolation recordViolation(
            Long attemptId, String violationType);

    long getViolationCount(Long attemptId);
    
    List<ExamViolation> getViolationsByAttempt(Long attemptId);
}
