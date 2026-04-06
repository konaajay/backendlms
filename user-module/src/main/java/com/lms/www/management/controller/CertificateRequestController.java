package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.CertificateRequest;
import com.lms.www.management.service.CertificateRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificates/requests")
@RequiredArgsConstructor
public class CertificateRequestController {

    private final CertificateRequestService certificateRequestService;

    // =========================================================
    // 🎓 STUDENT: SUBMIT REQUEST
    // =========================================================
    @PostMapping
   @PreAuthorize("hasAuthority('CERTIFICATE_REQUEST_CREATE') or hasRole('STUDENT')")
    public ResponseEntity<CertificateRequest> submitRequest(@RequestBody Map<String, String> payload) {

        Long userId = Long.valueOf(payload.get("userId"));
        String studentName = payload.get("studentName");
        String studentEmail = payload.get("studentEmail");
        TargetType targetType = TargetType.valueOf(payload.get("targetType"));
        Long targetId = Long.valueOf(payload.get("targetId"));
        String eventTitle = payload.get("eventTitle");

        Double score = null;
        if (payload.containsKey("score") && payload.get("score") != null) {
            score = Double.valueOf(payload.get("score"));
        }

        CertificateRequest request = certificateRequestService.submitRequest(
                userId,
                studentName,
                studentEmail,
                targetType,
                targetId,
                eventTitle,
                score);

        return ResponseEntity.status(201).body(request);
    }

    // =========================================================
    // 🛡️ ADMIN: GET PENDING REQUESTS
    // =========================================================
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('CERTIFICATE_REQUEST_VIEW_PENDING') or hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<CertificateRequest>> getPendingRequests() {

        List<CertificateRequest> requests = certificateRequestService.getPendingRequests();

        return ResponseEntity.ok(requests);
    }

    // =========================================================
    // 🛡️ ADMIN: APPROVE REQUEST
    // =========================================================
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('CERTIFICATE_REQUEST_APPROVE') or hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateRequest> approveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        Long adminId = Long.valueOf(payload.get("adminId"));
        String adminComment = payload.getOrDefault("adminComment", "Approved by Admin");

        CertificateRequest request = certificateRequestService.approveRequest(id, adminId, adminComment);

        return ResponseEntity.ok(request);
    }

    // =========================================================
    // 🛡️ ADMIN: REJECT REQUEST
    // =========================================================
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('CERTIFICATE_REQUEST_REJECT') or hasRole('ADMIN') or hasRole('INSTRUCTOR') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<CertificateRequest> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        Long adminId = Long.valueOf(payload.get("adminId"));
        String adminComment = payload.get("adminComment");

        CertificateRequest request = certificateRequestService.rejectRequest(id, adminId, adminComment);

        return ResponseEntity.ok(request);
    }
}