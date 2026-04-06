package com.lms.www.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.ParticipantType;
import com.lms.www.management.enums.PaymentStatus;
import com.lms.www.management.enums.RegistrationStatus;
import com.lms.www.management.enums.WebinarType;
import com.lms.www.management.model.ExternalParticipant;
import com.lms.www.management.model.Webinar;
import com.lms.www.management.model.WebinarRegistration;
import com.lms.www.management.repository.ExternalParticipantRepository;
import com.lms.www.management.repository.WebinarRegistrationRepository;
import com.lms.www.management.repository.WebinarRepository;
import com.lms.www.management.service.WebinarRegistrationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebinarRegistrationServiceImpl implements WebinarRegistrationService {

    private final WebinarRegistrationRepository registrationRepository;
    private final WebinarRepository webinarRepository;
    private final ExternalParticipantRepository externalParticipantRepository;

    @Override
    @Transactional
    public WebinarRegistration registerStudent(Long webinarId, Long userId) {
        Webinar webinar = getWebinarForRegistration(webinarId);

        // Check if already registered
        List<WebinarRegistration> existing = registrationRepository.findByUserId(userId);
        if (existing.stream().anyMatch(r -> r.getWebinar().getWebinarId().equals(webinarId)
                && r.getRegistrationStatus() != RegistrationStatus.CANCELLED)) {
            throw new RuntimeException("Student already registered for this webinar");
        }

        WebinarRegistration registration = new WebinarRegistration();
        registration.setWebinar(webinar);
        registration.setParticipantType(ParticipantType.INTERNAL);
        registration.setUserId(userId);

        return completeRegistration(registration, webinar);
    }

    @Override
    @Transactional
    public WebinarRegistration registerExternalParticipant(Long webinarId, Long participantId) {
        Webinar webinar = getWebinarForRegistration(webinarId);

        if (!Boolean.TRUE.equals(webinar.getAllowExternal())) {
            throw new RuntimeException("External participants are not allowed for this webinar");
        }

        ExternalParticipant participant = externalParticipantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("External Participant not found"));

        // Check if already registered
        List<WebinarRegistration> existing = registrationRepository.findByParticipant_ParticipantId(participantId);
        if (existing.stream().anyMatch(r -> r.getWebinar().getWebinarId().equals(webinarId)
                && r.getRegistrationStatus() != RegistrationStatus.CANCELLED)) {
            throw new RuntimeException("Participant already registered for this webinar");
        }

        WebinarRegistration registration = new WebinarRegistration();
        registration.setWebinar(webinar);
        registration.setParticipantType(ParticipantType.EXTERNAL);
        registration.setParticipant(participant);

        return completeRegistration(registration, webinar);
    }

    private Webinar getWebinarForRegistration(Long webinarId) {
        Webinar webinar = webinarRepository.findById(webinarId)
                .orElseThrow(() -> new RuntimeException("Webinar not found"));

        // Capacity Check Rule
        if (webinar.getRegisteredCount() >= webinar.getMaxParticipants()) {
            throw new RuntimeException("Registration for webinar is closed due to capacity");
        }

        return webinar;
    }

    private WebinarRegistration completeRegistration(WebinarRegistration registration, Webinar webinar) {
        if (webinar.getType() == WebinarType.FREE) {
            registration.setPaymentStatus(PaymentStatus.SUCCESS);
            registration.setRegistrationStatus(RegistrationStatus.REGISTERED);
        } else {
            registration.setPaymentStatus(PaymentStatus.PENDING);
            registration.setRegistrationStatus(RegistrationStatus.PENDING);
        }

        WebinarRegistration savedRegistration = registrationRepository.save(registration);

        // Update capacity
        if (savedRegistration.getRegistrationStatus() == RegistrationStatus.REGISTERED) {
            webinar.setRegisteredCount(webinar.getRegisteredCount() + 1);
            webinarRepository.save(webinar);
        }

        return savedRegistration;
    }

    @Override
    public List<WebinarRegistration> getRegistrationsByWebinar(Long webinarId) {
        return registrationRepository.findByWebinar_WebinarId(webinarId);
    }

    @Override
    @Transactional
    public void cancelRegistration(Long registrationId) {
        WebinarRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        registration.setRegistrationStatus(RegistrationStatus.CANCELLED);

        if (registration.getPaymentStatus() == PaymentStatus.SUCCESS) {
            // Mocking Refund execution
            registration.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        registrationRepository.save(registration);

        // Decrease capacity count
        Webinar webinar = registration.getWebinar();
        if (webinar.getRegisteredCount() > 0) {
            webinar.setRegisteredCount(webinar.getRegisteredCount() - 1);
            webinarRepository.save(webinar);
        }
    }
}