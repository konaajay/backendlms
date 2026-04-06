package com.lms.www.fee.payment;

import com.lms.www.fee.dto.*;
import com.lms.www.fee.payment.entity.StudentFeePayment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    // --- Core Payments ---
    StudentFeePaymentResponse getPaymentSecure(Long id);

    List<StudentFeePaymentResponse> getAllWithAllocation();

    InitiatePaymentResponse initiatePaymentSecure(InitiatePaymentRequest request);

    VerifyPaymentResponse verifyPaymentSecure(VerifyPaymentRequest request);

    byte[] getReceiptSecure(Long paymentId);

    String syncPaymentStatus(String orderId);

    InstallmentPaymentResponse getPublicPaymentInfo(String orderId);

    void handleWebhook(String payload, String signature);

    // --- Bulk Payments ---
    BulkCalculationResponse calculateBulkSecure(BulkRequest request);

    BulkPaymentResponse processBulkSecure(BulkProcessRequest request);

    // --- Refunds ---
    RefundResponse createRefund(RefundRequest request);

    List<RefundResponse> getMyRefunds();

    List<RefundResponse> getAllRefunds();

    List<RefundResponse> getRefundsByAllocation(Long allocationId);

    RefundResponse approveRefund(Long id);

    RefundResponse rejectRefund(Long id, String reason);

    void deleteRefundRequest(Long id);

    // --- Early Payments ---
    List<EarlyPaymentResponse> getEarlyPaymentsMe();

    List<EarlyPaymentResponse> getAllEarlyPayments();

    EarlyPaymentResponse generateEarlyPaymentLink(Long studentId, List<Long> installmentIds, String discountType,
            BigDecimal discountValue);

    EarlyPaymentResponse generateFullPaymentLink(Long studentId, Long allocationId, String discountType,
            BigDecimal discountValue);

    void releaseLocks(Long id);

    boolean syncEarlyPaymentStatus(String orderId);

    // --- Administrative / Internal ---
    StudentFeePayment recordManualPayment(Long allocationId, Long installmentPlanId, BigDecimal amount,
            String paymentMode, String transactionRef, String screenshotUrl, Long recordedBy,
            String studentName, String studentEmail, List<Double> discountPercentages,
            BigDecimal manualDiscount, String remarks);

    void finalizePaymentOnWebhookSuccess(String cashfreeOrderId, String rawResponse, BigDecimal gatewayAmount,
            String gatewayPaymentStatus, LocalDateTime gatewayTime);

    void finalizeEarlyPaymentOnWebhookSuccess(String cashfreeOrderId, String rawResponse,
            BigDecimal gatewayAmount, String gatewayPaymentStatus, LocalDateTime gatewayTime);
}
