package com.lms.www.marketing.service.email;

import com.lms.www.marketing.model.EmailRecipient;
import com.lms.www.marketing.model.EmailStatus;
import com.lms.www.marketing.dto.EmailProviderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppEmailProvider implements EmailProvider {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppEmailProvider.class);

    @Override
    public EmailProviderResult send(EmailRecipient recipient) {
        log.warn("WhatsApp messaging is currently disabled or not implemented.");
        return EmailProviderResult.builder()
                .success(false)
                .status(EmailStatus.FAILED)
                .error("WhatsApp provider service not available")
                .build();
    }
}
