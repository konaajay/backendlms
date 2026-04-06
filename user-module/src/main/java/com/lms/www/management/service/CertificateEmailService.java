package com.lms.www.management.service;

public interface CertificateEmailService {

    void sendCertificateEmail(
            String toEmail,
            String studentName,
            String courseName,
            String pdfPath
    );
    
}