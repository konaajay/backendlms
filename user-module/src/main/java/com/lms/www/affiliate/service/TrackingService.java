package com.lms.www.affiliate.service;

import org.springframework.stereotype.Service;

@Service("affiliateTrackingService")
public interface TrackingService {
    void trackClick(String referralCode, Long batchId, String ipAddress, String userAgent, String sessionId);
    String getReferralFromCache(String sessionId);
    void clearReferral(String sessionId);
}