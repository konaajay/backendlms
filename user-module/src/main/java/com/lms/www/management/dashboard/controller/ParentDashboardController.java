package com.lms.www.management.dashboard.controller;


import java.util.List;

import com.lms.www.management.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.dashboard.dto.ChildSummaryDTO;
import com.lms.www.management.dashboard.dto.ParentDashboardDTO;
import com.lms.www.management.dashboard.dto.StudentDashboardDTO;
import com.lms.www.management.dashboard.service.ParentDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
public class ParentDashboardController {

    private final ParentDashboardService parentDashboardService;
    private final SecurityUtil securityUtil;

    @GetMapping("/students")
    @PreAuthorize("hasRole('PARENT') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<ChildSummaryDTO>> getChildrenForParent() {
        Long parentId = securityUtil.getUserId();
        return ResponseEntity.ok(parentDashboardService.getChildrenForParent(parentId));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PARENT') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<ParentDashboardDTO> getAggregateDashboard() {
        Long parentId = securityUtil.getUserId();
        return ResponseEntity.ok(parentDashboardService.getParentDashboard(parentId));
    }

    @GetMapping("/dashboard/{studentId}")
    @PreAuthorize("hasRole('PARENT') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<StudentDashboardDTO> getSingleChildDashboard(@PathVariable Long studentId) {
        Long parentId = securityUtil.getUserId();
        return ResponseEntity.ok(parentDashboardService.getSingleChildDashboard(parentId, studentId));
    }
}