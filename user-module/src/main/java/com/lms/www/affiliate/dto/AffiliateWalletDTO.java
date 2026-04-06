package com.lms.www.affiliate.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class AffiliateWalletDTO {
    private BigDecimal availableBalance;
    private BigDecimal lockedBalance;
    private BigDecimal totalEarned;
    private BigDecimal totalPaid;
}
