package com.lms.www.management.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lms.www.management.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl
        implements EmailNotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendAttendanceAlert(
            Long studentId,
            String flagType,
            int attendancePercent) {

        // ⚠ TEMP: static email (NO user module dependency)
        String toEmail = "student.alert@test.com";

        String subject = "Attendance Alert";
        String body = "Student ID: " + studentId + "\n" +
                "Your attendance is at risk.\n\n" +
                "Attendance Percentage: " + attendancePercent + "%\n" +
                "Status: AT_RISK\n\n" +
                "Please attend upcoming sessions.";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("lmssender4@gmail.com"); // ✅ ADD THIS
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            System.out.println(
                    "📧 Attendance alert email sent for studentId="
                            + studentId);

        } catch (Exception e) {
            System.err.println(
                    "❌ Failed to send attendance alert email: "
                            + e.getMessage());
        }

    }

    public void sendManualAttendanceAlert(
            Long studentId,
            String flagType) {
        // not used now
    }

    @Override
    public void sendExamResultNotification(
            Long studentId,
            String examTitle,
            Double score,
            Boolean passed) {
        String toEmail = "student.exam@test.com"; // Placeholder
        String subject = "Exam Result: " + examTitle;
        String status = passed ? "PASSED" : "FAILED";

        String body = "Student ID: " + studentId + "\n" +
                "Exam: " + examTitle + "\n" +
                "Score: " + score + "\n" +
                "Status: " + status + "\n\n" +
                "Please log in to view detailed results.";

        sendEmail(toEmail, subject, body);
        System.out.println("📧 Exam result email sent for studentId=" + studentId);
    }

    @Override
    public void sendConsecutiveAbsenceAlert(
            Long studentId,
            int consecutiveDays) {
        String toEmail = "student.absence@test.com"; // Placeholder
        String subject = "Recurring Absence Alert";

        String body = "Student ID: " + studentId + "\n" +
                "Warning: You have been absent for " + consecutiveDays + " consecutive days.\n" +
                "Please attend upcoming sessions to avoid penalties.\n\n" +
                "Contact administration if this is an error.";

        sendEmail(toEmail, subject, body);
        System.out.println("📧 Consecutive absence email sent for studentId=" + studentId);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("lmssender4@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
 