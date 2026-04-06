package com.lms.www.marketing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("marketingEmailService")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send a single email (User requirement)
     */
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    /**
     * Send bulk emails to a list of recipients (Old method kept for compatibility if needed)
     */
    public BulkEmailResult sendBulkEmail(List<String> recipients, String subject, String body) {
        int success = 0, failed = 0;
        for (String email : recipients) {
            try {
                send(email, subject, body);
                success++;
            } catch (Exception e) {
                failed++;
            }
        }
        return new BulkEmailResult(recipients.size(), success, failed);
    }

    public record BulkEmailResult(int total, int success, int failed) {
    }
}
