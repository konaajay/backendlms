package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.CodingExecutionResult;

public interface CodingExecutionService {

    void runSubmission(Long responseId);

    List<CodingExecutionResult> getResultsByResponse(Long responseId);
}