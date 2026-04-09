package com.lms.www.fee.dto;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import com.lms.www.fee.structure.entity.FeeStructure;
import com.lms.www.fee.penalty.entity.FeePenaltySlab;
import com.lms.www.fee.structure.entity.FeeStructureComponent;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.payment.entity.StudentFeePayment;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.payment.entity.StudentFeeRefund;
import com.lms.www.fee.penalty.entity.FeePenalty;
import com.lms.www.fee.discount.entity.FeeDiscount;
import com.lms.www.fee.structure.entity.FeeType;
import com.lms.www.fee.ledger.entity.FeeAuditLog;
import com.lms.www.fee.admin.entity.RefundRule;
import com.lms.www.fee.payment.entity.PaymentNotification;
import com.lms.www.fee.payment.entity.EarlyPayment;
import com.lms.www.fee.admin.entity.MasterSetting;
import com.lms.www.model.User;

public class FeeMapper {

    public static FeeStructureResponse toResponse(FeeStructure entity) {
        if (entity == null)
            return null;

        FeeStructureResponse dto = new FeeStructureResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAcademicYear(entity.getAcademicYear());
        dto.setCourseId(entity.getCourseId());
        dto.setCourseName(entity.getCourseName());
        dto.setBatchId(entity.getBatchId());
        dto.setBatchName(entity.getBatchName());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setIsActive(entity.isActive());

        dto.setAdmissionFeeAmount(entity.getAdmissionFeeAmount());
        dto.setAdmissionNonRefundable(entity.getAdmissionNonRefundable());

        dto.setGstApplicable(entity.getGstApplicable());
        dto.setGstPercent(entity.getGstPercent());
        dto.setGstIncludedInFee(entity.getGstIncludedInFee());

        dto.setInstallmentCount(entity.getInstallmentCount());
        dto.setDurationMonths(entity.getDurationMonths());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());

        dto.setPenaltyType(entity.getPenaltyType());
        dto.setMaxPenaltyCap(entity.getMaxPenaltyCap());
        dto.setPenaltyPercentage(entity.getPenaltyPercentage());
        dto.setFixedPenaltyAmount(entity.getFixedPenaltyAmount());

        dto.setDiscountType(entity.getDiscountType());
        dto.setDiscountValue(entity.getDiscountValue());

        if (entity.getComponents() != null) {
            dto.setComponents(entity.getComponents().stream()
                    .map(FeeMapper::toComponentDto)
                    .collect(Collectors.toList()));
        }

        if (entity.getSlabs() != null) {
            dto.setSlabs(entity.getSlabs().stream()
                    .map(FeeMapper::toSlabDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private static FeeStructureResponse.SlabDTO toSlabDto(FeePenaltySlab entity) {
        FeeStructureResponse.SlabDTO dto = new FeeStructureResponse.SlabDTO();
        dto.setId(entity.getId());
        dto.setFromDay(entity.getFromDay());
        dto.setToDay(entity.getToDay());
        dto.setType(entity.getPenaltyType() != null ? entity.getPenaltyType().name() : null);
        dto.setValue(entity.getValue());
        return dto;
    }

    private static FeeStructureResponse.ComponentDTO toComponentDto(FeeStructureComponent entity) {
        FeeStructureResponse.ComponentDTO dto = new FeeStructureResponse.ComponentDTO();
        dto.setId(entity.getId());
        dto.setFeeTypeId(entity.getFeeTypeId());
        dto.setName(entity.getName());
        dto.setAmount(entity.getAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setRefundable(entity.getRefundable());
        dto.setInstallmentAllowed(entity.getInstallmentAllowed());
        dto.setMandatory(entity.getMandatory());
        return dto;
    }

    public static StudentFeeAllocationResponse toResponse(StudentFeeAllocation entity) {
        if (entity == null)
            return null;

        StudentFeeAllocationResponse dto = new StudentFeeAllocationResponse();
        dto.setAllocationId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setStudentName(entity.getStudentName());
        dto.setStudentEmail(entity.getStudentEmail());
        dto.setFeeStructureId(entity.getFeeStructureId());
        dto.setFeeStructureName(entity.getCourseName());
        dto.setOriginalAmount(entity.getOriginalAmount());
        dto.setTotalDiscount(entity.getTotalDiscount());
        BigDecimal payableAmount = entity.getPayableAmount() != null ? entity.getPayableAmount() : BigDecimal.ZERO;
        BigDecimal remainingAmount = entity.getRemainingAmount() != null ? entity.getRemainingAmount()
                : BigDecimal.ZERO;
        dto.setPayableAmount(payableAmount);
        dto.setPaidAmount(payableAmount.subtract(remainingAmount));
        dto.setRemainingAmount(remainingAmount);
        dto.setCurrency(entity.getCurrency());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : "PENDING");
        dto.setAllocationDate(entity.getAllocationDate() != null ? entity.getAllocationDate().atStartOfDay() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setOneTimeAmount(entity.getOneTimeAmount() != null ? entity.getOneTimeAmount() : BigDecimal.ZERO);
        dto.setInstallmentAmount(
                entity.getInstallmentAmount() != null ? entity.getInstallmentAmount() : BigDecimal.ZERO);
        dto.setBatchId(entity.getBatchId());
        dto.setCourseId(entity.getCourseId());
        dto.setCourseName(entity.getCourseName());
        dto.setBatchName(entity.getBatchName());
        dto.setAppliedPromoCode(entity.getAppliedPromoCode());
        dto.setPromoDiscount(entity.getPromoDiscount());
        dto.setAffiliateDiscount(entity.getAffiliateDiscount());
        dto.setAffiliateId(entity.getAffiliateId());

        return dto;
    }

    public static StudentFeePaymentResponse toResponse(StudentFeePayment entity) {
        if (entity == null)
            return null;

        StudentFeePaymentResponse dto = new StudentFeePaymentResponse();
        dto.setPaymentId(entity.getId());
        dto.setAllocationId(entity.getStudentFeeAllocationId());
        dto.setInstallmentId(entity.getStudentInstallmentPlanId());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setPaymentMode(entity.getPaymentMode() != null ? entity.getPaymentMode().name() : null);
        dto.setPaymentStatus(entity.getPaymentStatus() != null ? entity.getPaymentStatus().name() : null);
        dto.setTransactionReference(entity.getTransactionReference());
        dto.setScreenshotUrl(entity.getScreenshotUrl());
        dto.setCurrency(entity.getCurrency());
        dto.setRecordedBy(entity.getRecordedBy());

        return dto;
    }

    public static StudentFeePaymentResponse toResponse(StudentFeePayment entity, StudentFeeAllocation allocation) {
        StudentFeePaymentResponse dto = toResponse(entity);
        if (dto == null)
            return null;

        if (allocation != null) {
            dto.setStudentName(allocation.getStudentName());
            dto.setStudentEmail(allocation.getStudentEmail());
        }

        return dto;
    }

    public static InstallmentPlanResponse toResponse(StudentInstallmentPlan entity) {
        if (entity == null)
            return null;
        InstallmentPlanResponse dto = new InstallmentPlanResponse();
        dto.setId(entity.getId());
        dto.setAllocationId(entity.getStudentFeeAllocationId());
        dto.setInstallmentNumber(entity.getInstallmentNumber());
        dto.setInstallmentAmount(entity.getInstallmentAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setLabel(entity.getLabel());
        
        // Legacy fields
        dto.setNumber(entity.getInstallmentNumber());
        dto.setAmount(entity.getInstallmentAmount());
        
        return dto;
    }

    public static RefundResponse toResponse(StudentFeeRefund entity) {
        if (entity == null)
            return null;
        RefundResponse dto = new RefundResponse();
        dto.setId(entity.getId());
        dto.setAllocationId(entity.getAllocationId());
        dto.setAmount(entity.getAmount());
        dto.setType(entity.getRefundType() != null ? entity.getRefundType().name() : null);
        dto.setMode(entity.getRefundMode() != null ? entity.getRefundMode().name() : null);
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setReason(entity.getRefundReason());
        dto.setRejectionReason(entity.getRejectionReason());
        dto.setApprovedBy(entity.getApprovedBy());
        dto.setRejectedBy(entity.getRejectedBy());
        dto.setRequestDate(entity.getRequestDate());
        dto.setProcessedDate(entity.getProcessedDate());
        return dto;
    }

    public static PenaltyResponse toResponse(FeePenalty entity) {
        if (entity == null)
            return null;
        return PenaltyResponse.builder()
                .id(entity.getId())
                .installmentId(entity.getInstallmentId())
                .amount(entity.getAmount())
                .reason(entity.getReason())
                .waived(entity.isWaived())
                .build();
    }

    public static FeeDiscountResponse toResponse(FeeDiscount entity) {
        if (entity == null)
            return null;
        FeeDiscountResponse dto = new FeeDiscountResponse();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setAmount(entity.getAmount());
        dto.setReason(entity.getReason());
        return dto;
    }

    public static NotificationResponse toResponse(PaymentNotification entity) {
        if (entity == null)
            return null;
        NotificationResponse dto = new NotificationResponse();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public static FeeTypeResponse toResponse(FeeType entity) {
        if (entity == null)
            return null;
        FeeTypeResponse dto = new FeeTypeResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setMandatory(entity.getMandatory());
        dto.setRefundable(entity.getRefundable());
        dto.setOneTime(entity.getOneTime());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }

    public static RefundRuleResponse toResponse(RefundRule entity) {
        if (entity == null)
            return null;
        RefundRuleResponse dto = new RefundRuleResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getRuleName());
        dto.setDaysBeforeStart(entity.getDaysBeforeStart());
        dto.setRefundPercentage(entity.getRefundPercentage());
        dto.setActive(entity.getActive());
        return dto;
    }

    public static RefundRule toEntity(RefundRuleRequest dto) {
        if (dto == null)
            return null;
        RefundRule entity = new RefundRule();
        entity.setRuleName(dto.getName());
        entity.setDaysBeforeStart(dto.getDaysBeforeStart());
        entity.setRefundPercentage(dto.getRefundPercentage());
        entity.setActive(dto.getActive());
        return entity;
    }

    public static StudentInstallmentPlan toEntity(InstallmentPlanRequest dto) {
        if (dto == null)
            return null;
        StudentInstallmentPlan entity = new StudentInstallmentPlan();
        entity.setInstallmentNumber(dto.getInstallmentNumber());
        entity.setInstallmentAmount(dto.getInstallmentAmount());
        entity.setDueDate(dto.getDueDate());
        entity.setLabel(dto.getLabel());
        return entity;
    }



    public static FeePenaltySlabResponse toResponse(FeePenaltySlab entity) {
        if (entity == null)
            return null;
        return FeePenaltySlabResponse.builder()
                .id(entity.getId())
                .fromDay(entity.getFromDay())
                .toDay(entity.getToDay())
                .penaltyType(entity.getPenaltyType())
                .value(entity.getValue())
                .active(entity.isActive())
                .build();
    }

    public static FeeAuditLogResponse toResponse(FeeAuditLog entity) {
        if (entity == null)
            return null;
        return FeeAuditLogResponse.builder()
                .id(entity.getId())
                .module(entity.getModule())
                .entityName(entity.getEntityName())
                .entityId(entity.getEntityId())
                .action(entity.getAction() != null ? entity.getAction().name() : null)
                .performedBy(entity.getPerformedBy())
                .performedAt(entity.getPerformedAt())
                .ipAddress(entity.getIpAddress())
                .oldValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .build();
    }

    public static UserDto toUserDto(User entity) {
        if (entity == null)
            return null;
        UserDto dto = new UserDto();
        dto.setId(entity.getUserId());
        dto.setName(entity.getFirstName() + " " + (entity.getLastName() != null ? entity.getLastName() : ""));
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    public static PaymentLinkResponse toPaymentLinkResponse(StudentInstallmentPlan entity) {
        if (entity == null)
            return null;
        
        // Link points to frontend payment page
        String paymentUrl = "http://localhost:5173/fee/pay/" + entity.getCashfreeOrderId();
        
        return PaymentLinkResponse.builder()
                .installmentId(entity.getId())
                .paymentLink(paymentUrl)
                .success(true)
                .build();
    }

    public static MasterSettingResponse toResponse(MasterSetting entity) {
        if (entity == null)
            return null;
        return MasterSettingResponse.builder()
                .id(entity.getId())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .key(entity.getKeyName())
                .keyName(entity.getKeyName())
                .value(entity.getValue())
                .description(entity.getDescription())
                .active(entity.isActive())
                .build();
    }

    public static EarlyPaymentResponse toResponse(EarlyPayment entity) {
        if (entity == null)
            return null;
        return EarlyPaymentResponse.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .installmentIds(entity.getInstallmentIds())
                .totalOriginalAmount(entity.getTotalOriginalAmount())
                .discountType(entity.getDiscountType() != null ? entity.getDiscountType().name() : null)
                .discountValue(entity.getDiscountValue())
                .finalAmount(entity.getFinalAmount())
                .cashfreeOrderId(entity.getCashfreeOrderId())
                .paymentSessionId(entity.getPaymentSessionId())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .linkCreatedAt(entity.getLinkCreatedAt())
                .linkExpiry(entity.getLinkExpiry())
                .build();
    }
}
