package com.lms.www.fee.util;

import com.lms.www.fee.config.FeeModuleConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderIdGenerator {

    private final FeeModuleConfig config;

    public String generateNormal(Long allocationId) {
        return config.getOrderPrefix().getNormal() + System.currentTimeMillis() + "_" + allocationId;
    }

    public String generateEarly(Long studentId) {
        return config.getOrderPrefix().getEarly() + studentId + "_" + System.currentTimeMillis();
    }

    public String generateManual() {
        return config.getOrderPrefix().getManual() + System.currentTimeMillis();
    }
}
