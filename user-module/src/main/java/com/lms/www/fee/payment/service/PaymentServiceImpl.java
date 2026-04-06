package com.lms.www.fee.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.fee.config.FeeModuleConfig;
import com.lms.www.fee.dto.*;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.installment.repository.StudentInstallmentPlanRepository;
import com.lms.www.fee.payment.entity.EarlyPayment;
import com.lms.www.fee.payment.entity.PaymentMode;
import com.lms.www.fee.payment.entity.PaymentStatus;
import com.lms.www.fee.payment.entity.StudentFeePayment;
import com.lms.www.fee.payment.gateway.CashfreeGateway;
import com.lms.www.fee.payment.repository.EarlyPaymentRepository;
import com.lms.www.fee.payment.repository.StudentFeePaymentRepository;
import com.lms.www.fee.util.OrderIdGenerator;
import com.lms.www.security.UserContext;
import com.lms.www.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final StudentFeePaymentRepository paymentRepository;
    private final StudentFeeAllocationRepository allocationRepository;
    private final StudentInstallmentPlanRepository installmentRepository;
    private final EarlyPaymentRepository earlyPaymentRepository;
    private final CashfreeGateway cashfreeGateway;
    private final OrderIdGenerator orderIdGenerator;
    private final FeeModuleConfig feeModuleConfig;
    private final UserContext userContext;
    private final ObjectMapper objectMapper;

    @Override
    public InitiatePaymentResponse initiatePaymentSecure(InitiatePaymentRequest request) {
        Long allocationId = java.util.Objects.requireNonNull(request.getAllocationId(), "Allocation ID must not be null");
        StudentFeeAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found"));

        BigDecimal amountToPay = allocation.getRemainingAmount();
        String orderId = orderIdGenerator.generateNormal(request.getAllocationId());

        Map<String, String> order = cashfreeGateway.createOrder(
                amountToPay,
                orderId,
                String.valueOf(allocation.getUserId()),
                allocation.getStudentName(),
                allocation.getStudentEmail(),
                null);

        return new InitiatePaymentResponse(order.get("payment_session_id"), order.get("order_id"));
    }

    @Override
    public VerifyPaymentResponse verifyPaymentSecure(VerifyPaymentRequest request) {
        syncPaymentStatus(request.getOrderId());
        
        StudentFeePayment payment = paymentRepository.findByTransactionReference(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        boolean success = payment.getPaymentStatus() == PaymentStatus.SUCCESS;
        return new VerifyPaymentResponse(
                success,
                success ? "Payment verified successfully" : "Payment pending or failed",
                payment.getPaymentSessionId(),
                payment.getCashfreeOrderId()
        );
    }

    @Override
    public String syncPaymentStatus(String orderId) {
        Map<String, Object> orderData = cashfreeGateway.verifyOrderStatus(orderId);
        if (orderData == null) return "FAILED";

        String status = (String) orderData.get("order_status");
        if ("PAID".equalsIgnoreCase(status)) {
            BigDecimal amount = new BigDecimal(orderData.get("order_amount").toString());
            finalizePayment(orderId, "SYNC", amount, "SUCCESS", LocalDateTime.now());
            return "PAID";
        }
        return status;
    }

    @Override
    public void finalizePayment(String orderId, String rawResponse, BigDecimal amount, String status, LocalDateTime time) {
        if (orderId.startsWith(feeModuleConfig.getOrderPrefix().getEarly())) {
            finalizeEarlyPayment(orderId, rawResponse, amount, status, time);
            return;
        }

        StudentFeePayment payment = paymentRepository.findByTransactionReference(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) return;

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setRawGatewayResponse(rawResponse);
        payment.setGatewayAmount(amount);
        payment.setGatewayPaymentStatus(status);
        payment.setGatewayPaymentTime(time);
        paymentRepository.save(payment);

        Long allocationId = payment.getStudentFeeAllocationId();
        Objects.requireNonNull(allocationId, "Allocation ID cannot be null");
        StudentFeeAllocation allocation = allocationRepository.findById(allocationId).orElseThrow();
        allocation.setRemainingAmount(allocation.getRemainingAmount().subtract(amount));
        if (allocation.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            allocation.setStatus(StudentFeeAllocation.AllocationStatus.COMPLETED);
        }
        allocationRepository.save(allocation);
    }

    private void finalizeEarlyPayment(String cashfreeOrderId, String rawResponse, BigDecimal gatewayAmount, String gatewayPaymentStatus, LocalDateTime gatewayTime) {
        EarlyPayment earlyPayment = earlyPaymentRepository.findByCashfreeOrderId(cashfreeOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Early payment not found"));

        if (earlyPayment.getStatus() == EarlyPayment.EarlyPaymentStatus.PAID) return;

        earlyPayment.setStatus(EarlyPayment.EarlyPaymentStatus.PAID);
        earlyPaymentRepository.save(earlyPayment);

        List<Long> installmentIds = earlyPayment.getInstallmentIds();
        Objects.requireNonNull(installmentIds, "Installment IDs cannot be null");
        List<StudentInstallmentPlan> installments = installmentRepository.findAllById(installmentIds);
        for (StudentInstallmentPlan inst : installments) {
            inst.setPaidAmount(inst.getInstallmentAmount());
            inst.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID);
        }
        installmentRepository.saveAll(installments);

        if (!installments.isEmpty()) {
            Long allocationId = java.util.Objects.requireNonNull(installments.get(0).getStudentFeeAllocationId(), "Allocation ID must not be null");
            StudentFeeAllocation allocation = allocationRepository.findById(allocationId).orElseThrow();
            BigDecimal totalReduction = installments.stream().map(StudentInstallmentPlan::getInstallmentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            allocation.setRemainingAmount(allocation.getRemainingAmount().subtract(totalReduction));
            allocationRepository.save(allocation);
        }
    }

    @Override
    public EarlyPaymentResponse generateEarlyPaymentLink(GenerateEarlyPaymentRequest request, Long studentId) {
        List<Long> installmentIds = request.getInstallmentIds();
        Objects.requireNonNull(installmentIds, "Installment IDs cannot be null");
        List<StudentInstallmentPlan> installments = installmentRepository.findAllById(installmentIds);
        BigDecimal totalOriginal = installments.stream().map(StudentInstallmentPlan::getInstallmentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal finalAmount = totalOriginal;
        if (request.getDiscountValue() != null) {
            finalAmount = totalOriginal.subtract(request.getDiscountValue());
        }

        String orderId = orderIdGenerator.generateEarly(studentId);
        List<StudentFeeAllocation> allocations = allocationRepository.findByUserId(studentId);
        if (allocations.isEmpty()) throw new ResourceNotFoundException("No allocations found for student");
        StudentFeeAllocation allocation = allocations.get(0);

        Map<String, String> order = cashfreeGateway.createOrder(finalAmount, orderId, String.valueOf(studentId), allocation.getStudentName(), allocation.getStudentEmail(), null);

        EarlyPayment ep = EarlyPayment.builder()
                .studentId(studentId)
                .installmentIds(request.getInstallmentIds())
                .totalOriginalAmount(totalOriginal)
                .finalAmount(finalAmount)
                .cashfreeOrderId(orderId)
                .paymentSessionId(order.get("payment_session_id"))
                .status(EarlyPayment.EarlyPaymentStatus.CREATED)
                .linkCreatedAt(LocalDateTime.now())
                .linkExpiry(LocalDateTime.now().plusHours(24))
                .build();
        
        earlyPaymentRepository.save(ep);
        
        installments.forEach(i -> i.setStatus(StudentInstallmentPlan.InstallmentStatus.LOCKED_FOR_EARLY_PAYMENT));
        installmentRepository.saveAll(installments);

        return FeeMapper.toResponse(ep);
    }

    @Override
    public EarlyPaymentResponse generateFullPaymentLink(GenerateEarlyPaymentRequest request, Long studentId, Long allocationId) {
        List<StudentInstallmentPlan> unpaid = installmentRepository.findByStudentFeeAllocationId(allocationId).stream()
                .filter(i -> i.getStatus() != StudentInstallmentPlan.InstallmentStatus.PAID)
                .collect(Collectors.toList());
        
        request.setInstallmentIds(unpaid.stream().map(StudentInstallmentPlan::getId).toList());
        return generateEarlyPaymentLink(request, studentId);
    }

    @Override
    public StudentFeePaymentResponse recordManual(CreatePaymentRequest request) {
        StudentFeePayment p = new StudentFeePayment();
        p.setStudentFeeAllocationId(request.getAllocationId());
        p.setStudentInstallmentPlanId(request.getInstallmentId());
        p.setPaidAmount(request.getAmount());
        p.setPaymentDate(LocalDateTime.now());
        p.setPaymentMode(PaymentMode.valueOf(request.getPaymentMode().toUpperCase()));
        p.setPaymentStatus(PaymentStatus.SUCCESS);
        p.setTransactionReference(request.getTransactionReference() != null ? request.getTransactionReference() : orderIdGenerator.generateManual());
        p.setRecordedBy(userContext.getCurrentUserId());
        
        p = paymentRepository.save(p);

        StudentFeeAllocation allocation = allocationRepository.findById(request.getAllocationId()).orElseThrow();
        allocation.setRemainingAmount(allocation.getRemainingAmount().subtract(request.getAmount()));
        allocationRepository.save(allocation);

        return FeeMapper.toResponse(p, allocation);
    }

    @Override
    public StudentFeePaymentResponse getPaymentSecure(Long id) {
        StudentFeePayment p = paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return FeeMapper.toResponse(p, allocationRepository.findById(p.getStudentFeeAllocationId()).orElse(null));
    }

    @Override
    public List<StudentFeePaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(p -> FeeMapper.toResponse(p, allocationRepository.findById(p.getStudentFeeAllocationId()).orElse(null)))
                .toList();
    }

    @Override
    public List<StudentFeePaymentResponse> getAllWithAllocation() {
        return getAllPayments();
    }

    @Override
    public List<StudentFeePaymentResponse> getByAllocation(Long allocationId) {
        return paymentRepository.findByStudentFeeAllocationId(allocationId).stream()
                .map(p -> FeeMapper.toResponse(p, allocationRepository.findById(p.getStudentFeeAllocationId()).orElse(null)))
                .toList();
    }

    @Override
    public List<StudentFeePaymentResponse> getByStudent(Long studentId) {
        return paymentRepository.findAll().stream()
                .filter(p -> {
                    StudentFeeAllocation a = allocationRepository.findById(p.getStudentFeeAllocationId()).orElse(null);
                    return a != null && Objects.equals(a.getUserId(), studentId);
                })
                .map(p -> FeeMapper.toResponse(p, allocationRepository.findById(p.getStudentFeeAllocationId()).orElse(null)))
                .toList();
    }

    @Override
    public BulkCalculationResponse calculateBulkSecure(BulkRequest request) {
        return new BulkCalculationResponse();
    }

    @Override
    public BulkPaymentResponse processBulkSecure(BulkProcessRequest request) {
        return new BulkPaymentResponse();
    }

    @Override
    public void releaseEarlyPaymentLocks(Long earlyPaymentId) {
        Objects.requireNonNull(earlyPaymentId, "Early payment ID cannot be null");
        EarlyPayment ep = earlyPaymentRepository.findById(earlyPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Early payment not found"));
        
        List<Long> idsToUnlock = ep.getInstallmentIds();
        if (idsToUnlock != null && !idsToUnlock.isEmpty()) {
            List<StudentInstallmentPlan> installments = installmentRepository.findAllById(idsToUnlock);
            installments.forEach(i -> i.setStatus(StudentInstallmentPlan.InstallmentStatus.PENDING));
            installmentRepository.saveAll(installments);
        }
        
        ep.setStatus(EarlyPayment.EarlyPaymentStatus.FAILED);
        earlyPaymentRepository.save(ep);
    }

    @Override
    public void autoUnlockExpiredEarlyPayments() {
        // Implementation for unlocking expired links
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        if (!cashfreeGateway.verifyWebhookSignature(payload, signature)) {
            log.warn("Invalid webhook signature received");
            return;
        }

        try {
            Map<String, Object> body = objectMapper.readValue(payload, Map.class);
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            Map<String, Object> order = (Map<String, Object>) data.get("order");
            Map<String, Object> paymentData = (Map<String, Object>) data.get("payment");

            String orderId = (String) order.get("order_id");
            String status = (String) paymentData.get("payment_status");
            String txnId = String.valueOf(paymentData.get("cf_payment_id"));
            BigDecimal amount = new BigDecimal(String.valueOf(paymentData.get("payment_amount")));
            
            log.info("Processing webhook for OrderId: {}, Status: {}", orderId, status);

            if ("SUCCESS".equalsIgnoreCase(status)) {
                processSuccess(orderId, txnId, amount, payload);
            } else if ("FAILED".equalsIgnoreCase(status) || "USER_DROPPED".equalsIgnoreCase(status)) {
                processFailure(orderId, payload);
            }

        } catch (Exception e) {
            log.error("Failed to process webhook: {}", e.getMessage());
        }
    }

    private void processSuccess(String orderId, String gatewayTxnId, BigDecimal gatewayAmount, String rawResponse) {
        StudentFeePayment payment = paymentRepository.findByCashfreeOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            return;
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionReference(gatewayTxnId);
        payment.setRawGatewayResponse(rawResponse);
        payment.setGatewayAmount(gatewayAmount);
        payment.setGatewayPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        StudentFeeAllocation allocation = allocationRepository.findById(payment.getStudentFeeAllocationId()).orElseThrow();
        allocation.setRemainingAmount(allocation.getRemainingAmount().subtract(payment.getPaidAmount()));
        allocationRepository.save(allocation);

        if (payment.getInstallments() != null) {
            payment.getInstallments().forEach(i -> i.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID));
            installmentRepository.saveAll(payment.getInstallments());
        }

        log.info("Payment SUCCESS for OrderId: {}", orderId);
    }

    private void processFailure(String orderId, String rawResponse) {
        paymentRepository.findByCashfreeOrderId(orderId).ifPresent(payment -> {
            if (payment.getPaymentStatus() == PaymentStatus.PENDING) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setRawGatewayResponse(rawResponse);
                paymentRepository.save(payment);
                log.info("Payment FAILED for OrderId: {}", orderId);
            }
        });
    }

    @Override
    public byte[] getReceiptSecure(Long paymentId) {
        return new byte[0];
    }

    @Override
    public List<StudentFeePaymentResponse> getMyPayments() {
        return getByStudent(userContext.getCurrentUserId());
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public InstallmentPaymentResponse getPublicPaymentInfo(String orderId) {
        return null;
    }

    @Override
    public List<EarlyPaymentResponse> getAllEarlyPayments() {
        return earlyPaymentRepository.findAll().stream()
                .map(FeeMapper::toResponse)
                .toList();
    }

    @Override
    public List<EarlyPaymentResponse> getActiveEarlyPaymentsSecure(Long studentId) {
        return earlyPaymentRepository.findByStudentId(studentId).stream()
                .filter(ep -> ep.getStatus() == EarlyPayment.EarlyPaymentStatus.CREATED)
                .map(FeeMapper::toResponse)
                .toList();
    }

    @Override
    public boolean syncEarlyPaymentStatus(String orderId) {
        return "PAID".equals(syncPaymentStatus(orderId));
    }

    @Override
    public StudentFeePayment getById(Long id) {
        return paymentRepository.findById(id).orElseThrow();
    }
}
