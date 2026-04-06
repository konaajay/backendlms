package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureResponse {
    private Long id;
    private String name;
    private String academicYear;
    private Long courseId;
    private String courseName;
    private Long batchId;
    private String batchName;
    private BigDecimal totalAmount;
    private BigDecimal baseAmount;
    private String currency;
    private Boolean isActive;

    private BigDecimal admissionFeeAmount;
    private Boolean admissionNonRefundable;

    private Boolean gstApplicable;
    private BigDecimal gstPercent;
    private Boolean gstIncludedInFee;

    private Integer installmentCount;
    private Integer durationMonths;
    private LocalDate startDate;
    private LocalDate endDate;

    private String penaltyType;
    private BigDecimal maxPenaltyCap;
    private BigDecimal penaltyPercentage;
    private BigDecimal fixedPenaltyAmount;

    private String discountType;
    private BigDecimal discountValue;

    private List<ComponentDTO> components;
    private List<SlabDTO> slabs;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentDTO {
        private Long id;
        private String name;
        private Long feeTypeId;
        private BigDecimal amount;
        private java.time.LocalDate dueDate;
        private Boolean refundable;
        private Boolean installmentAllowed;
        private Boolean mandatory;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlabDTO {
        private Long id;
        private Integer fromDay;
        private Integer toDay;
        private String type; // FIXED / PER_DAY
        private BigDecimal value;
    }
}
