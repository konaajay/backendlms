package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.CourseBatchStats;
import com.lms.www.management.repository.CourseBatchStatsRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/course-batch-stats")
@RequiredArgsConstructor
public class CourseBatchStatsController {

    private final CourseBatchStatsRepository statsRepository;

    // ================= GET BY COURSE =================
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('COURSE_BATCH_STATS_VIEW')")
    public ResponseEntity<CourseBatchStats> getStatsByCourse(
            @PathVariable Long courseId) {

        CourseBatchStats stats = statsRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch stats not found for course"));

        return ResponseEntity.ok(stats);
    }
}