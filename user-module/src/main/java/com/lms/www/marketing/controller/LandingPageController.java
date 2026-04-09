package com.lms.www.marketing.controller;

import java.util.List;
import java.util.Map;

import com.lms.www.marketing.model.LandingPage;
import com.lms.www.marketing.service.LandingPageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/marketing/public/landing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LandingPageController {

    private final LandingPageService landingPageService;

    // =========================
    // PUBLIC
    // =========================
    @GetMapping("/{slug}")
    public ResponseEntity<LandingPage> getPageBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(landingPageService.getBySlug(slug));
    }

    // =========================
    // CREATE
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_LANDING_CREATE')")
    @PostMapping
    public ResponseEntity<?> createPage(@Valid @RequestBody LandingPage page) {
        return ResponseEntity.ok(landingPageService.create(page));
    }

    // =========================
    // SEED (STRICT)
    // =========================
    @PreAuthorize("hasAuthority('SYSTEM_INTERNAL')")
    @PostMapping("/seed")
    public ResponseEntity<?> seedData() {
        landingPageService.seedDefaults();
        return ResponseEntity.ok(Map.of("message", "Seeded successfully"));
    }

    // =========================
    // READ
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_LANDING_VIEW')")
    @GetMapping
    public ResponseEntity<List<LandingPage>> getAllPages() {
        return ResponseEntity.ok(landingPageService.getAll());
    }

    // =========================
    // UPDATE
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_LANDING_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePage(@PathVariable Long id,
            @Valid @RequestBody LandingPage page) {
        return ResponseEntity.ok(landingPageService.update(id, page));
    }

    // =========================
    // DELETE
    // =========================
    @PreAuthorize("hasAuthority('MARKETING_LANDING_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePage(@PathVariable Long id) {
        landingPageService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}