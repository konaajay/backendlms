package com.lms.www.affiliate.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.lms.www.affiliate.entity.AffiliateClick;
import com.lms.www.affiliate.repository.AffiliateClickRepository;
import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.service.FraudDetectionService;
import com.lms.www.affiliate.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingServiceImpl implements TrackingService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateClickRepository clickRepository;
    private final FraudDetectionService fraudDetectionService;

    private final Map<String, String> referralCache = new ConcurrentHashMap<>();

    @Override
    public void trackClick(String referralCode,
            Long batchId,
            String ipAddress,
            String userAgent,
            String sessionId) {

        if (referralCode == null || referralCode.isBlank()) {
            return;
        }

        if (fraudDetectionService.isSuspicious(referralCode, ipAddress)) {
            log.warn("Suspicious click detected ip={} referral={}", ipAddress, referralCode);
            return;
        }

        if (sessionId != null && referralCache.containsKey(sessionId)) {
            log.debug("Session already has a referral. Skipping redundant click tracking for session={}", sessionId);
            return;
        }

        // Prevention of memory leak
        if (referralCache.size() > 50_000) {
            referralCache.clear();
        }

        affiliateRepository.findByReferralCode(referralCode).ifPresent(affiliate -> {

            AffiliateClick click = AffiliateClick.builder()
                    .clickedCode(referralCode)
                    .affiliateCode(affiliate.getReferralCode())
                    .batchId(batchId)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            if (click != null) {
                clickRepository.save(click);
            }

            if (sessionId != null) {
                referralCache.put(sessionId, referralCode);
                log.info("Referral stored session={} code={}", sessionId, referralCode);
            }
        });
    }

    @Override
    public String getReferralFromCache(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return referralCache.get(sessionId);
    }

    @Override
    public void clearReferral(String sessionId) {
        if (sessionId != null) {
            referralCache.remove(sessionId);
        }
    }
}
