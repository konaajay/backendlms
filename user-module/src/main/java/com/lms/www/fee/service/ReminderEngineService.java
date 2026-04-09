package com.lms.www.fee.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.fee.admin.entity.ReminderJob;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.penalty.repository.FeePenaltyRepository;
import com.lms.www.fee.penalty.service.PenaltyService;
import com.lms.www.fee.admin.repository.ReminderJobRepository;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.installment.repository.StudentInstallmentPlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReminderEngineService {

    private final ReminderJobRepository jobRepository;
    private final StudentInstallmentPlanRepository installmentRepository;
    private final StudentFeeAllocationRepository allocationRepository;
    private final FeePenaltyRepository penaltyRepository;
    private final PenaltyService penaltyService;
    private final PaymentGatewayService paymentGatewayService;
    private final EmailService emailService;

    @Value("${lms.base-url:http://localhost:5173}")
    private String lmsBaseUrl;

    @Transactional
    public void processJob(ReminderJob job) {
        log.info("Processing ReminderJob ID: {}, Installment ID: {}", job.getId(), job.getInstallmentId());

        try {
            job.setStatus(ReminderJob.JobStatus.PROCESSING);
            jobRepository.save(job);

            Long installmentId = java.util.Objects.requireNonNull(job.getInstallmentId(), "Installment ID must not be null");
            StudentInstallmentPlan inst = installmentRepository.findById(installmentId)
                    .orElseThrow(() -> new RuntimeException("Installment not found"));

            // Requirement 3: Check status must be PENDING (or PARTIAL/OVERDUE)
            if (inst.getStatus() == StudentInstallmentPlan.InstallmentStatus.PAID) {
                log.info("Installment {} already PAID. Marking job {} as SUCCESS.", inst.getId(), job.getId());
                job.setStatus(ReminderJob.JobStatus.SUCCESS);
                jobRepository.save(job);
                return;
            }

            // Requirement 4: One Active Link Policy
            if (inst.getPaymentSessionId() != null
                    && inst.getLinkExpiry() != null
                    && inst.getLinkExpiry().isAfter(LocalDateTime.now())) {
                log.info("Active link exists for installment {}. Skipping job {}.", inst.getId(), job.getId());
                job.setStatus(ReminderJob.JobStatus.SUCCESS); // Skip counts as successful processing
                jobRepository.save(job);
                return;
            }

            Long allocationId = java.util.Objects.requireNonNull(inst.getStudentFeeAllocationId(), "Allocation ID must not be null");
            StudentFeeAllocation allocation = allocationRepository.findById(allocationId)
                    .orElseThrow(() -> new RuntimeException("Allocation not found"));

            // Calculate Amount
            BigDecimal remainingAmount = inst.getInstallmentAmount()
                    .subtract(inst.getPaidAmount() != null ? inst.getPaidAmount() : BigDecimal.ZERO);

            BigDecimal finalAmount = remainingAmount;

            // Requirement 3: Add late fee if today > dueDate
            if (LocalDate.now().isAfter(inst.getDueDate())) {
                BigDecimal autoCalculatedPenalty = penaltyService.calculatePenalty(inst.getId());
                if (autoCalculatedPenalty != null && autoCalculatedPenalty.compareTo(BigDecimal.ZERO) > 0) {
                    finalAmount = finalAmount.add(autoCalculatedPenalty);
                    log.info("Auto-calculated penalty for installment {}: {}", inst.getId(), autoCalculatedPenalty);
                } else {
                    // Fallback to repository for manually applied penalties
                    BigDecimal manualPenalty = penaltyRepository.getTotalPenaltyByInstallmentId(inst.getId());
                    if (manualPenalty != null) {
                        finalAmount = finalAmount.add(manualPenalty);
                    }
                }
            }

            // Generate Payment Link
            String orderId = "REMEM_" + inst.getId() + "_" + System.currentTimeMillis();
            Map<String, String> orderResult = paymentGatewayService.createOrder(
                    finalAmount,
                    orderId,
                    String.valueOf(allocation.getUserId()),
                    allocation.getStudentName(),
                    allocation.getStudentEmail(),
                    null);

            String sessionId = orderResult.get("payment_session_id");

            // Update Installment with new link
            inst.setCashfreeOrderId(orderId);
            inst.setPaymentSessionId(sessionId);
            inst.setLinkCreatedAt(LocalDateTime.now());
            inst.setLinkExpiry(LocalDateTime.now().plusDays(2));
            installmentRepository.save(inst);

            // Send Notification
            String payUrl = lmsBaseUrl + "/pay/" + orderId;
            emailService.sendPaymentLinkEmail(
                    allocation.getStudentEmail(),
                    allocation.getStudentName() != null ? allocation.getStudentName() : "Student",
                    payUrl,
                    finalAmount,
                    inst.getDueDate());

            // Update Job Status
            job.setStatus(ReminderJob.JobStatus.SUCCESS);
            jobRepository.save(job);

            log.info("Successfully processed ReminderJob {}", job.getId());

        } catch (Exception e) {
            log.error("Failed to process ReminderJob {}: {}", job.getId(), e.getMessage());
            handleFailure(job);
        }
    }

    private void handleFailure(ReminderJob job) {
        job.setRetryCount(job.getRetryCount() + 1);
        if (job.getRetryCount() >= 3) {
            job.setStatus(ReminderJob.JobStatus.FAILED);
        } else {
            job.setStatus(ReminderJob.JobStatus.FAILED); // Set to FAILED so retry query picks it up
            // Requirement 6: Retry policy
            int delayMinutes = switch (job.getRetryCount()) {
                case 1 -> 5;
                case 2 -> 30;
                default -> 120;
            };
            job.setNextRetryTime(LocalDateTime.now().plusMinutes(delayMinutes));
        }
        jobRepository.save(job);
    }
}
