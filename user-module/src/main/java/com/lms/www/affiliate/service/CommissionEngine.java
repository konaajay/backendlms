package com.lms.www.affiliate.service;

import java.math.BigDecimal;

import com.lms.www.affiliate.entity.Affiliate;

public interface CommissionEngine {
    BigDecimal calculateCommission(BigDecimal orderAmount, Affiliate affiliate, Long courseId);
    void creditCommissionToAffiliateWallet(BigDecimal orderAmount, Affiliate affiliate, Long courseId, Long saleId);
}