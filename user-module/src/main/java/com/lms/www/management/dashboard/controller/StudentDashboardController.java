package com.lms.www.management.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.dashboard.dto.StudentDashboardDTO;
import com.lms.www.management.dashboard.service.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT_DASHBOARD_VIEW') or hasAuthority('ROLE_STUDENT') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<StudentDashboardDTO> getStudentDashboard(
            @PathVariable Long studentId) {

        StudentDashboardDTO dashboard =
                studentDashboardService.getStudentDashboard(studentId);

        return ResponseEntity.ok(dashboard);
    }
}
