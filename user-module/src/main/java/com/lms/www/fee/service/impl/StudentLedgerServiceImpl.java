package com.lms.www.fee.service.impl;

import com.lms.www.fee.dto.*;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.installment.repository.StudentInstallmentPlanRepository;
import com.lms.www.fee.payment.repository.StudentFeePaymentRepository;
import com.lms.www.fee.service.StudentFeeAllocationService;
import com.lms.www.fee.service.StudentLedgerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentLedgerServiceImpl implements StudentLedgerService {

    private final StudentFeeAllocationService allocationService;
    private final StudentInstallmentPlanRepository installmentRepo;
    private final StudentFeePaymentRepository paymentRepo;

    public StudentLedgerServiceImpl(
            StudentFeeAllocationService allocationService,
            StudentInstallmentPlanRepository installmentRepo,
            StudentFeePaymentRepository paymentRepo
    ) {
        this.allocationService = allocationService;
        this.installmentRepo = installmentRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    public StudentLedgerResponse getLedger(Long studentId) {
        StudentFeeAllocation allocation = allocationService.getFeeAllocationById(studentId);
        
        List<StudentInstallmentPlan> installments = installmentRepo.findByStudentFeeAllocationId(allocation.getId());
        List<StudentFeePaymentResponse> payments = paymentRepo.findByStudentId(studentId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());

        return StudentLedgerResponse.builder()
                .allocationSummary(StudentLedgerResponse.AllocationSummary.builder()
                        .allocationId(allocation.getId())
                        .studentName(allocation.getStudentName())
                        .feeStructureId(allocation.getFeeStructureId())
                        .feeStructureName(allocation.getCourseName())
                        .feeTypeName(allocation.getBatchName())
                        .build())
                .totalDue(allocation.getPayableAmount())
                .totalPaid(allocation.getPaidAmount())
                .remainingBalance(allocation.getRemainingAmount())
                .installments(installments.stream()
                        .map(i -> StudentLedgerResponse.InstallmentDto.builder()
                                .id(i.getId())
                                .amount(i.getInstallmentAmount())
                                .dueDate(i.getDueDate())
                                .status(i.getStatus().name())
                                .paymentLinkAvailable(i.getStatus() != StudentInstallmentPlan.InstallmentStatus.PAID)
                                .build())
                        .collect(Collectors.toList()))
                .payments(payments)
                .build();
    }

    @Override
    public StudentFeeDashboardResponse getDashboard(Long studentId) {
        StudentFeeAllocation allocation = allocationService.getFeeAllocationById(studentId);
        
        List<StudentInstallmentPlan> installments = installmentRepo.findByStudentFeeAllocationId(allocation.getId());
        
        StudentInstallmentPlan next = installments.stream()
                .filter(i -> i.getStatus() != StudentInstallmentPlan.InstallmentStatus.PAID)
                .min(Comparator.comparing(StudentInstallmentPlan::getDueDate))
                .orElse(null);

        List<StudentFeeDashboardResponse.RecentTransaction> transactions = paymentRepo.findTop5ByStudentIdOrderByPaymentDateDesc(studentId).stream()
                .map(p -> StudentFeeDashboardResponse.RecentTransaction.builder()
                        .id(p.getId())
                        .amount(p.getPaidAmount())
                        .date(p.getPaymentDate().toLocalDate())
                        .status(p.getPaymentStatus().name())
                        .mode(p.getPaymentMode().name())
                        .build())
                .collect(Collectors.<StudentFeeDashboardResponse.RecentTransaction>toList());

        return StudentFeeDashboardResponse.builder()
                .totalAllocated(allocation.getPayableAmount())
                .totalPaid(allocation.getPaidAmount())
                .totalPending(allocation.getRemainingAmount())
                .nextInstallment(next != null ? StudentFeeDashboardResponse.NextInstallment.builder()
                        .amount(next.getInstallmentAmount())
                        .dueDate(next.getDueDate())
                        .label(next.getLabel())
                        .build() : null)
                .recentTransactions(transactions)
                .build();
    }

    @Override
    public Map<String, String> createOrder(Long installmentId, Long studentId) {
        // Delegate to PaymentGatewayService or similar in a real scenario
        return Map.of("order_id", "INST_" + installmentId + "_" + System.currentTimeMillis(), "payment_session_id", "MOCK_SESSION_" + installmentId);
    }
}
