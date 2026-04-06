package com.lms.www.management.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lms.www.management.enums.CertificateStatus;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.model.CertificateAuditLog;
import com.lms.www.management.repository.CertificateAuditLogRepository;
import com.lms.www.management.repository.CertificateRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CertificateExpiryScheduler {

    private final CertificateRepository certificateRepository;
    private final CertificateAuditLogRepository auditLogRepository;

    // 🔥 Runs every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void expireCertificates() {

        LocalDateTime now = LocalDateTime.now();

        List<Certificate> expiredCertificates =
                certificateRepository.findByExpiryDateBeforeAndStatus(
                        now,
                        CertificateStatus.ACTIVE
                );

        if (expiredCertificates.isEmpty()) {
            return;
        }

        for (Certificate certificate : expiredCertificates) {

            certificate.setStatus(CertificateStatus.EXPIRED);
            certificate.setUpdatedAt(now);

            auditLogRepository.save(
                    CertificateAuditLog.builder()
                            .certificateId(certificate.getId())
                            .action("EXPIRED")
                            .performedBy(null) // system action
                            .actionDate(now)
                            .remarks("Certificate auto-expired by scheduler")
                            .build()
            );
        }

        certificateRepository.saveAll(expiredCertificates);

        System.out.println("Expired certificates updated: "
                + expiredCertificates.size());
    }
}