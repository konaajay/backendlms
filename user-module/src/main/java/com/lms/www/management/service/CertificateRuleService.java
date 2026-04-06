package com.lms.www.management.service;

import com.lms.www.management.model.CertificateRule;

public interface CertificateRuleService {

    CertificateRule saveRule(CertificateRule rule);

    CertificateRule toggleRuleEnabled(Long id, boolean isEnabled);

    CertificateRule getRuleByTarget(String targetType, Long targetId);
}
