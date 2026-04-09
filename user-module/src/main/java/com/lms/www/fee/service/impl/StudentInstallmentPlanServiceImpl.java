package com.lms.www.fee.service.impl;

import com.lms.www.fee.dto.InstallmentPlanRequest;
import com.lms.www.fee.dto.InstallmentPlanResponse;
import com.lms.www.fee.dto.InstallmentUpdateRequest;
import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.installment.repository.StudentInstallmentPlanRepository;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.ledger.service.AuditLogService;
import com.lms.www.fee.service.PaymentGatewayService;
import com.lms.www.fee.service.StudentInstallmentPlanService;
import com.lms.www.fee.payment.entity.StudentFeePayment;
import com.lms.www.fee.payment.entity.PaymentMode;
import com.lms.www.fee.payment.entity.PaymentStatus;
import com.lms.www.fee.payment.repository.StudentFeePaymentRepository;
import com.lms.www.fee.service.EmailService;
import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.security.UserContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentInstallmentPlanServiceImpl implements StudentInstallmentPlanService {

    private final StudentInstallmentPlanRepository repo;
    private final StudentFeeAllocationRepository allocationRepo;
    private final AuditLogService auditLogService;
    private final PaymentGatewayService paymentGatewayService;
    private final StudentFeePaymentRepository paymentRepository;
    private final UserContext userContext;

    @org.springframework.beans.factory.annotation.Autowired
    private EmailService emailService;

    @Override
    public List<InstallmentPlanResponse> createInstallments(Long allocationId, List<InstallmentPlanRequest> requests) {
        if (repo.existsByStudentFeeAllocationId(allocationId)) {
            throw new IllegalStateException("Installments already exist for this allocation.");
        }

        StudentFeeAllocation allocation = allocationRepo.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + allocationId));

        List<StudentInstallmentPlan> toSave = requests.stream().map(req -> {
            StudentInstallmentPlan plan = FeeMapper.toEntity(req);
            plan.setStudentFeeAllocationId(allocationId);
            plan.setUserId(allocation.getUserId());
            plan.setPaidAmount(BigDecimal.ZERO);
            plan.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING);
            return plan;
        }).collect(Collectors.toList());

        List<StudentInstallmentPlan> saved = repo.saveAll(toSave);
        auditLogService.log("FEE", "INSTALLMENT", allocationId, FeeAuditLog.Action.CREATE, null, "BULK_CREATE");

        return saved.stream().map(FeeMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<InstallmentPlanResponse> resetInstallments(Long allocationId, List<InstallmentPlanRequest> requests) {
        if (!allocationRepo.existsById(allocationId)) {
            throw new ResourceNotFoundException("Allocation not found: " + allocationId);
        }
        List<StudentInstallmentPlan> existing = repo.findByStudentFeeAllocationId(allocationId);

        List<StudentInstallmentPlan> toDelete = existing.stream()
                .filter(p -> p.getStatus() != StudentInstallmentPlan.InstallmentStatus.PAID)
                .collect(Collectors.toList());
        repo.deleteAll(toDelete);
        repo.flush();

        return createInstallments(allocationId, requests);
    }

    @Override
    public List<InstallmentPlanResponse> getByAllocation(Long allocationId) {
        return repo.findByStudentFeeAllocationId(allocationId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InstallmentPlanResponse> getByAllocationSecure(Long allocationId) {
        StudentFeeAllocation allocation = allocationRepo.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + allocationId));
        if (!userContext.isAdmin() && !allocation.getUserId().equals(userContext.getCurrentUserId())) {
            throw new AccessDeniedException("Access denied to this allocation");
        }
        return getByAllocation(allocationId);
    }

    @Override
    public List<InstallmentPlanResponse> getOverdueInstallments() {
        return repo.findOverdueInstallments(LocalDate.now(), StudentInstallmentPlan.InstallmentStatus.PAID).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InstallmentPlanResponse extendDueDate(Long id, InstallmentUpdateRequest request) {
        StudentInstallmentPlan installment = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));

        if (!userContext.isAdmin()) {
            throw new AccessDeniedException("Only admins can extend due dates");
        }

        installment.setDueDate(request.getNewDueDate());
        if (installment.getStatus() == StudentInstallmentPlan.InstallmentStatus.OVERDUE) {
            installment.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING);
        }

        return FeeMapper.toResponse(repo.save(installment));
    }

    @Override
    public List<InstallmentPlanResponse> getInstallmentsByBatchId(Long batchId) {
        return repo.findByBatchId(batchId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteInstallment(Long id) {
        if (!userContext.isAdmin()) {
            throw new AccessDeniedException("Only admins can delete installments");
        }
        StudentInstallmentPlan plan = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));
        
        if (plan.getStatus() == StudentInstallmentPlan.InstallmentStatus.PAID) {
            throw new IllegalStateException("Cannot delete a PAID installment");
        }
        
        repo.deleteById(id);
    }

    @Override
    public StudentInstallmentPlan generatePaymentLink(Long id) {
        StudentInstallmentPlan installment = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));

        StudentFeeAllocation allocation = allocationRepo.findById(installment.getStudentFeeAllocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + installment.getStudentFeeAllocationId()));
        
        String orderId = "INST_" + id + "_" + System.currentTimeMillis();
        BigDecimal amount = installment.getInstallmentAmount().subtract(installment.getPaidAmount());

        Map<String, String> orderResult = paymentGatewayService.createOrder(
                amount,
                orderId,
                String.valueOf(allocation.getUserId()),
                allocation.getStudentName(),
                allocation.getStudentEmail(),
                null
        );

        installment.setCashfreeOrderId(orderId);
        installment.setPaymentSessionId(orderResult.get("payment_session_id"));
        installment.setLinkCreatedAt(LocalDateTime.now());
        installment.setLinkExpiry(LocalDateTime.now().plusDays(1));

        StudentInstallmentPlan saved = repo.save(installment);

        // CREATE PENDING RECORD IN HISTORY
        StudentFeePayment p = new StudentFeePayment();
        p.setStudentFeeAllocationId(allocation.getId());
        p.setStudentInstallmentPlanId(id);
        p.setPaidAmount(amount);
        p.setPaymentDate(LocalDateTime.now());
        p.setPaymentMode(PaymentMode.ONLINE);
        p.setPaymentStatus(PaymentStatus.PENDING);
        p.setTransactionReference(orderId);
        p.setCashfreeOrderId(orderId);
        p.setPaymentSessionId(saved.getPaymentSessionId());
        paymentRepository.save(p);

        // Add Logs for tracking
        System.out.println("--------------------------------------------------");
        System.out.println("🔗 PAYMENT LINK GENERATED");
        System.out.println("ID: " + id);
        System.out.println("Order ID: " + orderId);
        System.out.println("Amount: ₹" + amount);
        System.out.println("Customer: " + allocation.getStudentName() + " (" + allocation.getStudentEmail() + ")");
        System.out.println("Session ID: " + saved.getPaymentSessionId());
        System.out.println("--------------------------------------------------");

        log.info("Payment link generated for installment {} | Amount: {} | Session: {}", 
            id, amount, saved.getPaymentSessionId());

        // Send Email
        try {
            // Using session ID as the link for now, consider prepending frontend URL if available
            String paymentUrl = "http://localhost:5173/fee/pay/" + orderId; 
            emailService.sendPaymentLinkEmail(
                allocation.getStudentEmail(),
                allocation.getStudentName(),
                paymentUrl,
                amount,
                installment.getDueDate()
            );
        } catch (Exception e) {
            log.error("Failed to send payment link email for installment: " + id, e);
        }

        return saved;
    }

    @Override
    public List<StudentInstallmentPlan> generateInstallmentsFromStructure(StudentFeeAllocation allocation) {
        log.info("Generating installments for allocation: {}, count: {}, installment total: {}, one-time: {}", 
            allocation.getId(), allocation.getInstallmentCount(), allocation.getInstallmentAmount(), allocation.getOneTimeAmount());
            
         if (repo.existsByStudentFeeAllocationId(allocation.getId())) {
             log.info("Installments already exist for allocation: {}. Skipping generation.", allocation.getId());
             return repo.findByStudentFeeAllocationId(allocation.getId());
         }

         List<StudentInstallmentPlan> installments = new ArrayList<>();
         int count = (allocation.getInstallmentCount() != null && allocation.getInstallmentCount() > 0) ? allocation.getInstallmentCount() : 1;
         
         BigDecimal installmentBase = allocation.getInstallmentAmount() != null ? allocation.getInstallmentAmount() : BigDecimal.ZERO;
         BigDecimal oneTimeFees = allocation.getOneTimeAmount() != null ? allocation.getOneTimeAmount() : BigDecimal.ZERO;
         
         BigDecimal perInstallmentAmt = installmentBase.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
         BigDecimal totalDistributed = BigDecimal.ZERO;

         for (int i = 1; i <= count; i++) {
             StudentInstallmentPlan plan = new StudentInstallmentPlan();
             plan.setStudentFeeAllocationId(allocation.getId());
             plan.setUserId(allocation.getUserId());
             plan.setInstallmentNumber(i);
             plan.setPaidAmount(BigDecimal.ZERO);
             plan.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING);
             plan.setDueDate(LocalDate.now().plusMonths(i - 1)); // Start with current month

             BigDecimal currentAmt = perInstallmentAmt;
             
             // 🎯 FIX: Adjust last installment for rounding differences
             if (i == count) {
                 currentAmt = installmentBase.subtract(totalDistributed);
             } else {
                 totalDistributed = totalDistributed.add(perInstallmentAmt);
             }

             // 🎯 FIX: Bundle all one-time fees (Admission, Exam, etc.) into the FIRST installment
             if (i == 1) {
                 plan.setInstallmentAmount(currentAmt.add(oneTimeFees));
                 plan.setLabel(count > 1 ? "Upfront + Term #1" : "Full Payment");
             } else {
                 plan.setInstallmentAmount(currentAmt);
                 plan.setLabel("Term #" + i);
             }

             installments.add(plan);
         }

         List<StudentInstallmentPlan> saved = repo.saveAll(installments);
         log.info("Saved {} installments for allocation: {}", saved.size(), allocation.getId());
         return saved;
    }

    @Override
    public InstallmentPlanResponse getById(Long id) {
        return repo.findById(id)
                .map(FeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found: " + id));
    }

    @Override
    public void updateStatus(Long id, String status) {
        StudentInstallmentPlan plan = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found: " + id));
        plan.setStatus(StudentInstallmentPlan.InstallmentStatus.valueOf(status));
        repo.save(plan);
        auditLogService.log("FEE", "INSTALLMENT", id, FeeAuditLog.Action.UPDATE, "Status change", status);
    }

    @Override
    public void lockForEarlyPayment(Long id) {
        StudentInstallmentPlan plan = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Installment not found: " + id));
        plan.setStatus(StudentInstallmentPlan.InstallmentStatus.LOCKED_FOR_EARLY_PAYMENT);
        repo.save(plan);
        auditLogService.log("FEE", "INSTALLMENT", id, FeeAuditLog.Action.UPDATE, "LOCKED", "EARLY_PAYMENT");
    }
}