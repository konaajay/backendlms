package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.model.StudentBatchTransfer;
import com.lms.www.management.service.StudentBatchTransferService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student-batch-transfers")
@RequiredArgsConstructor
public class StudentBatchTransferController {

    private final StudentBatchTransferService transferService;

    // ================= TRANSFER =================
    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('STUDENT_BATCH_TRANSFER_CREATE')")
    public ResponseEntity<StudentBatchTransfer> transferStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam Long toBatchId,
            @RequestParam String reason) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String transferredBy = auth.getName();

        StudentBatchTransfer transfer =
                transferService.transferStudent(
                        studentId,
                        courseId,
                        toBatchId,
                        reason,
                        transferredBy
                );

        return new ResponseEntity<>(transfer, HttpStatus.CREATED);
    }

    // ================= VIEW BY STUDENT =================
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT_BATCH_TRANSFER_VIEW')")
    public ResponseEntity<List<StudentBatchTransfer>> getTransfersByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                transferService.getTransfersByStudent(studentId)
        );
    }

    // ================= VIEW BY COURSE =================
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('STUDENT_BATCH_TRANSFER_VIEW')")
    public ResponseEntity<List<StudentBatchTransfer>> getTransfersByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                transferService.getTransfersByCourse(courseId)
        );
    }
}