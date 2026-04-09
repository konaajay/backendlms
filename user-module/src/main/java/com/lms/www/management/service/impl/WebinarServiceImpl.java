package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.WebinarMode;
import com.lms.www.management.enums.WebinarStatus;
import com.lms.www.management.model.Webinar;
import com.lms.www.management.repository.WebinarRepository;
import com.lms.www.management.service.WebinarService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebinarServiceImpl implements WebinarService {

    private final WebinarRepository webinarRepository;
//    private final WebinarRegistrationRepository registrationRepository;

    @Override
    @Transactional
    public Webinar createWebinar(Webinar webinar) {
        validateWebinarRules(webinar);
        webinar.setStatus(WebinarStatus.SCHEDULED);
        webinar.setRegisteredCount(0);
        return webinarRepository.save(webinar);
    }

    @Override
    @Transactional
    public Webinar updateWebinar(Long webinarId, Webinar updatedWebinar) {

        Webinar existing = getWebinarById(webinarId);

        if (updatedWebinar.getTitle() != null)
            existing.setTitle(updatedWebinar.getTitle());

        if (updatedWebinar.getDescription() != null)
            existing.setDescription(updatedWebinar.getDescription());

        if (updatedWebinar.getTrainerId() != null)
            existing.setTrainerId(updatedWebinar.getTrainerId());

        if (updatedWebinar.getStartTime() != null)
            existing.setStartTime(updatedWebinar.getStartTime());

        if (updatedWebinar.getDurationMinutes() != null)
            existing.setDurationMinutes(updatedWebinar.getDurationMinutes());

        if (updatedWebinar.getTimezone() != null)
            existing.setTimezone(updatedWebinar.getTimezone());

        if (updatedWebinar.getMaxParticipants() != null)
            existing.setMaxParticipants(updatedWebinar.getMaxParticipants());

        if (updatedWebinar.getMode() != null)
            existing.setMode(updatedWebinar.getMode());

        if (updatedWebinar.getType() != null)
            existing.setType(updatedWebinar.getType());

        if (updatedWebinar.getPrice() != null)
            existing.setPrice(updatedWebinar.getPrice());

        // ✅ ADDED FOR IMAGE SUPPORT
        if (updatedWebinar.getImageUrl() != null)
            existing.setImageUrl(updatedWebinar.getImageUrl());

        if (updatedWebinar.getMeetingLink() != null)
            existing.setMeetingLink(updatedWebinar.getMeetingLink());

        if (updatedWebinar.getVenueName() != null)
            existing.setVenueName(updatedWebinar.getVenueName());

        if (updatedWebinar.getVenueAddress() != null)
            existing.setVenueAddress(updatedWebinar.getVenueAddress());

        if (updatedWebinar.getVenueCity() != null)
            existing.setVenueCity(updatedWebinar.getVenueCity());

        if (updatedWebinar.getVenueCountry() != null)
            existing.setVenueCountry(updatedWebinar.getVenueCountry());

        if (updatedWebinar.getMapLink() != null)
            existing.setMapLink(updatedWebinar.getMapLink());

        if (updatedWebinar.getAllowExternal() != null)
            existing.setAllowExternal(updatedWebinar.getAllowExternal());

        if (updatedWebinar.getStatus() != null)
            existing.setStatus(updatedWebinar.getStatus());

        validateWebinarRules(existing);

        return webinarRepository.save(existing);
    }

    private void validateWebinarRules(Webinar webinar) {

        // Rule 1 - Capacity Validation
        if (webinar.getMaxParticipants() == null || webinar.getMaxParticipants() <= 0) {
            throw new IllegalArgumentException("maxParticipants must be greater than 0.");
        }

        // Rule 2 - Scheduling Validation (Allowing a 5-minute grace period for clock drift/latency)
        if (webinar.getStartTime() == null || webinar.getStartTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("startTime must be in the future (or at most 5 minutes in the past due to clock drift).");
        }

        // Rule 3 - Webinar Mode Validation
        if (webinar.getMode() == WebinarMode.ONLINE) {
            if (webinar.getMeetingLink() == null || webinar.getMeetingLink().trim().isEmpty()) {
                throw new IllegalArgumentException("meetingLink is required for ONLINE mode.");
            }
        }

        if (webinar.getMode() == WebinarMode.OFFLINE) {
            if (webinar.getVenueName() == null || webinar.getVenueAddress() == null) {
                throw new IllegalArgumentException("venueName and venueAddress are required for OFFLINE mode.");
            }
        }

        if (webinar.getMode() == WebinarMode.HYBRID) {
            if (webinar.getMeetingLink() == null || webinar.getVenueName() == null) {
                throw new IllegalArgumentException("Both meetingLink and venue details are required for HYBRID mode.");
            }
        }
    }

    @Override
    public Webinar getWebinarById(Long webinarId) {
        return webinarRepository.findById(webinarId)
                .orElseThrow(() -> new RuntimeException("Webinar not found with ID: " + webinarId));
    }

    @Override
    public List<Webinar> getAllWebinars() {
        return webinarRepository.findAll();
    }

    @Override
    public List<Webinar> getScheduledWebinars() {
        return webinarRepository.findByStatus(WebinarStatus.SCHEDULED);
    }

    @Override
    @Transactional
    public void cancelWebinar(Long webinarId) {

        Webinar webinar = getWebinarById(webinarId);
        webinar.setStatus(WebinarStatus.CANCELLED);

        webinarRepository.save(webinar);
    }

    @Override
    @Transactional
    public Webinar changeWebinarStatus(Long webinarId, WebinarStatus status) {

        Webinar webinar = getWebinarById(webinarId);
        webinar.setStatus(status);

        return webinarRepository.save(webinar);
    }
    
    @Override
    @Transactional
    public void hardDeleteWebinar(Long webinarId) {

        Webinar webinar = getWebinarById(webinarId);

        webinarRepository.delete(webinar);
    }
}