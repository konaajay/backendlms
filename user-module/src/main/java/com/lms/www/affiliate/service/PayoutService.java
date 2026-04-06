package com.lms.www.affiliate.service;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliatePayout;
import java.math.BigDecimal;
import java.util.List;

public interface PayoutService {
    List<AffiliatePayout> getPayouts(Affiliate affiliate);

    List<AffiliatePayout> getPayoutsByAffiliateId(Long affiliateId);

    List<AffiliatePayout> getPayoutsByUserId(Long userId);

    AffiliatePayout requestPayout(Long userId, BigDecimal amount);

    void approvePayout(Long payoutId, String processedBy);

    void rejectPayout(Long payoutId, String reason);
}
