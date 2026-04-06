package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.CertificateRequestStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.Certificate;
import com.lms.www.management.model.CertificateRequest;
import com.lms.www.management.repository.CertificateRepository;
import com.lms.www.management.repository.CertificateRequestRepository;
import com.lms.www.management.service.CertificateEligibilityService;
import com.lms.www.management.service.CertificateRequestService;
import com.lms.www.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateRequestServiceImpl implements CertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateEligibilityService certificateEligibilityService;
    private final CertificateService certificateService;

    @Override
    public CertificateRequest submitRequest(
            Long userId,
            String studentName,
            String studentEmail,
            TargetType targetType,
            Long targetId,
            String eventTitle,
            Double score) {

        // 1. Check if a certificate already exists
        if (certificateRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            throw new IllegalStateException("Certificate already generated for this user and target.");
        }

        // 2. Check if a PENDING request already exists
        if (certificateRequestRepository.existsByUserIdAndTargetTypeAndTargetIdAndStatus(
                userId, targetType, targetId, CertificateRequestStatus.PENDING)) {
            throw new IllegalStateException("A pending certificate request already exists.");
        }

        // 3. Validate eligibility
        if (!certificateEligibilityService.isEligible(userId, targetType, targetId)) {
            throw new IllegalStateException(
                    "User is not eligible for a certificate (e.g., Attendance criteria not met or not completed).");
        }

        // 4. Create and save PENDING request
        CertificateRequest request = CertificateRequest.builder()
                .userId(userId)
                .studentName(studentName)
                .studentEmail(studentEmail)
                .targetType(targetType)
                .targetId(targetId)
                .eventTitle(eventTitle)
                .score(score)
                .status(CertificateRequestStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build();

        return certificateRequestRepository.save(request);
    }

    @Override
    public List<CertificateRequest> getPendingRequests() {
        return certificateRequestRepository.findAllByStatusOrderByRequestDateDesc(CertificateRequestStatus.PENDING);
    }

    @Override
    public CertificateRequest approveRequest(Long requestId, Long adminId, String adminComment) {
        CertificateRequest request = certificateRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate request not found."));

        if (request.getStatus() != CertificateRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be approved.");
        }

        if (certificateRepository.existsByUserIdAndTargetTypeAndTargetId(
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId())) {
            throw new IllegalStateException("Certificate already generated.");
        }

        request.setStatus(CertificateRequestStatus.APPROVED);
        request.setReviewedBy(adminId);
        request.setReviewedAt(LocalDateTime.now());
        request.setAdminComment(adminComment);

        // Generate the actual certificate
        Certificate certificate = certificateService.generateCertificate(
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId(),
                request.getStudentName(),
                request.getStudentEmail(),
                request.getEventTitle(),
                request.getScore());

        request.setCertificateId(certificate.getId());

        return certificateRequestRepository.save(request);
    }

    @Override
    public CertificateRequest rejectRequest(Long requestId, Long adminId, String adminComment) {
        if (adminComment == null || adminComment.isBlank()) {
            throw new IllegalArgumentException("Admin comment is required when rejecting a request.");
        }

        CertificateRequest request = certificateRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate request not found."));

        if (request.getStatus() != CertificateRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be rejected.");
        }

        request.setStatus(CertificateRequestStatus.REJECTED);
        request.setReviewedBy(adminId);
        request.setReviewedAt(LocalDateTime.now());
        request.setAdminComment(adminComment);

        return certificateRequestRepository.save(request);
    }
}
