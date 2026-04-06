package com.lms.www.fee.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.lms.www.fee.dto.*;
import com.lms.www.fee.payment.entity.StudentFeePayment;

public interface PaymentService {

    // --- Core Payments (Student/Parent) ---
    InitiatePaymentResponse initiatePaymentSecure(InitiatePaymentRequest request);
    VerifyPaymentResponse verifyPaymentSecure(VerifyPaymentRequest request);
    byte[] getReceiptSecure(Long paymentId);
    StudentFeePaymentResponse getPaymentSecure(Long id);
    List<StudentFeePaymentResponse> getMyPayments();
    
    // --- Manual/Admin Payments ---
    StudentFeePaymentResponse recordManual(CreatePaymentRequest request);
    void deletePayment(Long id);
    List<StudentFeePaymentResponse> getAllPayments();
    List<StudentFeePaymentResponse> getAllWithAllocation();
    List<StudentFeePaymentResponse> getByAllocation(Long allocationId);
    List<StudentFeePaymentResponse> getByStudent(Long studentId);

    // --- Bulk Payments ---
    BulkCalculationResponse calculateBulkSecure(BulkRequest request);
    BulkPaymentResponse processBulkSecure(BulkProcessRequest request);

    // --- Early Payments ---
    EarlyPaymentResponse generateEarlyPaymentLink(GenerateEarlyPaymentRequest request, Long studentId);
    EarlyPaymentResponse generateFullPaymentLink(GenerateEarlyPaymentRequest request, Long studentId, Long allocationId);
    List<EarlyPaymentResponse> getAllEarlyPayments();
    List<EarlyPaymentResponse> getActiveEarlyPaymentsSecure(Long studentId);
    boolean syncEarlyPaymentStatus(String orderId);
    void releaseEarlyPaymentLocks(Long earlyPaymentId);
    void autoUnlockExpiredEarlyPayments();
    
    // --- Webhooks & Status Sync ---
    void handleWebhook(String payload, String signature);
    String syncPaymentStatus(String orderId);
    InstallmentPaymentResponse getPublicPaymentInfo(String orderId);
    void finalizePayment(String orderId, String rawResponse, BigDecimal amount, String status, LocalDateTime time);

    // --- Internals ---
    StudentFeePayment getById(Long id);
}
