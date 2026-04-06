package com.lms.www.fee.payment.service;

import com.lms.www.fee.dto.InitiatePaymentRequest;
import com.lms.www.fee.dto.InitiatePaymentResponse;
import com.lms.www.fee.payment.entity.StudentFeePayment;

public interface PaymentInitiateService {
    /**
     * Initiates a payment by creating a PENDING record in the DB before calling the gateway.
     * Ensures idempotency: if an active PENDING payment already exists for this allocation, returns it.
     */
    InitiatePaymentResponse initiate(InitiatePaymentRequest request);
    
    /**
     * Specialized initiation for early/bulk payments.
     */
    InitiatePaymentResponse initiateEarly(InitiatePaymentRequest request);
    
    /**
     * Finds an existing active PENDING payment for an allocation to prevent duplicates.
     */
    StudentFeePayment findActivePending(Long allocationId);
}
