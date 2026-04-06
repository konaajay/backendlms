package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    // Get all sessions under a batch
    List<Session> findByBatchId(Long batchId);

    // Get sessions by batch and status (Running / Upcoming / Completed)
    List<Session> findByBatchIdAndStatus(Long batchId, String status);
    
    List<Session> findByTopicTopicId(Long topicId);
    
}
