package com.lms.www.fee.allocation.service;

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
import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.ledger.service.AuditLogService;
import com.lms.www.fee.structure.service.StructureService;
import com.lms.www.fee.service.NotificationService;
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
public class AllocationServiceImpl implements AllocationService {

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

    @Override
    public StudentFeeAllocationResponse create(CreateAllocationRequest request) {
        Long userId = request.getUserId() != null ? request.getUserId() : request.getStudentId();
        if (userId == null) throw new IllegalArgumentException("User/Student ID cannot be null");
        
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

        BigDecimal oneTimeBase = BigDecimal.ZERO;
        BigDecimal installmentBase = BigDecimal.ZERO;

        for (var c : structure.getComponents()) {
            // ONLY sum breakdown components (those WITHOUT a due date)
            // Items WITH a due date are installments and should not be part of the base calculation
            if (c.getAmount() != null && c.getDueDate() == null) {
                if (Boolean.FALSE.equals(c.getInstallmentAllowed())) oneTimeBase = oneTimeBase.add(c.getAmount());
                else installmentBase = installmentBase.add(c.getAmount());
            }
        }

        BigDecimal finalAdmin = request.getAdminDiscount() != null ? request.getAdminDiscount() : BigDecimal.ZERO;
        BigDecimal finalAdditional = request.getAdditionalDiscount() != null ? request.getAdditionalDiscount() : BigDecimal.ZERO;
        BigDecimal finalPromo = request.getPromoDiscount() != null ? request.getPromoDiscount() : BigDecimal.ZERO;
        
        BigDecimal affiliateDiscount = request.getAffiliateDiscount() != null ? request.getAffiliateDiscount() : BigDecimal.ZERO;
        Long affiliateId = null;
        Long leadId = null;
        
        if (affiliateDiscount.equals(BigDecimal.ZERO)) {
            try {
                AffiliateLeadDTO lead = affiliateLeadService.getLeadByEmailAndBatch(user.getEmail(), structure.getBatchId());
                if (lead != null) {
                    BigDecimal discountRate = BigDecimal.ZERO;
                    affiliateId = lead.getAffiliateId();
                    leadId = lead.getId();

                    if (lead.getReferralCode() != null) {
                        java.util.Optional<AffiliateLink> linkOpt = affiliateService.getLinkByReferralCode(lead.getReferralCode());
                        if (linkOpt.isPresent()) {
                            AffiliateLink link = linkOpt.get();
                            if (link.getStatus() == AffiliateLink.LinkStatus.ACTIVE &&
                                    (link.getExpiresAt() == null || link.getExpiresAt().isAfter(LocalDateTime.now()))) {
                                discountRate = link.getStudentDiscountValue();
                            }
                        }
                    }

                    if ((discountRate == null || discountRate.equals(BigDecimal.ZERO))) {
                        discountRate = lead.getStudentDiscountValue();
                    }

                    if (discountRate != null && discountRate.compareTo(BigDecimal.ZERO) > 0) {
                        affiliateDiscount = structure.getTotalAmount().multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to check for affiliate discount: {}", e.getMessage());
            }
        }

        BigDecimal totalDiscount = finalAdmin.add(finalAdditional).add(finalPromo).add(affiliateDiscount);
        if (totalDiscount.compareTo(installmentBase) > 0) totalDiscount = installmentBase;

        BigDecimal netInstallment = installmentBase.subtract(totalDiscount);
        BigDecimal gstRate = structure.getGstApplicable() ? structure.getGstPercent() : BigDecimal.ZERO;

        BigDecimal gstAmount = BigDecimal.ZERO;
        BigDecimal payableInstallment;
        BigDecimal payableOneTime;

        if (Boolean.TRUE.equals(structure.getGstIncludedInFee())) {
            BigDecimal factor = gstRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).add(BigDecimal.ONE);
            BigDecimal baseWithoutGst = netInstallment.divide(factor, 2, RoundingMode.HALF_UP);
            gstAmount = netInstallment.subtract(baseWithoutGst);
            payableInstallment = netInstallment;
            payableOneTime = oneTimeBase;
        } else {
            gstAmount = netInstallment.multiply(gstRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            payableInstallment = netInstallment.add(gstAmount);
            BigDecimal oneTimeGst = oneTimeBase.multiply(gstRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            payableOneTime = oneTimeBase.add(oneTimeGst);
            gstAmount = gstAmount.add(oneTimeGst);
        }

        BigDecimal payable = payableInstallment.add(payableOneTime);
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
                .totalDiscount(totalDiscount)
                .gstRate(gstRate)
                .gstAmount(gstAmount)
                .oneTimeAmount(payableOneTime)
                .installmentAmount(payableInstallment)
                .payableAmount(payable)
                .advancePayment(advance)
                .remainingAmount(payable.subtract(advance))
                .installmentCount(installmentCount != null ? installmentCount : structure.getInstallmentCount())
                .durationMonths(structure.getDurationMonths())
                .status(StudentFeeAllocation.AllocationStatus.ACTIVE)
                .allocationDate(LocalDate.now())
                .appliedPromoCode(request.getAppliedPromoCode())
                .promoDiscount(finalPromo)
                .affiliateDiscount(affiliateDiscount)
                .affiliateId(affiliateId)
                .build();

        java.util.Objects.requireNonNull(userId, "User ID cannot be null");
        StudentFeeAllocation saved = java.util.Objects.requireNonNull(allocationRepo.save(allocation));
        
        if (leadId != null) {
            try {
                affiliateSaleService.convertLeadToEnrollment(leadId, userId, structure.getTotalAmount());
            } catch (Exception e) {
                log.error("Failed to record affiliate sale: {}", e.getMessage());
            }
        }

        List<StudentInstallmentPlan> installments = installmentService.generateInstallmentsFromStructure(saved);
        
        try {
            StudentBatch sb = new StudentBatch();
            sb.setStudentId(saved.getUserId());
            sb.setStudentName(saved.getStudentName());
            sb.setStudentEmail(saved.getStudentEmail());
            sb.setCourseId(saved.getCourseId());
            sb.setBatchId(saved.getBatchId());
            sb.setStatus(StudentFeeAllocation.AllocationStatus.ACTIVE.name());
            sb.setJoinedAt(LocalDateTime.now());
            sb.setUserId(saved.getUserId());
            studentBatchService.enrollStudent(sb);
        } catch (Exception e) {
            log.error("Failed to auto-enroll student: {}", e.getMessage());
        }

        try {
            notificationService.sendEnrollmentNotification(saved, installments, BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("Failed to send enrollment notification: {}", e.getMessage());
        }

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
    public StudentFeeAllocationResponse getLatestSecure(Long userId) {
        if (!userContext.isAdmin() && !userContext.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return allocationRepo.findByUserId(userId).stream()
                .max((a, b) -> a.getId().compareTo(b.getId()))
                .map(FeeMapper::toResponse)
                .orElse(null);
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

    @Override
    public StudentFeeAllocationResponse update(Long id, UpdateAllocationRequest request) {
        StudentFeeAllocation existing = getFeeAllocationById(id);
        String oldValue = existing.toString();
        
        if (request.getAdminDiscount() != null) existing.setAdminDiscount(request.getAdminDiscount());
        if (request.getAdditionalDiscount() != null) existing.setAdditionalDiscount(request.getAdditionalDiscount());
        if (request.getInstallmentCount() != null) existing.setInstallmentCount(request.getInstallmentCount());
        
        StudentFeeAllocation saved = allocationRepo.save(existing);
        auditLogService.log(FeeConstants.MODULE_NAME, "StudentFeeAllocation", saved.getId(), FeeAuditLog.Action.UPDATE, oldValue, saved.toString());
        return FeeMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        StudentFeeAllocation existing = getFeeAllocationById(id);
        String oldValue = existing.toString();
        java.util.Objects.requireNonNull(id, "ID cannot be null");
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
    public void syncAllStudentInfo() {
        allocationRepo.findAll().forEach(a -> {
            UserDto user = userService.getUser(a.getUserId());
            if (user != null) {
                a.setStudentName(user.getName());
                a.setStudentEmail(user.getEmail());
                allocationRepo.save(a);
            }
        });
    }

    @Override
    public StudentFeeAllocation getFeeAllocationById(Long id) {
        java.util.Objects.requireNonNull(id, "ID cannot be null");
        return allocationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found: " + id));
    }
}
