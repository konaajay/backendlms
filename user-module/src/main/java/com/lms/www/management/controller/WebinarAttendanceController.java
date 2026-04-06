package com.lms.www.management.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.enums.AttendanceMode;
import com.lms.www.management.enums.AttendanceStatus;
import com.lms.www.management.model.WebinarAttendance;
import com.lms.www.management.service.WebinarAttendanceService;

@RestController
@RequestMapping("/api/webinar-attendance")
@CrossOrigin
public class WebinarAttendanceController {

    private final WebinarAttendanceService attendanceService;

    public WebinarAttendanceController(WebinarAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/mark")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public WebinarAttendance markAttendance(
            @RequestParam Long registrationId,
            @RequestParam AttendanceStatus status,
            @RequestParam AttendanceMode mode) {

        return attendanceService.markAttendance(registrationId, status, mode);
    }

    @GetMapping("/webinar/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRAINER')")
    public List<WebinarAttendance> getAttendanceByWebinar(@PathVariable Long id) {
        return attendanceService.getAttendanceByWebinar(id);
    }

    @GetMapping("/registration/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('TRAINER') or hasRole('STUDENT')")
    public List<WebinarAttendance> getAttendanceByRegistration(@PathVariable Long id) {
        return attendanceService.getAttendanceByRegistration(id);
    }
}