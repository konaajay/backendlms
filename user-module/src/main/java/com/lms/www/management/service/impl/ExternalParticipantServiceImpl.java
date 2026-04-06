package com.lms.www.management.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.model.ExternalParticipant;
import com.lms.www.management.repository.ExternalParticipantRepository;
import com.lms.www.management.service.ExternalParticipantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalParticipantServiceImpl implements ExternalParticipantService {

    private final ExternalParticipantRepository externalParticipantRepository;

    @Override
    @Transactional
    public ExternalParticipant createExternalParticipant(ExternalParticipant participant) {
        // Validate email uniqueness
        Optional<ExternalParticipant> existing = externalParticipantRepository.findByEmail(participant.getEmail());
        if (existing.isPresent()) {
            return existing.get(); // Return existing record instead of creating duplicate
        }

        return externalParticipantRepository.save(participant);
    }

    @Override
    public ExternalParticipant getParticipantById(Long participantId) {
        return externalParticipantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("External participant not found with ID: " + participantId));
    }

    @Override
    public Optional<ExternalParticipant> findByEmail(String email) {
        return externalParticipantRepository.findByEmail(email);
    }

    @Override
    public List<ExternalParticipant> getAllParticipants() {
        return externalParticipantRepository.findAll();
    }
}
