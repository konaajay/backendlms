package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.WebinarRegistration;

public interface WebinarRegistrationService {

    WebinarRegistration registerStudent(Long webinarId, Long userId);

    WebinarRegistration registerExternalParticipant(Long webinarId, Long participantId);

    void cancelRegistration(Long registrationId);

    List<WebinarRegistration> getRegistrationsByWebinar(Long webinarId);
}
