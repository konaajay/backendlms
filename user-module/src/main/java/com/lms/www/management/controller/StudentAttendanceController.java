package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.fee.dto.ApiResponse;
import com.lms.www.management.model.AttendanceRecord;
import com.lms.www.management.service.AttendanceRecordService;
import com.lms.www.management.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

/**
 * Controller for student-specific attendance actions.
 * All actions are performed for the currently authenticated student.
 */
@RestController
@RequestMapping("/api/student/attendance")
@RequiredArgsConstructor
public class StudentAttendanceController {

    private final AttendanceRecordService attendanceRecordService;
    private final SecurityUtil securityUtil;

    /**
     * Get the student's own attendance history (List of all records).
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('STUDENT') or hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getMyAttendanceHistory() {
        Long userId = securityUtil.getUserId();
        List<AttendanceRecord> records = attendanceRecordService.getByStudent(userId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    /**
     * Get the student's own attendance summary/percentage.
     * Note: Future enhancement could include per-course percentages.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('STUDENT') or hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getMyAttendanceSummary() {
        Long userId = securityUtil.getUserId();
        // Returning history as summary for now until specialized DTO is finalized
        return ResponseEntity.ok(ApiResponse.success(attendanceRecordService.getByStudent(userId)));
    }
}
