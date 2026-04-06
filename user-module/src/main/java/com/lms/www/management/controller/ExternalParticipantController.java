package com.lms.www.management.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ExternalParticipant;
import com.lms.www.management.service.ExternalParticipantService;

@RestController
@RequestMapping("/api/external-participants")
@CrossOrigin
public class ExternalParticipantController {

    private final ExternalParticipantService externalParticipantService;

    public ExternalParticipantController(ExternalParticipantService externalParticipantService) {
        this.externalParticipantService = externalParticipantService;
    }

    // ===============================
    // PUBLIC ENDPOINTS
    // ===============================

    @PostMapping
    public ExternalParticipant createExternalParticipant(@RequestBody ExternalParticipant participant) {
        return externalParticipantService.createExternalParticipant(participant);
    }

    // ===============================
    // ADMIN ENDPOINTS
    // ===============================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ExternalParticipant getParticipantById(@PathVariable Long id) {
        return externalParticipantService.getParticipantById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExternalParticipant> getAllParticipants() {
        return externalParticipantService.getAllParticipants();
    }
}
