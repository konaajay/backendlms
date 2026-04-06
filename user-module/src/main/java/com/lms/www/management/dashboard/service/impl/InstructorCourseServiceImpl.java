package com.lms.www.management.dashboard.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.lms.www.management.dashboard.service.InstructorCourseService;
import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.Batch;
import com.lms.www.management.model.Course;
import com.lms.www.management.model.CourseBatchStats;
import com.lms.www.management.model.Session;
import com.lms.www.management.model.SessionContent;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.CourseBatchStatsRepository;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.SessionContentRepository;
import com.lms.www.management.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorCourseServiceImpl implements InstructorCourseService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final CourseBatchStatsRepository statsRepository;
    private final SessionRepository sessionRepository;
    private final SessionContentRepository sessionContentRepository;

    @Override
    public List<Course> getAssignedCourses(Long instructorId) {
        // Find all unique course IDs from batches assigned to this instructor
        List<Long> courseIds = batchRepository.findAll().stream()
                .filter(b -> instructorId.equals(b.getTrainerId()))
                .map(Batch::getCourseId)
                .distinct()
                .collect(Collectors.toList());

        return courseRepository.findAllById(courseIds);
    }

    @Override
    public CourseBatchStats getCourseStats(Long instructorId, Long courseId) {
        // Validate if instructor is assigned to any batch in this course
        boolean isAssigned = batchRepository.findAll().stream()
                .anyMatch(b -> instructorId.equals(b.getTrainerId()) && courseId.equals(b.getCourseId()));

        if (!isAssigned) {
            throw new AccessDeniedException("Unauthorized to view stats for this course");
        }

        return statsRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Stats not found for course"));
    }

    @Override
    public SessionContent addSessionContent(Long instructorId, Long sessionId, SessionContent content) {
        validateSessionOwnership(instructorId, sessionId);
        content.setSessionId(sessionId);
        return sessionContentRepository.save(content);
    }

    @Override
    public List<SessionContent> getSessionContent(Long instructorId, Long sessionId) {
        validateSessionOwnership(instructorId, sessionId);
        return sessionContentRepository.findBySessionId(sessionId);
    }

    @Override
    public void deleteSessionContent(Long instructorId, Long sessionId, Long contentId) {
        validateSessionOwnership(instructorId, sessionId);
        SessionContent content = sessionContentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        if (!content.getSessionId().equals(sessionId)) {
            throw new IllegalArgumentException("Content does not belong to the specified session");
        }

        sessionContentRepository.delete(content);
    }

    private void validateSessionOwnership(Long instructorId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        Batch batch = batchRepository.findById(session.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        if (!instructorId.equals(batch.getTrainerId())) {
            throw new AccessDeniedException("Unauthorized to access this session");
        }
    }
}
