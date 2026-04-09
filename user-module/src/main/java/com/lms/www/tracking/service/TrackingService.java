package com.lms.www.tracking.service;

import com.lms.www.tracking.dto.TrackingRequest;
import com.lms.www.tracking.model.TrafficEvent;
import com.lms.www.tracking.repository.TrackingTrafficEventRepository;
import com.lms.www.marketing.repository.TrackedLinkRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("mainTrackingService")
@RequiredArgsConstructor
public class TrackingService {

    private static final Logger log = LoggerFactory.getLogger(TrackingService.class);

    private final TrackingTrafficEventRepository trafficEventRepository;
    private final TrackedLinkRepository trackedLinkRepository;

    private final Map<String, String> referralCache = new ConcurrentHashMap<>();

    @Transactional
    public void track(String tid, String eventType, String sessionId, String metadata) {
        log.info("TRACK EVENT: tid={}, type={}, session={}", tid, eventType, sessionId);
        
        TrafficEvent event = new TrafficEvent();
        event.setTrackedLinkId(tid);
        event.setEventType(eventType);
        event.setSessionId(sessionId);
        event.setMetadataJson(metadata);
        
        if (tid != null && !tid.isBlank() && !tid.equalsIgnoreCase("null")) {
            trackedLinkRepository.findByTrackedLinkId(tid).ifPresent(event::setTrackedLink);
        }
        
        trafficEventRepository.save(event);
    }

    public void track(TrackingRequest request) {
        track(request.getTrackedLinkId(), request.getEventType(), request.getSessionId(), request.getMetadata());
    }

    // Affiliate specific cache logic integrated here if needed
    public void cacheReferral(String sessionId, String referralCode) {
        if (sessionId != null && referralCode != null) {
            referralCache.put(sessionId, referralCode);
        }
    }

    public String getReferralFromCache(String sessionId) {
        return sessionId != null ? referralCache.get(sessionId) : null;
    }

    public void clearReferral(String sessionId) {
        if (sessionId != null) {
            referralCache.remove(sessionId);
        }
    }
}
