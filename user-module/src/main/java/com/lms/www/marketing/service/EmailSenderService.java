package com.lms.www.marketing.service;

import com.lms.www.marketing.dto.EmailProviderResult;
import com.lms.www.marketing.model.EmailRecipient;
import com.lms.www.marketing.model.EmailStatus;
import com.lms.www.marketing.model.EmailCampaign;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@campus.com}")
    private String defaultFromEmail;

    public EmailProviderResult sendCampaignEmail(EmailRecipient recipient) {
        String toEmail = recipient.getEmail();
        EmailCampaign campaign = recipient.getCampaign();
        String subject = (campaign != null) ? campaign.getSubject() : "No Subject";
        String content = (campaign != null) ? campaign.getContent() : "";
        String fromName = (campaign != null && campaign.getFromName() != null) ? campaign.getFromName() : "Marketing";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(defaultFromEmail, fromName));
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content != null ? content : "", true);

            if (campaign != null && campaign.getReplyTo() != null) {
                helper.setReplyTo(campaign.getReplyTo());
            }

            mailSender.send(message);

            return EmailProviderResult.builder().status(EmailStatus.SENT).success(true).build();

        } catch (Exception e) {
            log.error("SMTP_API_FAIL | To: {} | Error: {}", toEmail, e.getMessage());
            return EmailProviderResult.builder().status(EmailStatus.FAILED).success(false).error(e.getMessage()).build();
        }
    }
}
