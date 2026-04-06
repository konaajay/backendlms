package com.lms.www.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.model.CertificateRule;
import com.lms.www.management.repository.CertificateRuleRepository;
import com.lms.www.management.service.CertificateRuleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateRuleServiceImpl implements CertificateRuleService {

    private final CertificateRuleRepository certificateRuleRepository;

    @Override
    @Transactional
    public CertificateRule saveRule(CertificateRule rule) {

        CertificateRule existing = certificateRuleRepository.findById(rule.getId())
                .orElseThrow(() -> new RuntimeException("Rule not found"));

        if (rule.getAttendanceRequired() != null)
            existing.setAttendanceRequired(rule.getAttendanceRequired());

        if (rule.getMinAttendance() != null)
            existing.setMinAttendance(rule.getMinAttendance());

        if (rule.getScoreRequired() != null)
            existing.setScoreRequired(rule.getScoreRequired());

        if (rule.getRequiredScore() != null)
            existing.setRequiredScore(rule.getRequiredScore());

        if (rule.getIsEnabled() != null)
            existing.setIsEnabled(rule.getIsEnabled());

        return certificateRuleRepository.save(existing);
    }

    @Override
    @Transactional
    public CertificateRule toggleRuleEnabled(Long id, boolean isEnabled) {
        CertificateRule rule = certificateRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate Rule not found with id: " + id));

        rule.setIsEnabled(isEnabled);
        return certificateRuleRepository.save(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateRule getRuleByTarget(String targetType, Long targetId) {
        return certificateRuleRepository.findByTargetTypeAndTargetId(targetType, targetId)
                .orElseThrow(() -> new RuntimeException(
                        "Certificate Rule not found for target: " + targetType + " ID: " + targetId));
    }
    
}
