package com.lms.www.marketing.service;

import com.lms.www.affiliate.entity.AffiliateClick;
import com.lms.www.affiliate.repository.AffiliateClickRepository;
import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.marketing.model.Lead;
import com.lms.www.marketing.repository.LeadRepository;
import com.lms.www.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lms.www.management.model.Course;
import com.lms.www.management.model.Batch;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.affiliate.service.AffiliateLeadService;

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
    private final AffiliateLeadService affiliateLeadService;
    private final CourseRepository courseRepository;
    private final BatchRepository batchRepository;

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
    public void createLead(String name, String email, String phone, Long batchId, String referralCode) {
        createMarketingLead(name, email, phone, "Default Course", referralCode, null, null, referralCode);
    }

    @Transactional
    public void createMarketingLead(String name, String email, String phone, String courseInterest, 
                                   String utmSource, String utmMedium, String utmCampaign, String referralCode) {

        if (email == null || phone == null) {
            throw new IllegalArgumentException("Invalid lead data");
        }

        if (leadRepository.existsByEmailAndUtmCampaign(email, utmCampaign)) {
            // Already a lead for this campaign
            return;
        }

        Lead lead = Lead.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .mobile(phone) 
                .courseInterest(courseInterest)
                .source(utmSource)
                .utmSource(utmSource)
                .utmMedium(utmMedium)
                .utmCampaign(utmCampaign)
                .referralCode(referralCode)
                .createdAt(LocalDateTime.now())
                .build();

        leadRepository.save(lead);
        log.info("Marketing Lead captured: email={}, name={}, utmCampaign={}", email, name, utmCampaign);

        // ✅ Bridge to Affiliate Module
        if (referralCode != null && referralCode.startsWith("AFF-")) {
            try {
                // Find courseId from courseInterest (String)
                Course course = courseRepository.findByCourseTitle(courseInterest)
                        .orElseGet(() -> courseRepository.findByCourseName(courseInterest).orElse(null));
                
                if (course != null) {
                    Long courseId = course.getCourseId();
                    // Find first active batch for this course
                    Long batchId = batchRepository.findByCourseId(courseId).stream()
                            .filter(b -> "ACTIVE".equals(b.getStatus()) || "Upcoming".equals(b.getStatus()) || "Running".equals(b.getStatus()))
                            .map(Batch::getBatchId)
                            .findFirst()
                            .orElse(null);
                            
                    if (batchId != null) {
                        affiliateLeadService.createLead(name, phone, email, courseId, batchId, referralCode, null);
                        log.info("Affiliate Lead synced for code: {}", referralCode);
                    } else {
                        log.warn("No active batch found for course {} - Lead not synced to affiliate", courseInterest);
                    }
                } else {
                    log.warn("Course Interest '{}' not found in database - Lead not synced to affiliate", courseInterest);
                }
            } catch (Exception e) {
                log.error("Failed to bridge lead to affiliate module: {}", e.getMessage());
            }
        }
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
