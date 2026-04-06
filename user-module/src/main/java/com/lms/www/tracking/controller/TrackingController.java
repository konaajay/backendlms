package com.lms.www.tracking.controller;

import com.lms.www.marketing.repository.TrackedLinkRepository;
import com.lms.www.tracking.dto.TrackingRequest;
import com.lms.www.tracking.service.TrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrackingController {

    private final TrackingService trackingService;
    private final TrackedLinkRepository trackedLinkRepository;

    /**
     * Redirect-based tracking (High Reliability)
     */
    @GetMapping("/t/{tid}")
    public void redirectTracking(@PathVariable String tid, HttpSession session, HttpServletResponse response) throws IOException {
        String sessionId = session.getId();
        log.info("REDIRECT TRACK: tid={}, session={}", tid, sessionId);
        
        trackingService.track(tid, "PAGE_VIEW", sessionId, "Server Redirect");
        
        String targetUrl = trackedLinkRepository.findByTrackedLinkId(tid)
                .map(link -> "/l/" + link.getLandingSlug() + "?tid=" + tid)
                .orElse("/");
        
        response.sendRedirect(targetUrl);
    }

    /**
     * Event tracking (JS-based)
     */
    @PostMapping("/track")
    public ResponseEntity<Void> trackEvent(@RequestBody TrackingRequest request) {
        trackingService.track(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Affiliate click tracking
     */
    @GetMapping("/click")
    public ResponseEntity<String> trackClick(
            @RequestParam String ref,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String sessionId,
            HttpServletRequest request) {

        log.info("AFFILIATE CLICK: ref={}, session={}", ref, sessionId);
        
        // Track as a traffic event too
        trackingService.track(ref, "AFFILIATE_CLICK", sessionId, "IP: " + request.getRemoteAddr());
        
        // Cache for attribution
        if (sessionId != null) {
            trackingService.cacheReferral(sessionId, ref);
        }

        return ResponseEntity.ok("Click tracked successfully");
    }
}
