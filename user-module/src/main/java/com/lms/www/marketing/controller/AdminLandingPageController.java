package com.lms.www.marketing.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.marketing.service.LandingPageService;
import com.lms.www.marketing.model.LandingPage;
import com.lms.www.marketing.dto.LandingPageRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/marketing/admin/landing")
@CrossOrigin(origins = "*")
public class AdminLandingPageController {

    @Autowired
    private LandingPageService landingPageService;

    @PostMapping
    public ResponseEntity<LandingPage> createPage(@Valid @RequestBody LandingPageRequest request) {
        LandingPage page = new LandingPage();
        mapRequestToEntity(request, page);
        return ResponseEntity.ok(landingPageService.create(page));
    }

    @GetMapping
    public ResponseEntity<List<LandingPage>> getAllPages() {
        return ResponseEntity.ok(landingPageService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LandingPage> updatePage(@PathVariable Long id,
            @Valid @RequestBody LandingPageRequest request) {
        LandingPage page = new LandingPage();
        mapRequestToEntity(request, page);
        return ResponseEntity.ok(landingPageService.update(id, page));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        landingPageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void mapRequestToEntity(LandingPageRequest request, LandingPage page) {
        page.setTitle(request.getTitle());
        page.setSlug(request.getSlug());
        page.setHeadline(request.getHeadline());
        page.setSubtitle(request.getSubtitle());
        page.setPrice(request.getPrice());
        page.setAdBudget(request.getAdBudget());
        page.setVideoUrl(request.getVideoUrl());
        page.setFeatures(request.getFeatures());
        page.setCtaText(request.getCtaText());
    }
}
