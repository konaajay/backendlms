package com.lms.www.management.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.WebinarRegistration;
import com.lms.www.management.service.WebinarRegistrationService;

@RestController
@RequestMapping("/api/webinar-registrations")
@CrossOrigin
public class WebinarRegistrationController {

    private final WebinarRegistrationService registrationService;

    public WebinarRegistrationController(WebinarRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/student")
    @PreAuthorize("hasAuthority('WEBINAR_REGISTER')")
    public WebinarRegistration registerStudent(
            @RequestParam Long webinarId,
            @RequestParam Long userId) {
        return registrationService.registerStudent(webinarId, userId);
    }

    @PostMapping("/external")
    public WebinarRegistration registerExternal(
            @RequestParam Long webinarId,
            @RequestParam Long participantId) {
        return registrationService.registerExternalParticipant(webinarId, participantId);
    }

    @GetMapping("/webinar/{id}")
    @PreAuthorize("hasAuthority('WEBINAR_REGISTRATION_VIEW')")
    public List<WebinarRegistration> getRegistrations(@PathVariable Long id) {
        return registrationService.getRegistrationsByWebinar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('WEBINAR_REGISTRATION_CANCEL')")
    public void cancelRegistration(@PathVariable Long id) {
        registrationService.cancelRegistration(id);
    }
}