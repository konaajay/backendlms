package com.lms.www.management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.model.AttendanceSession;
import com.lms.www.management.service.AttendanceSessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/session")
@RequiredArgsConstructor
public class AttendanceSessionController {

    private final AttendanceSessionService attendanceSessionService;

    // ===============================
    // START ATTENDANCE
    // ===============================
    @PostMapping("/start")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_CREATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AttendanceSession> startAttendance(
            @RequestParam Long sessionId,
            @RequestParam Long courseId,
            @RequestParam Long batchId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
                attendanceSessionService.startAttendance(
                        sessionId,
                        courseId,
                        batchId,
                        userId
                )
        );
    }

    // ===============================
    // END ATTENDANCE
    // ===============================
    @PutMapping("/{attendanceSessionId}/end")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_UPDATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AttendanceSession> endAttendance(
            @PathVariable Long attendanceSessionId
    ) {
        return ResponseEntity.ok(
                attendanceSessionService.endAttendance(attendanceSessionId)
        );
    }

    // ===============================
    // GET BY ID
    // ===============================
    @GetMapping("/{attendanceSessionId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AttendanceSession> getById(
            @PathVariable Long attendanceSessionId
    ) {
        return ResponseEntity.ok(
                attendanceSessionService.getById(attendanceSessionId)
        );
    }

    // ===============================
    // GET ACTIVE ONLY
    // ===============================
    @GetMapping("/active/{sessionId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<AttendanceSession> getActiveBySession(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(
                attendanceSessionService.getActiveBySessionId(sessionId)
        );
    }

    // ===============================
    // GET ACTIVE + ENDED
    // ===============================
    @GetMapping("/session/{sessionId}/all")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AttendanceSession>> getActiveAndEndedBySession(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(
                attendanceSessionService.getActiveAndEndedBySessionId(sessionId)
        );
    }

    // ===============================
    // GET BY DATE
    // ===============================
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AttendanceSession>> getByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(
                attendanceSessionService.getByDate(date)
        );
    }

    // ===============================
    // DELETE
    // ===============================
    // ===============================
    // GET BY BATCH
    // ===============================
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<AttendanceSession>> getByBatch(
            @PathVariable Long batchId
    ) {
        return ResponseEntity.ok(
                attendanceSessionService.getByBatch(batchId)
        );
    }

    @DeleteMapping("/{attendanceSessionId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_SESSION_DELETE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteAttendanceSession(
            @PathVariable Long attendanceSessionId
    ) {
        attendanceSessionService.delete(attendanceSessionId);
        return ResponseEntity.noContent().build();
    }
}