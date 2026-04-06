package com.lms.www.affiliate.service.impl;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.service.CommissionEngine;
import com.lms.www.affiliate.service.CommissionService;
import com.lms.www.affiliate.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final CommissionEngine commissionEngine;
    private final WalletService walletService;

    @Override
    public BigDecimal calculateCommission(BigDecimal orderAmount, Affiliate affiliate, Long courseId) {
        return commissionEngine.calculateCommission(orderAmount, affiliate, courseId);
    }

    /**
     * Orchestrates commission calculation and wallet crediting.
     * This is the production-ready entry point for manual or synchronous credits.
     * NOTE: Enrollment flow uses SCHEDULED job, so this is for specialized use-cases.
     */
    public void processCommission(BigDecimal orderAmount, Affiliate affiliate, Long courseId, Long saleId) {

        BigDecimal commission = commissionEngine.calculateCommission(orderAmount, affiliate, courseId);

        if (commission.compareTo(BigDecimal.ZERO) > 0) {
            walletService.creditCommission(
                    affiliate.getId(),
                    commission,
                    saleId,
                    "Commission for sale " + (saleId != null ? saleId : "manual")
            );
        }
    }
}