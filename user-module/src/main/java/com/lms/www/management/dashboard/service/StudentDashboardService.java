package com.lms.www.management.dashboard.service;

import com.lms.www.management.dashboard.dto.StudentDashboardDTO;

public interface StudentDashboardService {

    StudentDashboardDTO getStudentDashboard(Long studentId);

}