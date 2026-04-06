package com.lms.www.management.dashboard.service;

import java.util.List;

import com.lms.www.management.model.Course;
import com.lms.www.management.model.CourseBatchStats;
import com.lms.www.management.model.SessionContent;

public interface InstructorCourseService {
    List<Course> getAssignedCourses(Long instructorId);

    CourseBatchStats getCourseStats(Long instructorId, Long courseId);

    SessionContent addSessionContent(Long instructorId, Long sessionId, SessionContent content);

    List<SessionContent> getSessionContent(Long instructorId, Long sessionId);

    void deleteSessionContent(Long instructorId, Long sessionId, Long contentId);
}
