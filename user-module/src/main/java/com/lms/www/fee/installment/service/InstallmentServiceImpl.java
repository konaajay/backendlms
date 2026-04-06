package com.lms.www.fee.installment.service;

import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.dto.InstallmentResponse;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.installment.repository.StudentInstallmentPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

//@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InstallmentServiceImpl implements InstallmentService {

    private final StudentInstallmentPlanRepository installmentPlanRepository;
    private final StudentFeeAllocationRepository allocationRepository;

    @Override
    public void generateInstallments(Long allocationId) {
        StudentFeeAllocation allocation = allocationRepository.findById(allocationId).orElseThrow();
        
        BigDecimal installmentAmount = allocation.getPayableAmount().divide(BigDecimal.valueOf(allocation.getInstallmentCount()));
        
        for (int i = 1; i <= allocation.getInstallmentCount(); i++) {
            StudentInstallmentPlan plan = StudentInstallmentPlan.builder()
                    .studentFeeAllocationId(allocationId)
                    .installmentNumber(i)
                    .installmentAmount(installmentAmount)
                    .dueDate(LocalDate.now().plusMonths(i))
                    .build();
            installmentPlanRepository.save(plan);
        }
    }

    @Override
    public List<InstallmentResponse> getInstallmentsByAllocation(Long allocationId) {
        return installmentPlanRepository.findByStudentFeeAllocationId(allocationId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public InstallmentResponse getInstallmentById(Long id) {
        return installmentPlanRepository.findById(id).map(this::mapToResponse).orElseThrow();
    }

    @Override
    public void updateInstallmentStatus(Long id, String status) {
        StudentInstallmentPlan plan = installmentPlanRepository.findById(id).orElseThrow();
        plan.setStatus(StudentInstallmentPlan.InstallmentStatus.valueOf(status));
        installmentPlanRepository.save(plan);
    }

    @Override
    public void lockForEarlyPayment(Long id) {
        StudentInstallmentPlan plan = installmentPlanRepository.findById(id).orElseThrow();
        plan.setStatus(StudentInstallmentPlan.InstallmentStatus.LOCKED_FOR_EARLY_PAYMENT);
        installmentPlanRepository.save(plan);
    }

    private InstallmentResponse mapToResponse(StudentInstallmentPlan plan) {
        return InstallmentResponse.builder()
                .id(plan.getId())
                .number(plan.getInstallmentNumber())
                .amount(plan.getInstallmentAmount())
                .paidAmount(plan.getPaidAmount())
                .dueDate(plan.getDueDate())
                .status(plan.getStatus().name())
                .build();
    }
}
