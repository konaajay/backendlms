package com.lms.www.fee.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.fee.dto.*;
import com.lms.www.fee.payment.entity.PaymentStatus;
import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.ledger.service.AuditLogService;
import com.lms.www.fee.structure.service.StructureService;
import com.lms.www.fee.service.NotificationService;
import com.lms.www.fee.service.StudentFeeAllocationService;
import com.lms.www.fee.service.StudentInstallmentPlanService;
import com.lms.www.fee.service.UserService;
import com.lms.www.repository.ParentRepository;
import com.lms.www.model.Parent;
import com.lms.www.security.UserContext;
import com.lms.www.affiliate.service.AffiliateLeadService;
import com.lms.www.affiliate.service.AffiliateService;
import com.lms.www.affiliate.dto.AffiliateLeadDTO;
import com.lms.www.affiliate.service.SaleService;
import com.lms.www.affiliate.entity.AffiliateLink;
import com.lms.www.management.service.StudentBatchService;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.config.FeeModuleConfig;
import com.lms.www.fee.config.FeeConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentFeeAllocationServiceImpl implements StudentFeeAllocationService {

    private final StudentFeeAllocationRepository allocationRepo;
    private final StructureService feeStructureService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final StudentInstallmentPlanService installmentService;
    private final UserService userService;
    private final ParentRepository parentRepository;
    private final UserContext userContext;
    private final FeeModuleConfig feeModuleConfig;
    private final AffiliateLeadService affiliateLeadService;
    private final SaleService affiliateSaleService;
    private final AffiliateService affiliateService;
    private final StudentBatchService studentBatchService;
    private final com.lms.www.fee.installment.repository.StudentInstallmentPlanRepository installmentRepo;
    private final com.lms.www.fee.payment.repository.StudentFeePaymentRepository paymentRepo;

    @Override
    public StudentFeeAllocationResponse create(CreateAllocationRequest request) {
        Long userId = request.getUserId();
        if (userId == null) throw new IllegalArgumentException("UserId cannot be null");
        
        Integer installmentCount = request.getInstallmentCount();
        if (installmentCount != null && (installmentCount < feeModuleConfig.getInstallment().getMin() || installmentCount > feeModuleConfig.getInstallment().getMax())) {
            throw new IllegalArgumentException("Installment count must be between " + 
                feeModuleConfig.getInstallment().getMin() + " and " + feeModuleConfig.getInstallment().getMax());
        }

        FeeStructureResponse structure = feeStructureService.getStructureById(request.getFeeStructureId());
        UserDto user = userService.getUser(userId);

        if (allocationRepo.findByUserIdAndBatchId(userId, structure.getBatchId()).stream().findFirst().isPresent()) {
            throw new IllegalStateException("Student already allocated for this batch");
        }

        BigDecimal finalAdmin = request.getAdminDiscount() != null ? request.getAdminDiscount() : BigDecimal.ZERO;
        BigDecimal finalAdditional = request.getAdditionalDiscount() != null ? request.getAdditionalDiscount() : BigDecimal.ZERO;
        BigDecimal finalPromo = request.getPromoDiscount() != null ? request.getPromoDiscount() : BigDecimal.ZERO;
        BigDecimal finalAffiliate = request.getAffiliateDiscount() != null ? request.getAffiliateDiscount() : BigDecimal.ZERO;
        BigDecimal advance = request.getAdvancePayment() != null ? request.getAdvancePayment() : BigDecimal.ZERO;

        StudentFeeAllocation allocation = StudentFeeAllocation.builder()
                .userId(userId)
                .feeStructureId(request.getFeeStructureId())
                .batchId(structure.getBatchId())
                .courseId(structure.getCourseId())
                .studentName(user.getName())
                .studentEmail(user.getEmail())
                .courseName(structure.getCourseName())
                .batchName(structure.getBatchName())
                .originalAmount(structure.getTotalAmount())
                .adminDiscount(finalAdmin)
                .additionalDiscount(finalAdditional)
                .promoDiscount(finalPromo)
                .affiliateDiscount(finalAffiliate)
                .advancePayment(advance)
                .installmentCount(installmentCount != null ? installmentCount : structure.getInstallmentCount())
                .durationMonths(structure.getDurationMonths())
                .status(StudentFeeAllocation.AllocationStatus.ACTIVE)
                .allocationDate(LocalDate.now())
                .appliedPromoCode(request.getAppliedPromoCode())
                .currency(structure.getCurrency())
                .build();

        // 🔹 Affiliate Discount Logic (Automatic)
        if (finalAffiliate.equals(BigDecimal.ZERO)) {
            applyAutomaticAffiliateDiscount(allocation, user, structure);
        }

        recalculateTotals(allocation, structure);

        StudentFeeAllocation saved = allocationRepo.save(allocation);
        
        // 🔹 Record Affiliate Sale if applicable
        recordAffiliateSale(allocation, user, structure);

        List<StudentInstallmentPlan> installments = installmentService.generateInstallmentsFromStructure(saved);
        
        // 🔹 Auto-Enroll Student in Batch
        autoEnrollStudent(saved);

        // 🔹 Send Enrollment Notification
        sendEnrollmentNotification(saved, installments);

        auditLogService.log(FeeConstants.MODULE_NAME, "StudentFeeAllocation", saved.getId(), FeeAuditLog.Action.CREATE, null, saved.toString());

        return FeeMapper.toResponse(saved);
    }

    @Override
    public List<StudentFeeAllocationResponse> createBulk(BulkAllocationRequest request) {
        return request.getUserIds().stream()
                .map(uid -> {
                    CreateAllocationRequest req = new CreateAllocationRequest();
                    req.setUserId(uid);
                    req.setFeeStructureId(request.getFeeStructureId());
                    req.setAdminDiscount(request.getAdminDiscount());
                    req.setAdditionalDiscount(request.getAdditionalDiscount());
                    req.setAdvancePayment(request.getAdvancePayment());
                    req.setInstallmentCount(request.getInstallmentCount());
                    req.setAppliedPromoCode(request.getAppliedPromoCode());
                    req.setPromoDiscount(request.getPromoDiscount());
                    return create(req);
                })
                .collect(Collectors.toList());
    }

    @Override
    public StudentFeeAllocationResponse getById(Long id) {
        return FeeMapper.toResponse(getFeeAllocationById(id));
    }

    @Override
    public StudentFeeAllocationResponse getByIdSecure(Long id) {
        StudentFeeAllocation allocation = getFeeAllocationById(id);
        if (!userContext.isAdmin() && !userContext.getCurrentUserId().equals(allocation.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }
        return FeeMapper.toResponse(allocation);
    }

    @Override
    public List<StudentFeeAllocationResponse> getByUser(Long userId) {
        return allocationRepo.findByUserId(userId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentFeeAllocationResponse> getByUserSecure(Long userId) {
        if (!userContext.isAdmin() && !userContext.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return getByUser(userId);
    }

    @Override
    public StudentFeeAllocationResponse getLatest(Long userId) {
        return allocationRepo.findByUserId(userId).stream()
                .max((a, b) -> a.getId().compareTo(b.getId()))
                .map(FeeMapper::toResponse)
                .orElse(null);
    }

    @Override
    public StudentFeeAllocationResponse getLatestSecure(Long userId) {
        if (!userContext.isAdmin() && !userContext.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return getLatest(userId);
    }

    @Override
    public List<StudentFeeAllocationResponse> getByBatch(Long batchId) {
        return allocationRepo.findByBatchId(batchId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentFeeAllocationResponse> getAllAllocations() {
        return allocationRepo.findAll().stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void recalculateTotals(StudentFeeAllocation allocation, FeeStructureResponse structure) {
        BigDecimal oneTimeBase = BigDecimal.ZERO;
        BigDecimal installmentBase = BigDecimal.ZERO;

        for (var c : structure.getComponents()) {
            if (c.getAmount() != null) {
                if (Boolean.FALSE.equals(c.getInstallmentAllowed())) {
                    oneTimeBase = oneTimeBase.add(c.getAmount());
                } else {
                    installmentBase = installmentBase.add(c.getAmount());
                }
            }
        }

        // Add Admission Fee to the subtotal if it exists in the structure
        BigDecimal admFee = structure.getAdmissionFeeAmount() != null ? structure.getAdmissionFeeAmount() : BigDecimal.ZERO;
        allocation.setAdmissionFeeAmount(admFee);
        
        // If "Include in Base" is implicit or handled as a component, we follow structure's lead.
        // Usually, the dedicated field is separate from components. We'll add it to oneTimeBase.
        oneTimeBase = oneTimeBase.add(admFee);

        BigDecimal totalDiscount = allocation.getAdminDiscount().add(allocation.getAdditionalDiscount())
                .add(allocation.getPromoDiscount()).add(allocation.getAffiliateDiscount());
        
        if (totalDiscount.compareTo(installmentBase) > 0) totalDiscount = installmentBase;
        allocation.setTotalDiscount(totalDiscount);

        BigDecimal netInstallment = installmentBase.subtract(totalDiscount);
        BigDecimal gstRate = Boolean.TRUE.equals(structure.getGstApplicable()) && structure.getGstPercent() != null 
                ? structure.getGstPercent() : BigDecimal.ZERO;

        BigDecimal gstAmount = BigDecimal.ZERO;
        BigDecimal payableInstallment;
        BigDecimal payableOneTime;

        if (Boolean.TRUE.equals(structure.getGstIncludedInFee())) {
            BigDecimal factor = gstRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).add(BigDecimal.ONE);
            BigDecimal baseWithoutGst = netInstallment.divide(factor, 2, RoundingMode.HALF_UP);
            gstAmount = netInstallment.subtract(baseWithoutGst);
            payableInstallment = netInstallment;
            
            BigDecimal oneTimeBaseWithoutGst = oneTimeBase.divide(factor, 2, RoundingMode.HALF_UP);
            gstAmount = gstAmount.add(oneTimeBase.subtract(oneTimeBaseWithoutGst));
            payableOneTime = oneTimeBase;
        } else {
            gstAmount = netInstallment.multiply(gstRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            payableInstallment = netInstallment.add(gstAmount);
            
            BigDecimal oneTimeGst = oneTimeBase.multiply(gstRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            payableOneTime = oneTimeBase.add(oneTimeGst);
            gstAmount = gstAmount.add(oneTimeGst);
        }

        BigDecimal payable = payableInstallment.add(payableOneTime);
        
        allocation.setGstRate(gstRate);
        allocation.setGstAmount(gstAmount);
        allocation.setOneTimeAmount(payableOneTime);
        allocation.setInstallmentAmount(payableInstallment);
        allocation.setPayableAmount(payable);
        allocation.setRemainingAmount(payable.subtract(allocation.getAdvancePayment()));
    }

    private void applyAutomaticAffiliateDiscount(StudentFeeAllocation allocation, UserDto user, FeeStructureResponse structure) {
        try {
            AffiliateLeadDTO lead = affiliateLeadService.getLeadByEmailAndBatch(user.getEmail(), structure.getBatchId());
            if (lead != null) {
                BigDecimal discountRate = lead.getStudentDiscountValue();
                
                if (lead.getReferralCode() != null) {
                    java.util.Optional<AffiliateLink> linkOpt = affiliateService.getLinkByReferralCode(lead.getReferralCode());
                    if (linkOpt.isPresent()) {
                        AffiliateLink link = linkOpt.get();
                        if (link.getStatus() == AffiliateLink.LinkStatus.ACTIVE &&
                                (link.getExpiresAt() == null || link.getExpiresAt().isAfter(java.time.LocalDateTime.now()))) {
                            discountRate = link.getStudentDiscountValue();
                        }
                    }
                }

                if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal affiliateDiscount = structure.getTotalAmount().multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    allocation.setAffiliateDiscount(affiliateDiscount);
                    allocation.setAffiliateId(lead.getAffiliateId());
                    log.info("Applied automatic affiliate discount: {} for student: {}", affiliateDiscount, user.getEmail());
                }
            }
        } catch (Exception e) {
            log.warn("Affiliate check failed: {}", e.getMessage());
        }
    }

    private void recordAffiliateSale(StudentFeeAllocation saved, UserDto user, FeeStructureResponse structure) {
        if (saved.getAffiliateId() != null) {
            try {
                AffiliateLeadDTO lead = affiliateLeadService.getLeadByEmailAndBatch(user.getEmail(), structure.getBatchId());
                if (lead != null) {
                    affiliateSaleService.convertLeadToEnrollment(lead.getId(), saved.getUserId(), structure.getTotalAmount());
                }
            } catch (Exception e) {
                log.error("Affiliate sale recording failed: {}", e.getMessage());
            }
        }
    }

    private void autoEnrollStudent(StudentFeeAllocation saved) {
        if (saved.getUserId() == null || saved.getBatchId() == null || saved.getCourseId() == null) {
            log.warn("Skipping auto-enrollment: missing critical fields for student ID: {}", saved.getUserId());
            return;
        }

        try {
            boolean alreadyEnrolled = studentBatchService.getStudentsByBatch(saved.getBatchId())
                .stream()
                .anyMatch(sb -> sb.getStudentId().equals(saved.getUserId()) && "ACTIVE".equals(sb.getStatus()));
            
            if (alreadyEnrolled) {
                log.info("Student {} already active in batch {}. Skipping auto-enrollment to avoid rollback.", saved.getUserId(), saved.getBatchId());
                return;
            }

            StudentBatch sb = new StudentBatch();
            sb.setStudentId(saved.getUserId()); // Mapping UserID to student_id per system pattern
            sb.setStudentName(saved.getStudentName() != null ? saved.getStudentName() : "Student");
            sb.setStudentEmail(saved.getStudentEmail() != null ? saved.getStudentEmail() : "email@notprovided.com");
            sb.setCourseId(saved.getCourseId());
            sb.setBatchId(saved.getBatchId());
            sb.setStatus("ACTIVE");
            sb.setJoinedAt(LocalDateTime.now());
            sb.setUserId(saved.getUserId());
            studentBatchService.enrollStudent(sb);
            log.info("Auto-enrolled student: {} (ID: {}) in batch: {}", saved.getStudentName(), saved.getUserId(), saved.getBatchId());
        } catch (Exception e) {
            log.error("Auto-enrollment failed for student: {} - Error: {}", saved.getUserId(), e.getMessage());
        }
    }

    private void sendEnrollmentNotification(StudentFeeAllocation saved, List<StudentInstallmentPlan> installments) {
        try {
            notificationService.sendEnrollmentNotification(saved, installments, BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("Notification failed: {}", e.getMessage());
        }
    }

    @Override
    public StudentFeeAllocationResponse update(Long id, UpdateAllocationRequest request) {
        StudentFeeAllocation existing = getFeeAllocationById(id);
        String oldValue = existing.toString();
        FeeStructureResponse structure = feeStructureService.getStructureById(existing.getFeeStructureId());

        if (request.getAdminDiscount() != null) existing.setAdminDiscount(request.getAdminDiscount());
        if (request.getAdditionalDiscount() != null) existing.setAdditionalDiscount(request.getAdditionalDiscount());
        if (request.getPromoDiscount() != null) existing.setPromoDiscount(request.getPromoDiscount());
        if (request.getInstallmentCount() != null) existing.setInstallmentCount(request.getInstallmentCount());
        
        recalculateTotals(existing, structure);
        
        StudentFeeAllocation saved = allocationRepo.save(existing);
        
        // 🔹 Sync installments if modified
        if (request.getInstallmentCount() != null || request.getAdminDiscount() != null || 
            request.getAdditionalDiscount() != null || request.getPromoDiscount() != null) {
            installmentService.generateInstallmentsFromStructure(saved);
        }

        auditLogService.log(FeeConstants.MODULE_NAME, "StudentFeeAllocation", saved.getId(), FeeAuditLog.Action.UPDATE, oldValue, saved.toString());
        return FeeMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        StudentFeeAllocation existing = getFeeAllocationById(id);
        String oldValue = existing.toString();
        allocationRepo.deleteById(id);
        auditLogService.log(FeeConstants.MODULE_NAME, "StudentFeeAllocation", id, FeeAuditLog.Action.DELETE, oldValue, null);
    }

    @Override
    public List<StudentFeeAllocationResponse> getByParent(Long parentUserId) {
        Parent parent = parentRepository.findByUser_UserId(parentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
        List<Long> studentUserIds = parent.getStudents().stream()
                .map(rel -> rel.getStudent().getUser().getUserId())
                .collect(Collectors.toList());
        return allocationRepo.findByUserIdIn(studentUserIds).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentFeeAllocationResponse> getByParentSecure(Long parentUserId) {
        if (!userContext.isAdmin() && !userContext.getCurrentUserId().equals(parentUserId)) {
            throw new AccessDeniedException("Access denied");
        }
        return getByParent(parentUserId);
    }

    @Override
    public void syncStudentInfo(Long id) {
        StudentFeeAllocation allocation = getFeeAllocationById(id);
        UserDto user = userService.getUser(allocation.getUserId());
        if (user != null) {
            allocation.setStudentName(user.getName());
            allocation.setStudentEmail(user.getEmail());
            allocationRepo.save(allocation);
        }
    }

    @Override
    public void syncAllStudentInfo() {
        allocationRepo.findAll().forEach(a -> syncStudentInfo(a.getId()));
    }

    @Override
    public StudentFeeAllocation getFeeAllocationById(Long id) {
        return allocationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + id));
    }

    @Override
    public StudentLedgerResponse getStudentLedger(Long userId) {
        StudentFeeAllocation allocation = allocationRepo.findByUserId(userId).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No fee allocation found for user: " + userId));

        List<StudentInstallmentPlan> installments = installmentRepo.findByStudentFeeAllocationId(allocation.getId());
        List<com.lms.www.fee.payment.entity.StudentFeePayment> payments = paymentRepo.findByStudentFeeAllocationId(allocation.getId());

        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
                .map(com.lms.www.fee.payment.entity.StudentFeePayment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return StudentLedgerResponse.builder()
                .allocationSummary(StudentLedgerResponse.AllocationSummary.builder()
                        .allocationId(allocation.getId())
                        .feeStructureId(allocation.getFeeStructureId())
                        .feeStructureName(allocation.getBatchName() + " Structure")
                        .studentName(allocation.getStudentName())
                        .build())
                .totalDue(allocation.getPayableAmount())
                .totalPaid(totalPaid)
                .remainingBalance(allocation.getPayableAmount().subtract(totalPaid))
                .installments(installments.stream().map(inst -> StudentLedgerResponse.InstallmentDto.builder()
                        .id(inst.getId())
                        .amount(inst.getInstallmentAmount())
                        .dueDate(inst.getDueDate())
                        .status(inst.getStatus() != null ? inst.getStatus().name() : "PENDING")
                        .orderId(inst.getCashfreeOrderId())
                        .paymentLinkAvailable(inst.getCashfreeOrderId() != null)
                        .build()).collect(Collectors.toList()))
                .payments(payments.stream().map(p -> StudentFeePaymentResponse.builder()
                        .paymentId(p.getId())
                        .paidAmount(p.getPaidAmount())
                        .paymentDate(p.getPaymentDate())
                        .paymentMode(p.getPaymentMode() != null ? p.getPaymentMode().name() : "OTHER")
                        .paymentStatus(p.getPaymentStatus() != null ? p.getPaymentStatus().name() : "PENDING")
                        .transactionReference(p.getTransactionReference())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
