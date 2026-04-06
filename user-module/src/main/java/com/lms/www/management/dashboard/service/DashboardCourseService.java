package com.lms.www.management.dashboard.service;

import java.util.List;

import com.lms.www.management.dashboard.dto.DashboardCourseDataDTO;

public interface DashboardCourseService {
    List<DashboardCourseDataDTO> getCoursesForStudent(Long studentId);
}