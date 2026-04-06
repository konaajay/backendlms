package com.lms.www.fee.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.payment.entity.PaymentNotification;
import java.util.List;

@Repository
public interface PaymentNotificationRepository extends JpaRepository<PaymentNotification, Long> {
    List<PaymentNotification> findByUserId(Long userId);
}
