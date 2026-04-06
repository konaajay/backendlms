package com.lms.www.fee.service;

import com.lms.www.fee.dto.InstallmentPlanRequest;
import com.lms.www.fee.dto.InstallmentPlanResponse;
import com.lms.www.fee.dto.InstallmentUpdateRequest;
import java.util.List;

public interface StudentInstallmentPlanService {

    List<InstallmentPlanResponse> createInstallments(Long allocationId, List<InstallmentPlanRequest> requests);

    List<InstallmentPlanResponse> resetInstallments(Long allocationId, List<InstallmentPlanRequest> requests);

    List<InstallmentPlanResponse> getByAllocation(Long allocationId);

    List<InstallmentPlanResponse> getByAllocationSecure(Long allocationId);

    List<InstallmentPlanResponse> getOverdueInstallments();

    InstallmentPlanResponse extendDueDate(Long id, InstallmentUpdateRequest request);

    List<InstallmentPlanResponse> getInstallmentsByBatchId(Long batchId);

    void deleteInstallment(Long id);

    // Internal or specialized methods
    com.lms.www.fee.installment.entity.StudentInstallmentPlan generatePaymentLink(Long id);
    
    List<com.lms.www.fee.installment.entity.StudentInstallmentPlan> generateInstallmentsFromStructure(
            com.lms.www.fee.allocation.entity.StudentFeeAllocation allocation);

    InstallmentPlanResponse getById(Long id);

    void updateStatus(Long id, String status);

    void lockForEarlyPayment(Long id);
}