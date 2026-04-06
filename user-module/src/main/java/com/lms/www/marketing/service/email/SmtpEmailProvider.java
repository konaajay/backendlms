package com.lms.www.marketing.service.email;

import com.lms.www.marketing.model.EmailRecipient;
import com.lms.www.marketing.dto.EmailProviderResult;
import com.lms.www.marketing.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailProvider implements EmailProvider {

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public EmailProviderResult send(EmailRecipient recipient) {
        return emailSenderService.sendCampaignEmail(recipient);
    }
}
