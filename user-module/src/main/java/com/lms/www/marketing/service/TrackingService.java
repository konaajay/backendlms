package com.lms.www.marketing.service;

import com.lms.www.marketing.dto.TrackingRequest;
import com.lms.www.tracking.model.TrafficEvent;

import com.lms.www.marketing.repository.MarketingTrafficEventRepository;
import com.lms.www.marketing.repository.TrackedLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("marketingTrackingService")
public class TrackingService {

    private static final Logger log = LoggerFactory.getLogger(TrackingService.class);

    @Autowired
    private MarketingTrafficEventRepository trafficEventRepository;
    
    @Autowired
    private TrackedLinkRepository trackedLinkRepository;

    @Transactional
    public void track(String tid, String eventType, String sessionId, String metadata) {
        log.info("TRACK EVENT: tid={}, type={}, session={}", tid, eventType, sessionId);
        
        TrafficEvent event = new TrafficEvent();
        event.setTrackedLinkId(tid);
        event.setEventType(eventType);
        event.setSessionId(sessionId);
        event.setMetadataJSON(metadata);
        
        // Optional link to entity if exists
        trackedLinkRepository.findByTrackedLinkId(tid).ifPresent(event::setTrackedLink);
        
        trafficEventRepository.save(event);
    }

    public void track(TrackingRequest request) {
        track(request.getTrackedLinkId(), request.getEventType(), request.getSessionId(), request.getMetadata());
    }
}
