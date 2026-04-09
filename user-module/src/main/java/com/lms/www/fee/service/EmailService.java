package com.lms.www.fee.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service("feeEmailService")
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Value("${spring.mail.username}")
    private String senderEmail;

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("==============================================");
        System.out.println("📧 EMAIL SERVICE INITIALIZED");
        System.out.println("SENDER: " + senderEmail);
        System.out.println("==============================================");
    }

    // ─────────────────────────────────────────────────────────────
    // ENROLLMENT INVOICE EMAIL (sent right after allocation is created)
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendEnrollmentInvoiceEmail(StudentFeeAllocation allocation, List<StudentInstallmentPlan> installments,
            BigDecimal latePenaltyPercentage) {
        try {
            log.info("Attempting to send enrollment email from {} to {}", senderEmail, allocation.getStudentEmail());
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true);
            h.setFrom(senderEmail);
            h.setTo(allocation.getStudentEmail());
            h.setSubject("📄 Fee Invoice – " + allocation.getCourseName() + " | " + allocation.getBatchName());
            h.setText(java.util.Objects
                    .requireNonNull(buildEnrollmentHtml(allocation, installments, latePenaltyPercentage)), true);
            log.info("Sending enrollment email | allocationId={} | studentEmail={}",
                    allocation.getId(),
                    allocation.getStudentEmail());
            mailSender.send(msg);
            System.out.println("✅ ENROLLMENT EMAIL SENT TO: " + allocation.getStudentEmail());
            log.info("Enrollment invoice sent successfully to {}", allocation.getStudentEmail());
        } catch (Exception e) {
            System.err.println("❌ FAILED TO SEND ENROLLMENT EMAIL TO: " + allocation.getStudentEmail());
            System.err.println("REASON: " + e.getMessage());
            log.error("Failed to send enrollment invoice to {}: {}", allocation.getStudentEmail(), e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PAYMENT SUCCESS EMAIL
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendPaymentSuccessEmail(String toEmail, String studentName, String receiptNumber,
            BigDecimal amount, String currency, byte[] receiptPdf) {
        if (toEmail == null || toEmail.isBlank())
            return;
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(senderEmail);
            h.setTo(toEmail);
            h.setSubject("✅ Payment Receipt – " + receiptNumber);
            h.setText(java.util.Objects.requireNonNull(buildPaymentHtml(studentName, receiptNumber, amount, currency)),
                    true);
            if (receiptPdf != null && receiptPdf.length > 0) {
                h.addAttachment("Receipt_" + receiptNumber + ".pdf",
                        new org.springframework.core.io.ByteArrayResource(receiptPdf),
                        "application/pdf");
            }
            mailSender.send(msg);
            log.info("Payment receipt sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send payment receipt to {}: {}", toEmail, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // OVERDUE / REMINDER
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendOverdueWarningEmail(String toEmail, String studentName,
            BigDecimal amount, BigDecimal penalty, int daysOverdue) {
        if (toEmail == null || toEmail.isBlank())
            return;
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, false, "UTF-8");
            h.setFrom(senderEmail);
            h.setTo(toEmail);
            h.setSubject("⚠️ Overdue Fee Alert – Action Required");
            h.setText(java.util.Objects.requireNonNull(buildOverdueHtml(studentName, amount, penalty, daysOverdue)),
                    true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("Failed to send overdue email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendDueReminderEmail(String toEmail, String studentName,
            BigDecimal amount, LocalDate dueDate) {
        if (toEmail == null || toEmail.isBlank())
            return;
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, false, "UTF-8");
            h.setFrom(senderEmail);
            h.setTo(toEmail);
            h.setSubject("🔔 Upcoming Fee Due – " + dueDate.format(DATE_FMT));
            h.setText(java.util.Objects.requireNonNull(buildReminderHtml(studentName, amount, dueDate)), true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("Failed to send reminder to {}: {}", toEmail, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PAYMENT LINK EMAIL
    // ─────────────────────────────────────────────────────────────
    @Async
    public void sendPaymentLinkEmail(String email, String studentName, String paymentLink, BigDecimal amount,
            LocalDate dueDate) {
        try {
            log.info("Attempting to send payment link email from {} to {}", senderEmail, email);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true);
            h.setFrom(senderEmail);
            h.setTo(email);
            h.setSubject("LMS Fee Payment Link");

            String body = "Hello " + studentName + ",\n\n"
                    + "Your installment payment is due.\n"
                    + "Amount: ₹" + amount + "\n"
                    + "Due Date: " + (dueDate != null ? dueDate.format(DATE_FMT) : "Immediate (Early Payment)") + "\n\n"
                    + "Pay using the link below:\n"
                    + paymentLink + "\n\n"
                    + "Thank you,\nLMS Team";

            log.info("Sending payment link email to: {}", email);

            // Sending as plain text as requested by the user script
            h.setText(body, false);
            mailSender.send(msg);
            System.out.println("✅ PAYMENT LINK EMAIL SENT TO: " + email);
            log.info("Payment link email sent successfully to {}", email);
        } catch (Exception e) {
            System.err.println("❌ FAILED TO SEND PAYMENT LINK EMAIL TO: " + email);
            System.err.println("REASON: " + e.getMessage());
            log.error("Failed to send payment link email to {}: {}", email, e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HTML BUILDERS
    // ─────────────────────────────────────────────────────────────
    private String buildEnrollmentHtml(StudentFeeAllocation a,
            List<StudentInstallmentPlan> installments,
            BigDecimal latePenaltyPercentage) {
        StringBuilder rows = new StringBuilder();
        for (StudentInstallmentPlan inst : installments) {
            rows.append("<tr>")
                    .append("<td style='padding:8px 12px;border-bottom:1px solid #f0f0f0;font-size:13px;'>Installment ")
                    .append(inst.getInstallmentNumber()).append("</td>")
                    .append("<td style='padding:8px 12px;border-bottom:1px solid #f0f0f0;font-size:13px;'>")
                    .append(inst.getDueDate() != null ? inst.getDueDate().format(DATE_FMT) : "—").append("</td>")
                    .append("<td style='padding:8px 12px;border-bottom:1px solid #f0f0f0;font-weight:600;font-size:13px;'>₹")
                    .append(inst.getInstallmentAmount()).append("</td>")
                    .append("<td style='padding:8px 12px;border-bottom:1px solid #f0f0f0;'><span style='background:#fef3c7;color:#92400e;padding:2px 8px;border-radius:20px;font-size:11px;'>PENDING</span></td>")
                    .append("</tr>");
        }

        String penaltySection = "";
        if (latePenaltyPercentage != null && latePenaltyPercentage.compareTo(BigDecimal.ZERO) > 0) {
            penaltySection = "<div style='margin-top:24px;background:#fff7ed;border:1px solid #fed7aa;border-radius:10px;padding:16px;'>"
                    +
                    "<h3 style='color:#c2410c;margin:0 0 8px;font-size:14px;'>⚠️ Late Payment Penalty Policy</h3>" +
                    "<p style='margin:0;color:#78350f;font-size:13px;'>A late fee of <strong>" + latePenaltyPercentage
                    + "%</strong> per month will be charged on overdue installments. " +
                    "Please make payments on time to avoid penalties.</p>" +
                    "</div>";
        }

        return "<div style='font-family:Inter,Arial,sans-serif; width:100%; max-width:640px; margin:0 auto; background:#f8fafc; padding:10px; box-sizing:border-box;'>"
                +
                "<div style='background:#fff; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08);'>"
                +
                // Header
                "<div style='background:linear-gradient(135deg,#4f46e5 0%,#7c3aed 100%); padding:24px 20px; text-align:center;'>"
                +
                "<h1 style='color:#fff; margin:0; font-size:22px; font-weight:700;'>Fee Invoice</h1>" +
                "<p style='color:rgba(255,255,255,0.8); margin:4px 0 0; font-size:13px;'>Class X 360 – Learning Management System</p>"
                +
                "</div>" +
                // Student & Invoice Info Table
                "<div style='padding:20px 20px 0;'>" +
                "<table width='100%' style='border-collapse:collapse;'>" +
                "<tr>" +
                "<td style='vertical-align:top; width:50%; padding-bottom:15px;'>" +
                "<p style='margin:0;color:#64748b;font-size:11px;text-transform:uppercase;letter-spacing:1px;'>Bill To</p>"
                +
                "<p style='margin:4px 0 0;font-weight:700;font-size:14px;color:#0f172a;'>" + safe(a.getStudentName())
                + "</p>" +
                "<p style='margin:2px 0 0;color:#64748b;font-size:12px; word-break:break-all;'>"
                + safe(a.getStudentEmail())
                + "</p>" +
                "</td>" +
                "<td style='vertical-align:top; width:50%; text-align:right; padding-bottom:15px;'>" +
                "<p style='margin:0;color:#64748b;font-size:11px;text-transform:uppercase;letter-spacing:1px;'>Invoice</p>"
                +
                "<p style='margin:4px 0 0;font-weight:700;font-size:14px;color:#0f172a;'>INV-" + a.getId() + "</p>" +
                "<p style='margin:2px 0 0;color:#64748b;font-size:12px;'>" + LocalDate.now().format(DATE_FMT) + "</p>"
                +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</div>" +
                // Course / Batch - Removed display: flex for better mobile support
                "<div style='padding:10px 20px;'>" +
                "<div style='background:#f8fafc; border-radius:10px; padding:12px;'>" +
                "<table width='100%' style='border-collapse:collapse;'>" +
                "<tr>" +
                "<td style='width:50%;'>" +
                "<p style='margin:0;color:#64748b;font-size:11px;'>Course</p><p style='margin:4px 0 0;font-weight:600;font-size:13px;color:#0f172a;'>"
                + safe(a.getCourseName()) + "</p>" +
                "</td>" +
                "<td style='width:50%;'>" +
                "<p style='margin:0;color:#64748b;font-size:11px;'>Batch</p><p style='margin:4px 0 0;font-weight:600;font-size:13px;color:#0f172a;'>"
                + safe(a.getBatchName()) + "</p>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</div>" +
                "</div>" +
                // Fee Summary
                "<div style='padding:0 20px;'>" +
                "<table width='100%' style='border-collapse:collapse;'>" +
                "<tr style='background:#f8fafc;'>" +
                "<td style='padding:8px 12px;font-size:12px;color:#64748b;'>Original Amount</td>" +
                "<td style='padding:8px 12px;text-align:right;font-size:12px;'>₹" + safe(a.getOriginalAmount())
                + "</td></tr>" +
                (a.getAdmissionFeeAmount() != null && a.getAdmissionFeeAmount().compareTo(BigDecimal.ZERO) > 0
                        ? "<tr><td style='padding:8px 12px;font-size:12px;color:#64748b;'>Admission Fee</td>" +
                                "<td style='padding:8px 12px;text-align:right;font-size:12px;'>₹"
                                + safe(a.getAdmissionFeeAmount())
                                + "</td></tr>"
                        : "")
                +
                "<tr><td style='padding:8px 12px;font-size:12px;color:#64748b;'>Discount</td>" +
                "<td style='padding:8px 12px;text-align:right;color:#16a34a;font-size:12px;'>- ₹"
                + safe(a.getTotalDiscount())
                + "</td></tr>" +
                "<tr style='background:#f8fafc;'><td style='padding:8px 12px;font-size:12px;color:#64748b;'>GST ("
                + safe(a.getGstRate()) + "%)</td>" +
                "<td style='padding:8px 12px;text-align:right;font-size:12px;'>₹" + safe(a.getGstAmount())
                + "</td></tr>"
                +
                "<tr style='border-top:2px solid #e2e8f0;'>" +
                "<td style='padding:12px;font-weight:700;font-size:14px;color:#0f172a;'>Total Payable</td>" +
                "<td style='padding:12px;text-align:right;font-weight:700;font-size:16px;color:#4f46e5;'>₹"
                + safe(a.getPayableAmount()) + "</td></tr>" +
                "</table></div>" +
                // Installment table - Optimized for mobile
                "<div style='padding:20px 20px;'>" +
                "<h3 style='margin:0 0 12px;font-size:14px;color:#0f172a;'>📅 Installment Schedule</h3>" +
                // Use div with overflow-x for table if it gets too wide, though we simplified
                // it
                "<div style='width:100%; overflow-x:auto;'>" +
                "<table width='100%' style='min-width:320px; border-collapse:collapse; background:#fff; border-radius:10px; overflow:hidden; border:1px solid #e2e8f0;'>"
                +
                "<thead><tr style='background:#f8fafc;'>" +
                "<th style='padding:8px 12px;text-align:left;font-size:10px;color:#64748b;text-transform:uppercase;'>Plan</th>"
                +
                "<th style='padding:8px 12px;text-align:left;font-size:10px;color:#64748b;text-transform:uppercase;'>Due</th>"
                +
                "<th style='padding:8px 12px;text-align:left;font-size:10px;color:#64748b;text-transform:uppercase;'>Amt</th>"
                +
                "<th style='padding:8px 12px;text-align:left;font-size:10px;color:#64748b;text-transform:uppercase;'>St</th>"
                +
                "</tr></thead><tbody>" + rows + "</tbody></table>" +
                "</div>" +
                penaltySection +
                "</div>" +
                // Footer
                "<div style='padding:15px 20px;background:#f8fafc;border-top:1px solid #e2e8f0;text-align:center;'>" +
                "<p style='margin:0;font-size:11px;color:#94a3b8;'>This is a system-generated invoice. For queries contact your institution admin.</p>"
                +
                "</div></div></div>";
    }

    private String buildPaymentHtml(String name, String receipt, BigDecimal amount, String currency) {
        return "<div style='font-family:Inter,Arial,sans-serif;max-width:540px;margin:0 auto;background:#f8fafc;padding:20px;'>"
                +
                "<div style='background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);'>"
                +
                "<div style='background:linear-gradient(135deg,#10b981 0%,#059669 100%);padding:32px;text-align:center;'>"
                +
                "<div style='font-size:48px;'>✅</div>" +
                "<h1 style='color:#fff;margin:8px 0 0;font-size:22px;'>Payment Successful</h1>" +
                "</div>" +
                "<div style='padding:28px 32px;text-align:center;'>" +
                "<p style='color:#64748b;margin:0;'>Hi <strong>" + safe(name)
                + "</strong>, your payment has been received.</p>" +
                "<div style='margin:24px 0;background:#f0fdf4;border-radius:12px;padding:20px;'>" +
                "<p style='margin:0;color:#064e3b;font-size:32px;font-weight:700;'>" + currency + " ₹" + safe(amount)
                + "</p>" +
                "<p style='margin:4px 0 0;color:#6b7280;font-size:13px;'>Receipt No: " + safe(receipt) + "</p>" +
                "</div>" +
                "<p style='color:#94a3b8;font-size:12px;'>Thank you for your payment. Please keep this for your records.</p>"
                +
                "</div></div></div>";
    }

    private String buildOverdueHtml(String name, BigDecimal amount, BigDecimal penalty, int days) {
        return "<div style='font-family:Inter,Arial,sans-serif;max-width:540px;margin:0 auto;padding:20px;'>" +
                "<div style='background:#fff;border-radius:16px;border:2px solid #fca5a5;padding:28px;'>" +
                "<h2 style='color:#dc2626;margin:0 0 12px;'>⚠️ Overdue Fee Alert</h2>" +
                "<p>Dear <strong>" + safe(name) + "</strong>,</p>" +
                "<p>Your fee payment of <strong>₹" + safe(amount) + "</strong> is <strong>" + days
                + " days overdue</strong>.</p>" +
                "<p>A penalty of <strong>₹" + safe(penalty) + "</strong> has been applied.</p>" +
                "<p>Please make the payment immediately to avoid further penalties.</p>" +
                "</div></div>";
    }

    private String buildReminderHtml(String name, BigDecimal amount, LocalDate dueDate) {
        return "<div style='font-family:Inter,Arial,sans-serif;max-width:540px;margin:0 auto;padding:20px;'>" +
                "<div style='background:#fff;border-radius:16px;border:2px solid #fde68a;padding:28px;'>" +
                "<h2 style='color:#d97706;margin:0 0 12px;'>🔔 Fee Due Reminder</h2>" +
                "<p>Dear <strong>" + safe(name) + "</strong>,</p>" +
                "<p>Your fee payment of <strong>₹" + safe(amount) + "</strong> is due on <strong>"
                + dueDate.format(DATE_FMT) + "</strong>.</p>" +
                "<p>Please ensure timely payment to avoid late fees.</p>" +
                "</div></div>";
    }

    private String safe(Object o) {
        return o == null ? "—" : o.toString();
    }
}
