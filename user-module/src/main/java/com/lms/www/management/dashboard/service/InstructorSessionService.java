package com.lms.www.management.dashboard.service;

import java.util.List;

import com.lms.www.management.model.Session;

public interface InstructorSessionService {
    Session createSession(Long instructorId, Long batchId, Session session);

    Session updateSession(Long instructorId, Long sessionId, Session session);

    void deleteSession(Long instructorId, Long sessionId);

    List<Session> getSessionsByBatchId(Long instructorId, Long batchId);
}
