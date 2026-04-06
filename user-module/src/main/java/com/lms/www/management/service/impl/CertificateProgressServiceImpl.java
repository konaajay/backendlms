package com.lms.www.management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lms.www.management.enums.CertificateEligibilityStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.CertificateProgress;
import com.lms.www.management.model.CertificateRule;
import com.lms.www.management.repository.CertificateProgressRepository;
import com.lms.www.management.repository.CertificateRuleRepository;
import com.lms.www.management.service.CertificateProgressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateProgressServiceImpl implements CertificateProgressService {

    private final CertificateProgressRepository progressRepository;
    private final CertificateRuleRepository ruleRepository;

    @Override
    public void updateExamProgress(Long userId,
            TargetType targetType,
            Long targetId,
            Double score) {

        processProgress(userId, targetType, targetId, score);
    }

    @Override
    public void manualUpdateProgress(Long userId,
            TargetType targetType,
            Long targetId,
            Double score) {

        processProgress(userId, targetType, targetId, score);
    }

    private void processProgress(Long userId,
            TargetType targetType,
            Long targetId,
            Double score) {

        // 1️⃣ Fetch rule
        Optional<CertificateRule> ruleOpt = ruleRepository.findByTargetTypeAndTargetIdAndIsEnabledTrue(
                targetType.name(),
                targetId);

        if (ruleOpt.isEmpty()) {
            return; // No rule defined → no progress update
        }

        CertificateRule rule = ruleOpt.get();

        // 2️⃣ Determine eligibility
        CertificateEligibilityStatus status = CertificateEligibilityStatus.NOT_ELIGIBLE;

        // 🟢 If rule is disabled, validations are skipped and user is eligible by default
        if (rule.getIsEnabled() != null && !rule.getIsEnabled()) {
            status = CertificateEligibilityStatus.ELIGIBLE;
        } else {
            // 🧠 EXAM or COURSE rule with a required score
            if (rule.getRequiredScore() != null && score != null) {

                BigDecimal studentScore = BigDecimal.valueOf(score);
                BigDecimal requiredScore = BigDecimal.valueOf(rule.getRequiredScore());

                if (studentScore.compareTo(requiredScore) >= 0) {
                    status = CertificateEligibilityStatus.ELIGIBLE;
                }
            }
            // 🧠 COURSE rule (no score required)
            else if (rule.getRequiredScore() == null
                    && targetType == TargetType.COURSE) {

                status = CertificateEligibilityStatus.ELIGIBLE;
            }
        }

        // 3️⃣ Fetch existing progress or create new
        CertificateProgress progress = progressRepository
                .findByUserIdAndTargetTypeAndTargetId(
                        userId, targetType, targetId)
                .orElse(
                        CertificateProgress.builder()
                                .userId(userId)
                                .targetType(targetType)
                                .targetId(targetId)
                                .build());

        // 4️⃣ Update fields
        progress.setScore(score);
        progress.setEligibilityStatus(status);
        progress.setUpdatedAt(LocalDateTime.now());

        progressRepository.save(progress);
    }
}