package com.lms.www.fee.payment.event;

import com.lms.www.fee.payment.entity.StudentFeePayment;
import com.lms.www.fee.service.EmailService;
import com.lms.www.fee.service.PDFReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeeEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final PDFReceiptService pdfReceiptService;
    private final EmailService emailService;
    private final com.lms.www.fee.ledger.service.AuditLogService auditLogService;
    private final com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository allocationRepository;

    public void publishPaymentSuccess(StudentFeePayment payment) {
        log.info("event=PAYMENT_FINALIZED orderId={} allocationId={} status=PUBLISHING", 
            payment.getCashfreeOrderId(), payment.getStudentFeeAllocationId());
        eventPublisher.publishEvent(new PaymentSuccessEvent(payment));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccessAsync(PaymentSuccessEvent event) {
        StudentFeePayment payment = event.getPayment();
        log.info("event=PAYMENT_ASYNC_START orderId={} status=COMMITTED", payment.getCashfreeOrderId());
        
        try {
            com.lms.www.fee.allocation.entity.StudentFeeAllocation allocation = 
                allocationRepository.findById(payment.getStudentFeeAllocationId()).orElse(null);
            
            if (allocation == null) {
                log.error("event=PAYMENT_ASYNC_FAILURE orderId={} error=ALLOCATION_NOT_FOUND", payment.getCashfreeOrderId());
                return;
            }

            // 1. Generate Receipt
            byte[] pdf = pdfReceiptService.generateReceiptPDF(
                "REC-" + payment.getId(),
                allocation.getStudentName(),
                allocation,
                payment.getPaidAmount(),
                payment.getCurrency(),
                payment.getPaymentMode().name(),
                payment.getTransactionReference(),
                payment.getPaymentDate()
            );

            // 2. Dispatch Email
            emailService.sendPaymentSuccessEmail(
                allocation.getStudentEmail(),
                allocation.getStudentName(),
                "REC-" + payment.getId(),
                payment.getPaidAmount(),
                payment.getCurrency(),
                pdf
            );

            // 3. Log Audit
            auditLogService.log(
                "FEE",
                "StudentFeePayment",
                payment.getId(),
                com.lms.www.fee.ledger.entity.FeeAuditLog.Action.UPDATE,
                "PENDING",
                "SUCCESS"
            );

            log.info("event=PAYMENT_ASYNC_SUCCESS orderId={}", payment.getCashfreeOrderId());
        } catch (Exception e) {
            log.error("event=PAYMENT_ASYNC_FAILURE orderId={} error={}", payment.getCashfreeOrderId(), e.getMessage());
        }
    }

    @lombok.Getter
    @RequiredArgsConstructor
    public static class PaymentSuccessEvent {
        private final StudentFeePayment payment;
    }
}
