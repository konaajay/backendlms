package com.lms.www.affiliate.service;

import java.math.BigDecimal;
import com.lms.www.affiliate.entity.Affiliate;

public interface CommissionService {

    BigDecimal calculateCommission(BigDecimal orderAmount, Affiliate affiliate, Long courseId);

}