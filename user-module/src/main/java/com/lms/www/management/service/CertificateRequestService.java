package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.CertificateRequest;

public interface CertificateRequestService {

    CertificateRequest submitRequest(
            Long userId,
            String studentName,
            String studentEmail,
            TargetType targetType,
            Long targetId,
            String eventTitle,
            Double score);

    List<CertificateRequest> getPendingRequests();

    CertificateRequest approveRequest(Long requestId, Long adminId, String adminComment);

    CertificateRequest rejectRequest(Long requestId, Long adminId, String adminComment);
}
