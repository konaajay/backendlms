package com.lms.www.management.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.service.AttendanceSummaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceSummaryController {

    private final AttendanceSummaryService attendanceSummaryService;

    // ================= STUDENT SUMMARY =================
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public Map<String, Object> getStudentAttendanceSummary(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam Long batchId
    ) {
        return attendanceSummaryService.getStudentEligibilitySummary(
                studentId, courseId, batchId
        );
    }

    // ================= BATCH SUMMARY =================
    @GetMapping("/batch-summary")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public Map<String, Object> getBatchSummary(
            @RequestParam Long courseId,
            @RequestParam Long batchId
    ) {
        return attendanceSummaryService
                .getBatchSummary(courseId, batchId);
    }
}