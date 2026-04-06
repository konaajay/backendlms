package com.lms.www.marketing.service.email;

import com.lms.www.marketing.model.EmailRecipient;
import com.lms.www.marketing.dto.EmailProviderResult;
import com.lms.www.marketing.service.ResendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailProvider implements EmailProvider {

    @Autowired
    private ResendEmailService resendEmailService;

    @Override
    public EmailProviderResult send(EmailRecipient recipient) {
        return resendEmailService.sendEmailViaResend(recipient);
    }
}
