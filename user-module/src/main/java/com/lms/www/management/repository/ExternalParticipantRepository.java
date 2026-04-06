package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExternalParticipant;

@Repository
public interface ExternalParticipantRepository extends JpaRepository<ExternalParticipant, Long> {

    Optional<ExternalParticipant> findByEmail(String email);

}