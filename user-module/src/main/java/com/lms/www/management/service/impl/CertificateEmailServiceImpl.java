package com.lms.www.management.service.impl;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.lms.www.management.service.CertificateEmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateEmailServiceImpl implements CertificateEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendCertificateEmail(String toEmail,
                                     String studentName,
                                     String courseName,
                                     String pdfPath) {

        try {

            // ============================
            // 🔍 Validate Email
            // ============================
            if (toEmail == null || toEmail.isBlank()) {
                System.err.println("⚠ Email not provided. Skipping email sending.");
                return; // Do NOT crash certificate generation
            }

            System.out.println("📧 Sending certificate to: " + toEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);
            
            helper.setFrom("lmssender4@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Your Certificate - " + courseName);

            helper.setText(
                "Dear " + studentName + ",\n\n" +
                "Congratulations! Please find your certificate attached.\n\n" +
                "Regards,\nLMS Team"
            );

            // ============================
            // 📄 Validate PDF File
            // ============================
            File fileObj = new File(pdfPath);
            
            System.out.println("PDF PATH = " + fileObj.getAbsolutePath());
            System.out.println("FILE EXISTS = " + fileObj.exists());
            System.out.println("FILE SIZE = " + fileObj.length());

            if (!fileObj.exists()) {
                System.err.println("⚠ Certificate PDF not found at: " + pdfPath);
                return; // Do NOT crash system
            }

            FileSystemResource file = new FileSystemResource(new File(pdfPath));
            helper.addAttachment("certificate.pdf", file);

            mailSender.send(message);

            System.out.println("✅ Certificate email sent successfully.");

        } catch (Exception e) {

            // ============================
            // 🚫 DO NOT BREAK API
            // ============================
            System.err.println("⚠ Failed to send certificate email: " + e.getMessage());
            e.printStackTrace();

            // ❌ Removed throwing RuntimeException
            // Certificate should still generate even if email fails
        }
    }
 
}