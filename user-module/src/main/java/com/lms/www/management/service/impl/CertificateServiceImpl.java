package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lms.www.management.enums.CertificateStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.model.CertificateAuditLog;
import com.lms.www.management.model.CertificateProgress;
import com.lms.www.management.model.CertificateRenewal;
import com.lms.www.management.model.CertificateTemplate;
import com.lms.www.management.model.Course;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.CertificateAuditLogRepository;
import com.lms.www.management.repository.CertificateProgressRepository;
import com.lms.www.management.repository.CertificateRenewalRepository;
import com.lms.www.management.repository.CertificateRepository;
import com.lms.www.management.repository.CertificateTemplateRepository;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.CertificateEligibilityService;
import com.lms.www.management.service.CertificateEmailService;
import com.lms.www.management.service.CertificatePdfService;
import com.lms.www.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

        private final CertificateRepository certificateRepository;
        private final CertificatePdfService certificatePdfService;
        private final CertificateAuditLogRepository auditLogRepository;
        private final CertificateProgressRepository certificateProgressRepository;
        private final CertificateRenewalRepository renewalRepository;
        private final BatchRepository batchRepository;
        private final CourseRepository courseRepository;
        private final StudentBatchRepository studentBatchRepository;
        private final CertificateTemplateRepository templateRepository;
        private final CertificateEmailService certificateEmailService;
        private final CertificateEligibilityService certificateEligibilityService;
        private final ExamRepository examRepository;

        // =========================================================
        // 🔐 AUTO GENERATE CERTIFICATE (If Eligible)
        // =========================================================
        @Override
        public Certificate generateCertificateIfEligible(
                        Long userId,
                        TargetType targetType,
                        Long targetId,
                        String studentName,
                        String studentEmail,
                        String eventTitle,
                        Double score) {

                // 1️⃣ Strictly evaluate custom eligibility rules (e.g., Attendance %)
                if (!certificateEligibilityService.isEligible(userId, targetType, targetId)) {
                        throw new IllegalStateException(
                                        "User is not eligible for certificate (e.g., Attendance criteria not met).");
                }

                // 2️⃣ Record successful progress if eligible
                CertificateProgress progress = certificateProgressRepository
                                .findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                                .orElse(CertificateProgress.builder()
                                                .userId(userId)
                                                .targetType(targetType)
                                                .targetId(targetId)
                                                .build());

                progress.setScore(score);
                progress.setEligibilityStatus(com.lms.www.management.enums.CertificateEligibilityStatus.ELIGIBLE);
                progress.setUpdatedAt(LocalDateTime.now());
                certificateProgressRepository.save(progress);

                // 3️⃣ Generate Certificate
                return createCertificateInternal(userId, targetType, targetId, studentName, studentEmail, eventTitle,
                                score);
        }

        // =========================================================
        // 🔐 MANUAL GENERATE CERTIFICATE (Bypass Eligibility)
        // =========================================================
        @Override
        public Certificate generateCertificate(
                        Long userId,
                        TargetType targetType,
                        Long targetId,
                        String studentName,
                        String studentEmail,
                        String eventTitle,
                        Double score) {

                // Record progress implicitly for manually generated certificates
                CertificateProgress progress = certificateProgressRepository
                                .findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                                .orElse(CertificateProgress.builder()
                                                .userId(userId)
                                                .targetType(targetType)
                                                .targetId(targetId)
                                                .build());

                progress.setScore(score);
                progress.setEligibilityStatus(com.lms.www.management.enums.CertificateEligibilityStatus.ELIGIBLE);
                progress.setUpdatedAt(LocalDateTime.now());
                certificateProgressRepository.save(progress);

                return createCertificateInternal(userId, targetType, targetId, studentName, studentEmail, eventTitle,
                                score);
        }

        private Certificate createCertificateInternal(
                        Long userId,
                        TargetType targetType,
                        Long targetId,
                        String studentName,
                        String studentEmail,
                        String eventTitle,
                        Double score) {

                if (userId == null || targetId == null) {
                    throw new IllegalArgumentException("User ID and Target ID must not be null");
                }

                return certificateRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                                .orElseGet(() -> {
                                        // Proceed with generation if not found
                                        return generateNewCertificate(userId, targetType, targetId, studentName, studentEmail, eventTitle, score);
                                });
        }

        private Certificate generateNewCertificate(
                        Long userId,
                        TargetType targetType,
                        Long targetId,
                        String studentName,
                        String studentEmail,
                        String eventTitle,
                        Double score) {

                String certificateId = "CERT-" + UUID.randomUUID()
                                .toString().substring(0, 8).toUpperCase();

                if (certificateId == null) {
                    throw new RuntimeException("Failed to generate certificate ID");
                }

                String verificationToken = UUID.randomUUID().toString();

                CertificateTemplate template = null;

                // 1️⃣ Course-specific template
                if (targetType == TargetType.COURSE && targetId != null) {
                        Course course = courseRepository.findById(targetId).orElse(null);
                        if (course != null && course.getCertificateTemplateId() != null) {
                                Long courseTemplateId = course.getCertificateTemplateId();
                                template = templateRepository.findById(courseTemplateId).orElse(null);
                        }
                }

                // 2️⃣ Exam-specific template
                else if (targetType == TargetType.EXAM && targetId != null) {
                        Exam exam = examRepository.findById(targetId).orElse(null);
                        if (exam != null && exam.getCertificateTemplateId() != null) {
                                Long examTemplateId = exam.getCertificateTemplateId();
                                template = templateRepository.findById(examTemplateId).orElse(null);
                        }
                }

                // 3️⃣ Target-specific template
                if (template == null && targetId != null) {
                        template = templateRepository
                                        .findFirstByTargetTypeAndTargetIdAndIsActiveTrue(targetType, targetId)
                                        .orElse(null);
                }

                // 4️⃣ Target default template
                if (template == null) {
                        template = templateRepository
                                        .findFirstByTargetTypeAndTargetIdIsNullAndIsActiveTrue(targetType)
                                        .orElse(null);
                }

                if (template == null) {
                        template = templateRepository.findFirstByIsActiveTrue().orElse(null);
                }

                if (template == null) {
                        try {
                                template = CertificateTemplate.builder()
                                                .templateName("System Default Template")
                                                .templateType("DEFAULT")
                                                .isActive(true)
                                                .targetType(targetType)
                                                .build();
                                template = templateRepository.save(template);
                        } catch (Exception e) {
                                throw new IllegalStateException("No certificate template configured and failed to create fallback", e);
                        }
                }

                Certificate certificate = Certificate.builder()
                                .certificateId(certificateId)
                                .verificationToken(verificationToken)
                                .studentId(userId)
                                .studentName(studentName)
                                .studentEmail(studentEmail)
                                .targetType(targetType)
                                .targetId(targetId)
                                .eventTitle(eventTitle)
                                .score(score != null ? score : 0.0)
                                .issueDate(LocalDateTime.now())
                                .templateId(template.getId())
                                .status(CertificateStatus.ACTIVE)
                                .version(1)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                Certificate saved = certificateRepository.save(certificate);

                if (saved != null && saved.getId() != null) {
                    Long savedId = saved.getId();
                    auditLogRepository.save(
                                    CertificateAuditLog.builder()
                                                    .certificateId(savedId)
                                                    .action("GENERATED")
                                                    .performedBy(userId)
                                                    .actionDate(LocalDateTime.now())
                                                    .remarks("Certificate generated")
                                                    .build());
                }

                LocalDateTime startDate = null;
                LocalDateTime endDate = null;

                if (targetType == TargetType.COURSE) {

                        StudentBatch studentBatch = studentBatchRepository
                                        .findFirstByStudentIdAndCourseIdAndStatus(
                                                        userId, targetId, "COMPLETED")
                                        .orElse(null);

                        if (studentBatch != null && studentBatch.getBatchId() != null) {

                                var batch = batchRepository
                                                .findById(studentBatch.getBatchId())
                                                .orElse(null);

                                Course course = targetId != null ? courseRepository
                                                .findById(targetId)
                                                .orElse(null) : null;

                                if (batch != null && course != null) {

                                        startDate = batch.getStartDate().atStartOfDay();

                                        if (course.getValidityInDays() != null) {
                                                endDate = startDate.plusDays(course.getValidityInDays());
                                        }
                                }
                        }
                }

                String pdfPath = certificatePdfService.generatePdf(
                                saved,
                                studentName,
                                eventTitle,
                                startDate,
                                endDate);

                saved.setPdfUrl(pdfPath);
                saved.setUpdatedAt(LocalDateTime.now());

                Certificate finalCertificate = certificateRepository.save(saved);

                try {
                        certificateEmailService.sendCertificateEmail(
                                        studentEmail,
                                        studentName,
                                        eventTitle,
                                        pdfPath);
                } catch (Exception e) {
                        System.out.println("❌ Email sending failed");
                        e.printStackTrace();
                }

                return finalCertificate;
        }

        // =========================================================
        // VERIFY
        // =========================================================
        @Override
        public Certificate verifyCertificate(String token) {
                return certificateRepository.findByVerificationToken(token)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid certificate token"));
        }

        // =========================================================
        // REVOKE
        // =========================================================
        @Override
        public void revokeCertificate(Long certificateId, String reason) {

                Certificate certificate = certificateRepository.findById(certificateId)
                                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

                certificate.setStatus(CertificateStatus.REVOKED);
                certificate.setRevokedReason(reason);
                certificate.setRevokedAt(LocalDateTime.now());
                certificate.setUpdatedAt(LocalDateTime.now());

                certificateRepository.save(certificate);
        }

        // =========================================================
        // UPDATE EXPIRY
        // =========================================================
        @Override
        public void updateExpiryDate(Long certificateId, String expiryDateStr) {

                Certificate certificate = certificateRepository.findById(certificateId)
                                .orElseThrow(() -> new RuntimeException("Certificate not found"));

                LocalDateTime expiryDate = LocalDateTime.parse(expiryDateStr);

                certificate.setExpiryDate(expiryDate);
                certificate.setUpdatedAt(LocalDateTime.now());
                certificate.setStatus(
                                expiryDate.isBefore(LocalDateTime.now())
                                                ? CertificateStatus.EXPIRED
                                                : CertificateStatus.ACTIVE);

                certificateRepository.save(certificate);
        }

        // =========================================================
        // RENEW
        // =========================================================
        @Override
        public void renewCertificate(Long certificateId,
                        String newExpiryDateStr,
                        Long renewedBy,
                        String remarks) {

                Certificate certificate = certificateRepository.findById(certificateId)
                                .orElseThrow(() -> new RuntimeException("Certificate not found"));

                LocalDateTime newExpiry = LocalDateTime.parse(newExpiryDateStr);

                certificate.setExpiryDate(newExpiry);
                certificate.setVersion(certificate.getVersion() + 1);
                certificate.setUpdatedAt(LocalDateTime.now());
                certificate.setStatus(CertificateStatus.ACTIVE);

                certificateRepository.save(certificate);

                renewalRepository.save(
                                CertificateRenewal.builder()
                                                .certificateId(certificate.getId())
                                                .previousExpiry(certificate.getExpiryDate())
                                                .newExpiry(newExpiry)
                                                .renewedOn(LocalDateTime.now())
                                                .renewedBy(renewedBy)
                                                .remarks(remarks)
                                                .build());
        }

        // =========================================================
        // STATS
        // =========================================================
        @Override
        public Map<String, Long> getCertificateStats() {

                Map<String, Long> stats = new HashMap<>();

                stats.put("total", certificateRepository.count());
                stats.put("active", certificateRepository.countByStatus(CertificateStatus.ACTIVE));
                stats.put("expired", certificateRepository.countByStatus(CertificateStatus.EXPIRED));
                stats.put("revoked", certificateRepository.countByStatus(CertificateStatus.REVOKED));

                LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);

                stats.put("issuedThisMonth",
                                certificateRepository.countByIssuedDateAfter(startOfMonth));

                return stats;
        }

        // =========================================================
        // BULK GENERATE BY BATCH
        // =========================================================
        @Override
        public Map<String, Integer> bulkGenerateForBatch(Long batchId) {

                List<StudentBatch> students = studentBatchRepository.findByBatchId(batchId);

                int total = students.size();
                int generated = 0;
                int alreadyExists = 0;

                for (StudentBatch student : students) {

                        if (!"COMPLETED".equalsIgnoreCase(student.getStatus()))
                                continue;

                        boolean exists = certificateRepository.existsByUserIdAndTargetTypeAndTargetId(
                                        student.getStudentId(),
                                        TargetType.COURSE,
                                        student.getCourseId());

                        if (exists) {
                                alreadyExists++;
                                continue;
                        }

                        Course course = courseRepository
                                        .findById(student.getCourseId())
                                        .orElse(null);

                        if (course == null)
                                continue;

                        generateCertificateIfEligible(
                                        student.getStudentId(),
                                        TargetType.COURSE,
                                        student.getCourseId(),
                                        student.getStudentName(),
                                        student.getStudentEmail(),
                                        course.getCourseName(),
                                        null);

                        generated++;
                }

                return Map.of(
                                "totalStudents", total,
                                "generated", generated,
                                "alreadyExists", alreadyExists);
        }

        @Override
        public int bulkGenerateCertificates(TargetType targetType, Long targetId) {
                return 0;
        }

        @Override
        public void sendCertificateEmailManually(Certificate certificate) {

                if (certificate.getStudentEmail() == null ||
                                certificate.getStudentEmail().isBlank()) {

                        throw new RuntimeException("Student email not available");
                }

                certificateEmailService.sendCertificateEmail(
                                certificate.getStudentEmail(),
                                certificate.getStudentName(),
                                certificate.getEventTitle(),
                                certificate.getPdfUrl());
        }
}