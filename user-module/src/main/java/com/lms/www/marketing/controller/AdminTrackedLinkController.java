package com.lms.www.marketing.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.marketing.model.TrackedLink;
import com.lms.www.marketing.service.TrackedLinkService;

@RestController
@RequestMapping("/api/v1/marketing/admin/tracked-links")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminTrackedLinkController {

    private final TrackedLinkService service;

    // ===== CREATE =====
    @PostMapping
    @PreAuthorize("hasAuthority('MARKETING_TRACKED_LINK_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<TrackedLink> createLink(
            @Valid @RequestBody TrackedLink link) {

        return ResponseEntity.ok(service.saveLink(link));
    }

    // ===== VIEW =====
    @GetMapping
    @PreAuthorize("hasAuthority('MARKETING_TRACKED_LINK_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<?> getAllLinks() {
        return ResponseEntity.ok(service.getAllLinksWithAnalytics());
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MARKETING_TRACKED_LINK_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        service.deleteLink(id);
        return ResponseEntity.noContent().build();
    }
}