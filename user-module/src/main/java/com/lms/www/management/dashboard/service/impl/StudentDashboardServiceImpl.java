package com.lms.www.management.dashboard.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.dashboard.dto.AttendanceSummaryDTO;
import com.lms.www.management.dashboard.dto.BatchSummaryDTO;
import com.lms.www.management.dashboard.dto.CertificateSummaryDTO;
import com.lms.www.management.dashboard.dto.DashboardCourseDataDTO;
import com.lms.www.management.dashboard.dto.ExamSummaryDTO;
import com.lms.www.management.dashboard.dto.ProgressSummaryDTO;
import com.lms.www.management.dashboard.dto.StudentDashboardDTO;
import com.lms.www.management.dashboard.dto.StudentInfoDTO;
import com.lms.www.management.dashboard.dto.WebinarSummaryDTO;
import com.lms.www.management.dashboard.service.DashboardCourseService;
import com.lms.www.management.dashboard.service.DashboardMetricsService;
import com.lms.www.management.dashboard.service.StudentDashboardService;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.repository.StudentBatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService {

        private final DashboardCourseService dashboardCourseService;
        private final DashboardMetricsService dashboardMetricsService;
        private final StudentBatchRepository studentBatchRepository;

        @Override
        public StudentDashboardDTO getStudentDashboard(Long studentId) {

                // Courses & progress
                List<DashboardCourseDataDTO> courses = dashboardCourseService.getCoursesForStudent(studentId);

                // Batches
                List<BatchSummaryDTO> batches = dashboardMetricsService.getBatchesForStudent(studentId);

                // Attendance
                AttendanceSummaryDTO attendance = dashboardMetricsService.getAttendanceForStudent(studentId);

                // Exams
                List<ExamSummaryDTO> exams = dashboardMetricsService.getExamsForStudent(studentId);

                // Certificates
                List<CertificateSummaryDTO> certificates = dashboardMetricsService.getCertificatesForStudent(studentId);

                // Webinars
                List<WebinarSummaryDTO> webinars = dashboardMetricsService.getWebinarsForStudent(studentId);

                // Student Info (basic placeholder – can be replaced with UserService later)
                StudentInfoDTO studentInfo = studentBatchRepository
                                .findByStudentId(studentId)
                                .stream()
                                .findFirst()
                                .map(sb -> StudentInfoDTO.builder()
                                                .studentId(sb.getStudentId())
                                                .studentName(sb.getStudentName())
                                                .email(sb.getStudentEmail())
                                                .status(sb.getStatus())
                                                .build())
                                .orElseGet(() -> StudentInfoDTO.builder()
                                                .studentId(studentId)
                                                .studentName("Student " + studentId)
                                                .email("student" + studentId + "@example.com")
                                                .status("ACTIVE")
                                                .build());

                // Progress summary calculation
                int totalCourses = courses.size();
                long completedCourses = courses.stream().filter(DashboardCourseDataDTO::isCompleted).count();
                long activeCourses = courses.stream().filter(DashboardCourseDataDTO::isActive).count();

                ProgressSummaryDTO progressSummary = ProgressSummaryDTO.builder()
                                .totalCourses(totalCourses)
                                .completedCourses((int) completedCourses)
                                .activeCourses((int) activeCourses)
                                .build();

                // Final dashboard response
                return StudentDashboardDTO.builder()
                                .studentInfo(studentInfo)
                                .progressSummary(progressSummary)
                                .courses(courses.stream().map(DashboardCourseDataDTO::getCourseProgress).toList())
                                .batches(batches)
                                .attendanceSummary(attendance)
                                .exams(exams)
                                .certificates(certificates)
                                .webinars(webinars)
                                .build();
        }
}