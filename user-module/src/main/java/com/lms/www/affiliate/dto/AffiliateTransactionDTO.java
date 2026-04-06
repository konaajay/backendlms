package com.lms.www.affiliate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;



import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AffiliateTransactionDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
}
