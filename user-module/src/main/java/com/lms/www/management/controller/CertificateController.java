package com.lms.www.management.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.enums.CertificateStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.repository.CertificateRepository;
import com.lms.www.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificateRepository certificateRepository;
    private final com.lms.www.security.UserContext userContext;

    // =========================================================
    // 🔐 MANUAL GENERATE
    // =========================================================
    @PostMapping("/manual-generate")
    @PreAuthorize("hasAuthority('CERTIFICATE_GENERATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public Certificate manualGenerate(@RequestBody Map<String, Object> request) {

        Long userId = request.get("userId") != null 
                ? Long.valueOf(request.get("userId").toString()) 
                : (request.get("studentId") != null ? Long.valueOf(request.get("studentId").toString()) : null);
                
        String targetTypeStr = request.get("targetType") != null 
                ? request.get("targetType").toString().toUpperCase() 
                : null;
        TargetType targetType = targetTypeStr != null ? TargetType.valueOf(targetTypeStr) : null;
        
        Long targetId = request.get("targetId") != null 
                ? Long.valueOf(request.get("targetId").toString()) 
                : null;

        String studentName = request.get("studentName") != null ? request.get("studentName").toString() : null;
        String studentEmail = request.get("studentEmail") != null ? request.get("studentEmail").toString() : null;
        String eventTitle = request.get("eventTitle") != null ? request.get("eventTitle").toString() : null;

        Double score = request.get("score") != null
                ? Double.valueOf(request.get("score").toString())
                : null;

        if (userId == null || targetType == null || targetId == null) {
            throw new IllegalArgumentException("Missing required fields: userId, targetType, or targetId");
        }

        return certificateService.generateCertificate(
                userId,
                targetType,
                targetId,
                studentName,
                studentEmail,
                eventTitle,
                score
        );
    }

    // =========================================================
    // 🔎 VERIFY CERTIFICATE (TOKEN)
    // =========================================================
    @GetMapping("/verify")
    public Certificate verifyCertificate(@RequestParam String token) {
        return certificateService.verifyCertificate(token);
    }

    @PostMapping("/verify")
    public Certificate verifyCertificatePost(@RequestBody Map<String, String> request) {
        return certificateService.verifyCertificate(request.get("token"));
    }

    // =========================================================
    // 🌍 PUBLIC VERIFY PAGE (QR)
    // =========================================================
    @GetMapping("/public/{token}")
    public ResponseEntity<String> verifyCertificatePublic(@PathVariable String token) throws IOException {

        Certificate certificate = certificateService.verifyCertificate(token);

        ClassPathResource resource = new ClassPathResource("templates/certificate-verification.html");

        String html = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String issuedDate = certificate.getIssuedDate() != null
                ? certificate.getIssuedDate().toLocalDate().toString()
                : "N/A";

        String expiryDate = certificate.getExpiryDate() != null
                ? certificate.getExpiryDate().toLocalDate().toString()
                : "No Expiry";

        String statusBadge;

        switch (certificate.getStatus()) {

            case ACTIVE:
                if (certificate.getExpiryDate() != null &&
                        certificate.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {

                    statusBadge = "<span class=\"status status-expired\">EXPIRED</span>";
                } else {
                    statusBadge = "<span class=\"status status-active\">VALID CERTIFICATE</span>";
                }
                break;

            case REVOKED:
                statusBadge = "<span class=\"status status-revoked\">REVOKED</span>";
                break;

            case EXPIRED:
                statusBadge = "<span class=\"status status-expired\">EXPIRED</span>";
                break;

            default:
                statusBadge = "<span class=\"status status-revoked\">INVALID</span>";
        }

        String score = certificate.getScore() != null
                ? String.valueOf(certificate.getScore())
                : "N/A";

        html = html.replace("{{statusBadge}}", statusBadge)
                .replace("{{studentName}}", certificate.getStudentName())
                .replace("{{eventTitle}}", certificate.getEventTitle())
                .replace("{{score}}", score)
                .replace("{{issuedDate}}", issuedDate)
                .replace("{{expiryDate}}", expiryDate)
                .replace("{{certificateId}}", String.valueOf(certificate.getCertificateId()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/html")
                .body(html);
    }

    // =========================================================
    // 👤 GET USER CERTIFICATES
    // =========================================================
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('CERTIFICATE_VIEW_SELF') or hasAuthority('CERTIFICATE_VIEW_ALL') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public List<Certificate> getCertificatesByUser(@PathVariable Long userId) {
        return certificateRepository.findByUserId(userId);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('CERTIFICATE_VIEW_SELF') or hasAuthority('ALL_PERMISSIONS')")
    public List<Certificate> getMyCertificates() {
        Long userId = userContext.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return certificateRepository.findByUserId(userId);
    }

    // =========================================================
    // 🚫 REVOKE CERTIFICATE
    // =========================================================
    @PutMapping("/{id}/revoke")
    @PreAuthorize("hasAuthority('CERTIFICATE_REVOKE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public Certificate revokeCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        certificateService.revokeCertificate(id, request.get("reason"));

        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    // =========================================================
    // ⏳ UPDATE EXPIRY
    // =========================================================
    @PutMapping("/{id}/expiry")
    @PreAuthorize("hasAuthority('CERTIFICATE_UPDATE_EXPIRY') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public Certificate updateExpiry(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        certificateService.updateExpiryDate(id, request.get("expiryDate"));

        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    // =========================================================
    // 🔐 DOWNLOAD USING TOKEN
    // =========================================================
    @GetMapping("/download/{token}")
    public ResponseEntity<Resource> downloadCertificateByToken(@PathVariable String token) throws IOException {

        Certificate certificate =
                certificateRepository.findByVerificationToken(token)
                        .orElseThrow(() -> new RuntimeException("Invalid certificate token"));

        if (certificate.getStatus() == CertificateStatus.REVOKED) {
            throw new RuntimeException("Certificate has been revoked");
        }

        if (certificate.getStatus() == CertificateStatus.EXPIRED) {
            throw new RuntimeException("Certificate has expired");
        }

        File file = new File(certificate.getPdfUrl());

        if (!file.exists()) {
            throw new RuntimeException("Certificate file not found");
        }

        Resource resource = new UrlResource(file.toURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);
    }

    // =========================================================
    // 📄 GET CERTIFICATE BY ID
    // =========================================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CERTIFICATE_VIEW_ALL') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public Certificate getCertificateById(@PathVariable Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    // =========================================================
    // 🔄 RENEW CERTIFICATE
    // =========================================================
    @PutMapping("/{id}/renew")
    @PreAuthorize("hasAuthority('CERTIFICATE_RENEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> renewCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {

        certificateService.renewCertificate(
                id,
                (String) request.get("newExpiryDate"),
                Long.valueOf(request.get("renewedBy").toString()),
                (String) request.get("remarks")
        );

        return ResponseEntity.ok("Certificate renewed successfully");
    }

    // =========================================================
    // 📋 GET ALL CERTIFICATES
    // =========================================================
    @GetMapping
    @PreAuthorize("hasAuthority('CERTIFICATE_VIEW_ALL') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    // =========================================================
    // 📊 CERTIFICATE STATS
    // =========================================================
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('CERTIFICATE_STATS_VIEW') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public Map<String, Long> getStats() {
        return certificateService.getCertificateStats();
    }

    // =========================================================
    // 📦 BULK GENERATE
    // =========================================================
    @PostMapping("/bulk-generate")
    @PreAuthorize("hasAuthority('CERTIFICATE_BULK_GENERATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public Map<String, Object> bulkGenerate(@RequestBody Map<String, String> request) {

        TargetType targetType = TargetType.valueOf(request.get("targetType"));
        Long targetId = Long.parseLong(request.get("targetId"));

        int count = certificateService.bulkGenerateCertificates(targetType, targetId);

        return Map.of(
                "message", "Bulk generation completed",
                "generatedCount", count
        );
    }

    // =========================================================
    // 📦 BULK GENERATE BY BATCH
    // =========================================================
    @PostMapping("/bulk-generate/batch/{batchId}")
    @PreAuthorize("hasAuthority('CERTIFICATE_BULK_GENERATE') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public Map<String, Integer> bulkGenerateByBatch(@PathVariable Long batchId) {
        return certificateService.bulkGenerateForBatch(batchId);
    }

    // =========================================================
    // 📧 SEND CERTIFICATE EMAIL
    // =========================================================
    @PostMapping("/{id}/send-email")
    @PreAuthorize("hasAuthority('CERTIFICATE_SEND_EMAIL') or hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<String> sendCertificateEmail(@PathVariable Long id) {

        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (certificate.getPdfUrl() == null) {
            throw new RuntimeException("Certificate PDF not generated");
        }

        certificateService.sendCertificateEmailManually(certificate);

        return ResponseEntity.ok("Certificate email sent successfully");
    }
}