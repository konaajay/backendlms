package com.lms.www.fee.payment.service;

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
import com.lms.www.common.exception.ResourceNotFoundException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Font;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public InitiatePaymentResponse initiatePaymentSecure(InitiatePaymentRequest request) {
        if (request.getAllocationId() == null) throw new IllegalArgumentException("Allocation ID required");
        StudentFeeAllocation allocation = allocationRepository.findById(request.getAllocationId())
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

        StudentFeePayment p = new StudentFeePayment();
        p.setStudentFeeAllocationId(request.getAllocationId());
        p.setPaidAmount(amountToPay);
        p.setPaymentDate(LocalDateTime.now());
        p.setPaymentMode(PaymentMode.ONLINE);
        p.setPaymentStatus(PaymentStatus.PENDING);
        p.setTransactionReference(orderId);
        p.setCashfreeOrderId(orderId);
        p.setPaymentSessionId(order.get("payment_session_id"));
        paymentRepository.save(p);

        return new InitiatePaymentResponse(order.get("payment_session_id"), order.get("order_id"));
    }

    @Override
    public VerifyPaymentResponse verifyPaymentSecure(VerifyPaymentRequest request) {
        syncPaymentStatus(request.getOrderId());
        StudentFeePayment payment = paymentRepository.findByTransactionReference(request.getOrderId()).orElseThrow();
        boolean success = payment.getPaymentStatus() == PaymentStatus.SUCCESS;
        return new VerifyPaymentResponse(success, success ? "Verified" : "Pending", payment.getPaymentSessionId(), payment.getCashfreeOrderId());
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
        StudentFeePayment payment = paymentRepository.findByTransactionReference(orderId).orElseThrow();
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) return;
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAmount(amount);
        paymentRepository.save(payment);
        StudentFeeAllocation allocation = allocationRepository.findById(payment.getStudentFeeAllocationId()).orElseThrow();
        allocation.setRemainingAmount(allocation.getRemainingAmount().subtract(amount));
        allocationRepository.save(allocation);
    }

    private void finalizeEarlyPayment(String cashfreeOrderId, String rawResponse, BigDecimal gatewayAmount, String gatewayPaymentStatus, LocalDateTime gatewayTime) {
        EarlyPayment earlyPayment = earlyPaymentRepository.findByCashfreeOrderId(cashfreeOrderId).orElseThrow();
        if (earlyPayment.getStatus() == EarlyPayment.EarlyPaymentStatus.PAID) return;
        earlyPayment.setStatus(EarlyPayment.EarlyPaymentStatus.PAID);
        earlyPaymentRepository.save(earlyPayment);
        paymentRepository.findByCashfreeOrderId(cashfreeOrderId).ifPresent(p -> {
            p.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(p);
        });
        List<StudentInstallmentPlan> installments = installmentRepository.findAllById(earlyPayment.getInstallmentIds());
        for (StudentInstallmentPlan inst : installments) {
            inst.setPaidAmount(inst.getInstallmentAmount());
            inst.setStatus(StudentInstallmentPlan.InstallmentStatus.PAID);
        }
        installmentRepository.saveAll(installments);
        if (!installments.isEmpty()) {
            StudentFeeAllocation a = allocationRepository.findById(installments.get(0).getStudentFeeAllocationId()).orElseThrow();
            BigDecimal totalReduction = installments.stream().map(StudentInstallmentPlan::getInstallmentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            a.setRemainingAmount(a.getRemainingAmount().subtract(totalReduction));
            allocationRepository.save(a);
        }
    }

    @Override
    public EarlyPaymentResponse generateEarlyPaymentLink(GenerateEarlyPaymentRequest request, Long studentId) {
        List<StudentInstallmentPlan> installments = installmentRepository.findAllById(request.getInstallmentIds());
        BigDecimal totalOriginal = installments.stream().map(StudentInstallmentPlan::getInstallmentAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal finalAmount = totalOriginal;
        BigDecimal discountValue = request.getDiscountValue() != null ? request.getDiscountValue() : BigDecimal.ZERO;
        if (discountValue.compareTo(BigDecimal.ZERO) > 0) {
            if (request.getDiscountType() == EarlyPayment.DiscountType.PERCENT) {
                BigDecimal discount = totalOriginal.multiply(discountValue).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                finalAmount = totalOriginal.subtract(discount);
            } else {
                finalAmount = totalOriginal.subtract(discountValue);
            }
        }
        String orderId = orderIdGenerator.generateEarly(studentId);
        StudentFeeAllocation allocation = allocationRepository.findByUserId(studentId).get(0);
        Map<String, String> order = cashfreeGateway.createOrder(finalAmount, orderId, String.valueOf(studentId), allocation.getStudentName(), allocation.getStudentEmail(), null);
        EarlyPayment ep = EarlyPayment.builder().studentId(studentId).installmentIds(request.getInstallmentIds()).totalOriginalAmount(totalOriginal).discountType(request.getDiscountType()).discountValue(discountValue).finalAmount(finalAmount).cashfreeOrderId(orderId).paymentSessionId(order.get("payment_session_id")).status(EarlyPayment.EarlyPaymentStatus.CREATED).linkCreatedAt(LocalDateTime.now()).linkExpiry(LocalDateTime.now().plusHours(24)).build();
        earlyPaymentRepository.save(ep);
        StudentFeePayment p = new StudentFeePayment();
        p.setStudentFeeAllocationId(allocation.getId());
        p.setPaidAmount(finalAmount);
        p.setPaymentDate(LocalDateTime.now());
        p.setPaymentStatus(PaymentStatus.PENDING);
        p.setCashfreeOrderId(orderId);
        p.setPaymentSessionId(order.get("payment_session_id"));
        paymentRepository.save(p);
        return FeeMapper.toResponse(ep);
    }

    @Override
    public byte[] getReceiptSecure(Long paymentId) {
        StudentFeePayment payment = paymentRepository.findById(paymentId).orElseThrow();
        StudentFeeAllocation allocation = allocationRepository.findById(payment.getStudentFeeAllocationId()).orElseThrow();
        try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph header = new Paragraph("LMS - PAYMENT RECEIPT", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph("Official Payment Proof", smallFont));
            document.add(new Paragraph(" ")); 
            document.add(new Paragraph("______________________________________________________________________________"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Receipt Number: " + (payment.getTransactionReference() != null ? payment.getTransactionReference() : payment.getCashfreeOrderId()), subHeaderFont));
            document.add(new Paragraph("STUDENT DETAILS", subHeaderFont));
            document.add(new Paragraph("Name: " + allocation.getStudentName(), normalFont));
            document.add(new Paragraph("Course: " + allocation.getCourseName(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("PAYMENT DETAILS", subHeaderFont));
            BigDecimal finalAmount = payment.getPaidAmount();
            Optional<EarlyPayment> earlyOpt = earlyPaymentRepository.findByCashfreeOrderId(payment.getCashfreeOrderId());
            if (earlyOpt.isPresent()) {
                EarlyPayment ep = earlyOpt.get();
                if (ep.getDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
                    document.add(new Paragraph("Original Amount: " + allocation.getCurrency() + " " + ep.getTotalOriginalAmount(), normalFont));
                    BigDecimal discountAmt = ep.getTotalOriginalAmount().subtract(ep.getFinalAmount());
                    document.add(new Paragraph("Discount Applied: - " + allocation.getCurrency() + " " + discountAmt, normalFont));
                }
            }
            document.add(new Paragraph("Net Amount Paid: " + allocation.getCurrency() + " " + finalAmount, subHeaderFont));
            document.add(new Paragraph("Payment Status: " + payment.getPaymentStatus(), normalFont));
            document.add(new Paragraph(" "));
            
            Paragraph footer = new Paragraph("Thank you for your payment. This is a system-generated invoice.", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            return ("Error generating receipt").getBytes();
        }
    }

    @Override public EarlyPaymentResponse generateFullPaymentLink(GenerateEarlyPaymentRequest r, Long s, Long a) { return generateEarlyPaymentLink(r, s); }
    @Override public StudentFeePaymentResponse recordManual(CreatePaymentRequest r) { return null; }
    @Override public StudentFeePaymentResponse getPaymentSecure(Long id) { return null; }
    @Override public List<StudentFeePaymentResponse> getAllPayments() { return null; }
    @Override public List<StudentFeePaymentResponse> getAllWithAllocation() { return null; }
    @Override public List<StudentFeePaymentResponse> getByAllocation(Long a) { return null; }
    @Override public List<StudentFeePaymentResponse> getByStudent(Long s) { return null; }
    @Override public BulkCalculationResponse calculateBulkSecure(BulkRequest r) { return null; }
    @Override public BulkPaymentResponse processBulkSecure(BulkProcessRequest r) { return null; }
    @Override public void releaseEarlyPaymentLocks(Long e) {}
    @Override public void autoUnlockExpiredEarlyPayments() {}
    @Override public void handleWebhook(String p, String s) {}
    @Override public List<StudentFeePaymentResponse> getMyPayments() { return null; }
    @Override public void deletePayment(Long id) {}
    @Override public InstallmentPaymentResponse getPublicPaymentInfo(String o) { return null; }
    @Override public List<EarlyPaymentResponse> getAllEarlyPayments() { return null; }
    @Override public List<EarlyPaymentResponse> getActiveEarlyPaymentsSecure(Long s) { return null; }
    @Override public boolean syncEarlyPaymentStatus(String o) { return false; }
    @Override public StudentFeePayment getById(Long id) { return paymentRepository.findById(id).orElseThrow(); }
}
