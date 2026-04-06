package com.lms.www.management.service;

import java.time.LocalDateTime;

import com.lms.www.management.model.Certificate;

public interface CertificatePdfService {
	String generatePdf(Certificate certificate,
            String studentName,
            String eventTitle,
            LocalDateTime startDate,
            LocalDateTime endDate);
}
