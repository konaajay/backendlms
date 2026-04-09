package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.model.Session;
import com.lms.www.management.service.SessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    // ================= CREATE =================
    @PostMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('SESSION_CREATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Session> createSession(
            @PathVariable Long batchId,
            @RequestBody Session session) {

        Session createdSession =
                sessionService.createSession(batchId, session);

        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    // ================= GET BY ID =================
    @GetMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('SESSION_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Session> getSessionById(
            @PathVariable Long sessionId) {

        return ResponseEntity.ok(
                sessionService.getSessionById(sessionId)
        );
    }

    // ================= GET BY BATCH =================
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('SESSION_VIEW') or hasAnyRole('INSTRUCTOR', 'STUDENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<Session>> getSessionsByBatchId(
            @PathVariable Long batchId) {

        return ResponseEntity.ok(
                sessionService.getSessionsByBatchId(batchId)
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('SESSION_UPDATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Session> updateSession(
            @PathVariable Long sessionId,
            @RequestBody Session session) {

        return ResponseEntity.ok(
                sessionService.updateSession(sessionId, session)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('SESSION_DELETE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId) {

        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}