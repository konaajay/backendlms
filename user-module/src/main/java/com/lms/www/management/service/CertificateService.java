package com.lms.www.management.service;

import java.util.Map;

import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.Certificate;

public interface CertificateService {

        // =========================================================
        // 🔐 GENERATE CERTIFICATE (Manual + Auto)
        // =========================================================
        Certificate generateCertificateIfEligible(
                        Long userId,
                        TargetType targetType,
                        Long targetId,
                        String studentName,
                        String studentEmail,
                        String eventTitle,
                        Double score);

        Certificate generateCertificate(
                        Long userId,
                        TargetType targetType,
                        Long targetId,
                        String studentName,
                        String studentEmail,
                        String eventTitle,
                        Double score);

        // =========================================================
        // 🔎 VERIFY CERTIFICATE (Using Secure Token)
        // =========================================================
        Certificate verifyCertificate(String token);

        // =========================================================
        // 🚫 REVOKE CERTIFICATE
        // =========================================================
        void revokeCertificate(Long certificateId, String reason);

        // =========================================================
        // ⏳ UPDATE EXPIRY DATE
        // =========================================================
        void updateExpiryDate(Long certificateId, String expiryDate);

        // =========================================================
        // 🔄 RENEW CERTIFICATE
        // =========================================================
        void renewCertificate(
                        Long certificateId,
                        String newExpiryDateStr,
                        Long renewedBy,
                        String remarks);

        // =========================================================
        // 📊 DASHBOARD STATS
        // =========================================================
        Map<String, Long> getCertificateStats();

        // =========================================================
        // 📦 BULK GENERATE FOR BATCH
        // =========================================================
        Map<String, Integer> bulkGenerateForBatch(Long batchId);

        // =========================================================
        // 📦 BULK GENERATE FOR TARGET (EXAM / COURSE)
        // =========================================================
        int bulkGenerateCertificates(
                        TargetType targetType,
                        Long targetId);

        void sendCertificateEmailManually(Certificate certificate);
}