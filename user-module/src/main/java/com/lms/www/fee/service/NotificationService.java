package com.lms.www.fee.service;

import com.lms.www.fee.dto.NotificationRequest;
import com.lms.www.fee.dto.NotificationResponse;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import java.math.BigDecimal;
import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    List<NotificationResponse> getAllNotifications();
    List<NotificationResponse> getNotificationsByUserId(Long userId);
    List<NotificationResponse> getNotificationsByUserIdSecure(Long userId);
    NotificationResponse updateNotification(Long id, NotificationRequest request);
    void deleteNotification(Long id);
    
    void sendPaymentSuccessNotification(Long userId, Long paymentId, String email);
    void sendEnrollmentNotification(StudentFeeAllocation allocation, List<StudentInstallmentPlan> installments, BigDecimal penalty);
}
