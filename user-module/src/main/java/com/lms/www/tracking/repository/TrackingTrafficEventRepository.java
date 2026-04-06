package com.lms.www.tracking.repository;

import com.lms.www.tracking.model.TrafficEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingTrafficEventRepository extends JpaRepository<TrafficEvent, Long> {
    List<TrafficEvent> findBySessionId(String sessionId);
}
