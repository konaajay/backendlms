package com.lms.www.management.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.dashboard.dto.StudentDashboardDTO;
import com.lms.www.management.dashboard.service.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDashboardDTO> getStudentDashboard(
            @PathVariable Long studentId) {

        StudentDashboardDTO dashboard =
                studentDashboardService.getStudentDashboard(studentId);

        return ResponseEntity.ok(dashboard);
    }
}
