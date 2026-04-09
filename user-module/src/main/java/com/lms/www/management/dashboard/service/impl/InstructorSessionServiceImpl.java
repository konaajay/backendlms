package com.lms.www.management.dashboard.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.dashboard.service.InstructorSessionService;
import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.Batch;
import com.lms.www.management.model.Session;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.SessionRepository;
import com.lms.www.management.service.SessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorSessionServiceImpl implements InstructorSessionService {

    private final SessionService sessionService; // Reuse existing core engine
    private final SessionRepository sessionRepository;
    private final BatchRepository batchRepository;

    @Override
    @Transactional
    public Session createSession(Long instructorId, Long batchId, Session session) {
        validateBatchOwnership(instructorId, batchId);
        return sessionService.createSession(batchId, session);
    }

    @Override
    @Transactional
    public Session updateSession(Long instructorId, Long sessionId, Session session) {
        validateSessionOwnership(instructorId, sessionId);
        return sessionService.updateSession(sessionId, session);
    }

    @Override
    @Transactional
    public void deleteSession(Long instructorId, Long sessionId) {
        validateSessionOwnership(instructorId, sessionId);
        sessionService.deleteSession(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> getSessionsByBatchId(Long instructorId, Long batchId) {
        validateBatchOwnership(instructorId, batchId);
        return sessionService.getSessionsByBatchId(batchId);
    }

    @Override
    @Transactional(readOnly = true)
    public Session getSessionById(Long instructorId, Long sessionId) {
        validateSessionOwnership(instructorId, sessionId);
        return sessionRepository.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }

    private void validateBatchOwnership(Long instructorId, Long batchId) {
        if (instructorId == null) {
            throw new AccessDeniedException("User identity not found. Please log in again.");
        }
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        if (!java.util.Objects.equals(instructorId, batch.getTrainerId())) {
            throw new AccessDeniedException("Unauthorized to access this batch");
        }
    }

    private void validateSessionOwnership(Long instructorId, Long sessionId) {
        Session existingSession = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        validateBatchOwnership(instructorId, existingSession.getBatchId());
    }
}
