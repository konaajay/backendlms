package com.lms.www.fee.installment.service;

import com.lms.www.fee.dto.InstallmentResponse;
import java.util.List;

public interface InstallmentService {
    void generateInstallments(Long allocationId);
    List<InstallmentResponse> getInstallmentsByAllocation(Long allocationId);
    InstallmentResponse getInstallmentById(Long id);
    void updateInstallmentStatus(Long id, String status);
    void lockForEarlyPayment(Long id);
}
