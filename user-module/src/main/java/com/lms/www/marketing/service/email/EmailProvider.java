package com.lms.www.marketing.service.email;

import com.lms.www.marketing.model.EmailRecipient;
import com.lms.www.marketing.dto.EmailProviderResult;

public interface EmailProvider {
    /**
     * Sends an email to a recipient and returns a typed result.
     * No more silent failures or "Status Magic". ✨🛡️✍️
     */
    EmailProviderResult send(EmailRecipient recipient);
}
