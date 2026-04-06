package com.lms.www.fee.payment.job;

import com.lms.www.fee.payment.entity.StudentFeePayment;
import com.lms.www.fee.payment.entity.PaymentStatus;
import com.lms.www.fee.payment.repository.StudentFeePaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentExpiryJob {

    private final StudentFeePaymentRepository paymentRepository;

    /**
     * Runs every hour to mark PENDING payments older than 24 hours as FAILED.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireStalePayments() {
        log.info("Starting PaymentExpiryJob...");
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        
        List<StudentFeePayment> stalePayments = paymentRepository.findByPaymentStatusAndPaymentDateBefore(
                PaymentStatus.PENDING, 
                threshold
        );

        if (stalePayments.isEmpty()) {
            log.info("No stale PENDING payments found.");
            return;
        }

        log.info("Found {} stale PENDING payments to expire.", stalePayments.size());
        
        for (StudentFeePayment payment : stalePayments) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }
        
        paymentRepository.saveAll(stalePayments);
        log.info("PaymentExpiryJob completed successfully.");
    }
}
