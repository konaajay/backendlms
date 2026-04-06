package com.lms.www.management.service;

import java.util.Map;

public interface StudentVideoProgressService {

    Map<String, Object> updateWatchProgress(
            Long studentId,
            Long sessionId,
            Long sessionContentId,
            Long currentPosition
    );

    Map<String, Object> getSessionProgress(
            Long studentId,
            Long sessionId
    );
}