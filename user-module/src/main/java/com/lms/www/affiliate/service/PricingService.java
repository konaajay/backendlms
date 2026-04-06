package com.lms.www.affiliate.service;

import com.lms.www.affiliate.dto.PricingResponseDTO;
import java.math.BigDecimal;

public interface PricingService {

    PricingResponseDTO calculatePrice(Long courseId, String referralCode, BigDecimal originalPrice);
}
