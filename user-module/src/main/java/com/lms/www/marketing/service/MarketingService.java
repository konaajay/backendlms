package com.lms.www.marketing.service;

import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.entity.AffiliateClick;
import com.lms.www.marketing.model.Lead;
import com.lms.www.affiliate.repository.AffiliateClickRepository;
import com.lms.www.marketing.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketingService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateClickRepository clickRepository;
    private final LeadRepository leadRepository;
    private final EmailService emailService;

    // ================= TRACK CLICK =================
    public void trackClick(String code, String source, String ip) {

        if (code == null || code.isBlank())
            return;

        // ✅ Strict validation
        affiliateRepository.findByReferralCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid referral"));

        // ✅ Idempotency (1 min window)
        boolean exists = clickRepository.existsRecent(
                code,
                ip,
                LocalDateTime.now().minusMinutes(1));

        if (exists)
            return;

        AffiliateClick click = AffiliateClick.builder()
                .affiliateCode(code)
                .clickedCode(code)
                .batchId(1L)
                .ipAddress(ip)
                .build();

        clickRepository.save(click);

        log.info("Click tracked: code={}, source={}, ip={}", code, source, ip);
    }

    // ================= CREATE LEAD =================
    @Transactional
    public void createLead(String email, String mobile, Long batchId) {

        if (email == null || mobile == null) {
            throw new IllegalArgumentException("Invalid lead data");
        }

        if (leadRepository.existsByEmailAndBatchId(email, batchId)) {
            throw new IllegalStateException("Duplicate lead");
        }

        Lead lead = Lead.builder()
                .email(email)
                .mobile(mobile)
                .batchId(batchId)
                .createdAt(LocalDateTime.now())
                .build();

        leadRepository.save(lead);
    }

    // ================= BULK EMAIL =================
    public void sendBulkEmail(List<String> emails, String referralCode) {

        String link = buildLink(referralCode, "email");

        int batchSize = 100;

        for (int i = 0; i < emails.size(); i += batchSize) {

            List<String> batch = emails.subList(i, Math.min(i + batchSize, emails.size()));

            for (String email : batch) {
                try {
                    emailService.send(
                            email,
                            "Course Offer",
                            "Join using this link: " + link);
                } catch (Exception e) {
                    log.error("Email failed: {}", email);
                }
            }
        }
    }

    // ================= LINK =================
    public String buildLink(String code, String source) {
        return "https://yourapp.com/ref?code=" + code + "&source=" + source;
    }

    // ================= CLEANUP JOB =================
    @Scheduled(cron = "0 0 2 * * ?") // daily at 2 AM
    @Transactional
    public void cleanupOldClicks() {

        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        clickRepository.deleteAllByClickedAtBefore(cutoff);

        log.info("Old clicks cleaned");
    }
}
