package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.SessionContent;

@Repository
public interface SessionContentRepository extends JpaRepository<SessionContent, Long> {

    // Get all contents for a session
    List<SessionContent> findBySessionId(Long sessionId);

    List<SessionContent> findBySessionIdIn(List<Long> sessionIds);

    // Get only ACTIVE contents for a session
    List<SessionContent> findBySessionIdAndStatus(Long sessionId, String status);

    @org.springframework.data.jpa.repository.Query("SELECT sc FROM SessionContent sc WHERE sc.sessionId = :sessionId AND (sc.status IN :statuses OR sc.status IS NULL)")
    List<SessionContent> findBySessionIdAndStatusIn(Long sessionId, java.util.List<String> statuses);
}
