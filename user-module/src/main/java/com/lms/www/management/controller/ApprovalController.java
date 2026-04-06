package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.Approval;
import com.lms.www.management.service.ApprovalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    // ✅ CREATE
    @PostMapping
    @PreAuthorize("hasAnyAuthority('APPROVAL_CREATE', 'ROLE_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<Approval> createApproval(
            @RequestBody Approval approval) {

        return ResponseEntity.status(201)
                .body(approvalService.createApproval(approval));
    }

    // ✅ GET PENDING
    @GetMapping
    @PreAuthorize("hasAnyAuthority('APPROVAL_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<Approval>> getPending() {

        return ResponseEntity.ok(approvalService.getPendingApprovals());
    }

    // ✅ APPROVE
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('APPROVAL_APPROVE', 'ROLE_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<Approval> approve(@PathVariable Long id) {

        return ResponseEntity.ok(approvalService.approve(id));
    }

    // ✅ REJECT
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('APPROVAL_REJECT', 'ROLE_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<Approval> reject(@PathVariable Long id) {

        return ResponseEntity.ok(approvalService.reject(id));
    }
}