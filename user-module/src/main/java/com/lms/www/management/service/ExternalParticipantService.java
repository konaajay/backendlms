package com.lms.www.management.service;

import java.util.List;
import java.util.Optional;

import com.lms.www.management.model.ExternalParticipant;

public interface ExternalParticipantService {

    ExternalParticipant createExternalParticipant(ExternalParticipant participant);

    ExternalParticipant getParticipantById(Long participantId);

    Optional<ExternalParticipant> findByEmail(String email);

    List<ExternalParticipant> getAllParticipants();
}
