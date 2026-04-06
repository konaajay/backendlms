package com.lms.www.management.service.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lms.www.management.enums.CertificateEligibilityStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.CertificateProgress;
import com.lms.www.management.model.CertificateRule;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.repository.CertificateProgressRepository;
import com.lms.www.management.repository.CertificateRuleRepository;
import com.lms.www.management.repository.ExamAttemptRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.AttendanceSummaryService;
import com.lms.www.management.service.CertificateEligibilityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateEligibilityServiceImpl implements CertificateEligibilityService {

    private final CertificateRuleRepository certificateRuleRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CertificateProgressRepository certificateProgressRepository;
    private final StudentBatchRepository studentBatchRepository;
    private final AttendanceSummaryService attendanceSummaryService;

    @Override
    public boolean isEligible(Long userId,
                              TargetType targetType,
                              Long targetId) {

        // 🔵 STEP 0 – Check certificate progress first
        Optional<CertificateProgress> progressOpt =
                certificateProgressRepository
                        .findByUserIdAndTargetTypeAndTargetId(
                                userId, targetType, targetId);

        if (progressOpt.isPresent()) {
            return progressOpt.get().getEligibilityStatus()
                    == CertificateEligibilityStatus.ELIGIBLE;
        }

        // 1️⃣ Fetch active rule
        Optional<CertificateRule> ruleOpt =
        		certificateRuleRepository
        		.findByTargetTypeAndTargetId(
        		        targetType.name(),
        		        targetId);

        if (ruleOpt.isEmpty()) {
            return true;   // no rule → allow certificate
        }
        CertificateRule rule = ruleOpt.get();

        // 🟢 If rule disabled → allow certificate
        if (rule.getIsEnabled() != null && !rule.getIsEnabled()) {
            return true;
        }

        // 2️⃣ Check based on target type
        if (targetType == TargetType.EXAM) {
            return checkExamEligibility(userId, targetId, rule);
        }

        if (targetType == TargetType.COURSE) {
            return checkCourseEligibility(userId, targetId, rule);
        }

        return false;
    }

    // ================= EXAM RULE =================

    private boolean checkExamEligibility(Long userId,
                                         Long examId,
                                         CertificateRule rule) {

        Optional<ExamAttempt> attemptOpt =
                examAttemptRepository
                        .findTopByStudentIdAndExamIdAndStatusOrderByScoreDesc(
                                userId,
                                examId,
                                "EVALUATED");

        if (attemptOpt.isEmpty()) {
            return false;
        }

        ExamAttempt attempt = attemptOpt.get();

        if (attempt.getScore() == null) {
            return false;
        }

        // If score rule disabled → eligible
        if (rule.getScoreRequired() == null || !rule.getScoreRequired()) {
            return true;
        }

        if (rule.getRequiredScore() == null) {
            return true;
        }

        BigDecimal score = BigDecimal.valueOf(attempt.getScore());
        BigDecimal requiredScore = BigDecimal.valueOf(rule.getRequiredScore());

        return score.compareTo(requiredScore) >= 0;
    }

    // ================= COURSE RULE =================

    private boolean checkCourseEligibility(Long userId,
                                           Long courseId,
                                           CertificateRule rule) {

        Optional<StudentBatch> batchOpt =
                studentBatchRepository
                        .findFirstByStudentIdAndCourseIdAndStatus(
                                userId,
                                courseId,
                                "ACTIVE");

        if (batchOpt.isEmpty()) {
            batchOpt =
                    studentBatchRepository
                            .findFirstByStudentIdAndCourseIdAndStatus(
                                    userId,
                                    courseId,
                                    "COMPLETED");
        }

        if (batchOpt.isEmpty()) {
            return false;
        }

        Long batchId = batchOpt.get().getBatchId();

        // If attendance rule disabled → eligible
        if (rule.getAttendanceRequired() == null
                || !rule.getAttendanceRequired()) {
            return true;
        }

        if (rule.getMinAttendance() == null) {
            return true;
        }

        Map<String, Object> summary =
                attendanceSummaryService
                        .getStudentEligibilitySummary(
                                userId,
                                courseId,
                                batchId);

        Object percentageObj = summary.get("attendancePercentage");

        if (percentageObj == null) {
            return false;
        }

        double percentage = ((Number) percentageObj).doubleValue();

        BigDecimal attendance = BigDecimal.valueOf(percentage);
        BigDecimal minAttendance = BigDecimal.valueOf(rule.getMinAttendance());

        return attendance.compareTo(minAttendance) >= 0;
    }
}