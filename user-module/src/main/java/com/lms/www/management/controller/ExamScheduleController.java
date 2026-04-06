package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExamSchedule;
import com.lms.www.management.service.ExamScheduleService;

@RestController
@RequestMapping("/api/exam-schedules")
public class ExamScheduleController {

    private final ExamScheduleService scheduleService;

    public ExamScheduleController(ExamScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // CREATE SCHEDULE
    @PostMapping
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_CREATE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamSchedule> createSchedule(@RequestBody ExamSchedule schedule) {
        return new ResponseEntity<>(
                scheduleService.createSchedule(schedule),
                HttpStatus.CREATED
        );
    }

    // GET BY ID
    @GetMapping("/{scheduleId}")
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<ExamSchedule> getSchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(
                scheduleService.getScheduleById(scheduleId)
        );
    }

    // GET BY EXAM
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<ExamSchedule>> getSchedulesByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(
                scheduleService.getSchedulesByExamId(examId)
        );
    }

    // GET BY COURSE
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<ExamSchedule>> getSchedulesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(
                scheduleService.getSchedulesByCourseId(courseId)
        );
    }

    // GET BY BATCH
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<ExamSchedule>> getSchedulesByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(
                scheduleService.getSchedulesByBatchId(batchId)
        );
    }

    // DEACTIVATE SCHEDULE (SOFT DELETE)
    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_DELETE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> deactivateSchedule(@PathVariable Long scheduleId) {
        scheduleService.deactivateSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/deactivated")
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<ExamSchedule>> getDeactivatedSchedules() {
        return ResponseEntity.ok(scheduleService.getDeactivatedSchedules());
    }
    
    @PutMapping("/{scheduleId}/restore")
    @PreAuthorize("hasAnyAuthority('EXAM_SCHEDULE_RESTORE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> restoreSchedule(@PathVariable Long scheduleId) {
        scheduleService.restoreSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}