package com.lms.www.management.service;

import com.lms.www.management.enums.TargetType;

public interface CertificateEligibilityService {

    boolean isEligible(
            Long userId,
            TargetType targetType,
            Long targetId
    );
}