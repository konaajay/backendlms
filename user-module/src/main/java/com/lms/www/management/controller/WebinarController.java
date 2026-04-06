package com.lms.www.management.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.Webinar;
import com.lms.www.management.service.WebinarService;

@RestController
@RequestMapping("/api/webinars")
@CrossOrigin
public class WebinarController {

    private final WebinarService webinarService;

    public WebinarController(WebinarService webinarService) {
        this.webinarService = webinarService;
    }

    // ===============================
    // ADMIN ENDPOINTS
    // ===============================

    @PostMapping
    @PreAuthorize("hasAuthority('WEBINAR_CREATE')")
    public Webinar createWebinar(@RequestBody Webinar webinar) {
        return webinarService.createWebinar(webinar);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WEBINAR_UPDATE')")
    public Webinar updateWebinar(@PathVariable Long id, @RequestBody Webinar webinar) {
        return webinarService.updateWebinar(id, webinar);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WEBINAR_CANCEL')")
    public void cancelWebinar(@PathVariable Long id) {
        webinarService.cancelWebinar(id);
    }

    // ===============================
    // IMAGE UPLOAD (NEW API)
    // ===============================

    @PutMapping("/{id}/image")
    @PreAuthorize("hasAuthority('WEBINAR_UPDATE')")
    public Webinar uploadWebinarImage(
            @PathVariable Long id,
            MultipartFile file) throws IOException {

        Webinar webinar = webinarService.getWebinarById(id);

        String uploadDir = "uploads/webinars/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = "webinar_" + id + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.write(filePath, file.getBytes());

        webinar.setImageUrl("/" + uploadDir + fileName);

        return webinarService.updateWebinar(id, webinar);
    }

    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasAuthority('WEBINAR_CANCEL')")
    public void hardDeleteWebinar(@PathVariable Long id) {
        webinarService.hardDeleteWebinar(id);
    }

    // ===============================
    // PUBLIC / GENERAL ENDPOINTS
    // ===============================

    @GetMapping("/{id}")
    public Webinar getWebinar(@PathVariable Long id) {
        return webinarService.getWebinarById(id);
    }

    @GetMapping("/{id}/share-link")
    public java.util.Map<String, String> getShareLink(@PathVariable Long id) {
        // Verify webinar exists
        webinarService.getWebinarById(id);

        // Construct a generic frontend registration route
        String baseUrl = "https://your-frontend.com";
        String shareUrl = baseUrl + "/webinar-registration/" + id;

        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("shareLink", shareUrl);
        return response;
    }

    @GetMapping
    public List<Webinar> getAllWebinars() {
        return webinarService.getAllWebinars();
    }

    @GetMapping("/scheduled")
    public List<Webinar> getScheduledWebinars() {
        return webinarService.getScheduledWebinars();
    }
}