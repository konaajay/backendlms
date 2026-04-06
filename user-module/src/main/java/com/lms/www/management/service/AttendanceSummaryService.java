package com.lms.www.management.service;

import java.util.Map;

public interface AttendanceSummaryService {

    Map<String, Object> getStudentEligibilitySummary(
            Long studentId,
            Long courseId,
            Long batchId
    );
    Map<String, Object> getBatchSummary(Long courseId, Long batchId);
}
