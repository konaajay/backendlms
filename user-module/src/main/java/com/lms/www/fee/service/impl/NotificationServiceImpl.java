package com.lms.www.fee.service.impl;

import com.lms.www.fee.dto.*;
import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.payment.entity.PaymentNotification;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.payment.repository.PaymentNotificationRepository;
import com.lms.www.fee.service.NotificationService;
import com.lms.www.security.UserContext;
import com.lms.www.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final PaymentNotificationRepository repo;
    private final UserContext userContext;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        PaymentNotification entity = PaymentNotification.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .build();
        return FeeMapper.toResponse(repo.save(entity));
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return repo.findAll().stream()
                .map(n -> FeeMapper.toResponse(n))
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(n -> FeeMapper.toResponse(n))
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserIdSecure(Long userId) {
        if (!userContext.isAdmin() && !userContext.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return getNotificationsByUserId(userId);
    }

    @Override
    public NotificationResponse updateNotification(Long id, NotificationRequest request) {
        PaymentNotification existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        existing.setTitle(request.getTitle());
        existing.setMessage(request.getMessage());
        return FeeMapper.toResponse(repo.save(existing));
    }

    @Override
    public void deleteNotification(Long id) {
        repo.deleteById(id);
    }

    @Override
    public void sendPaymentSuccessNotification(Long userId, Long paymentId, String email) {
        // Implementation for sending actual email/notification
    }

    @Override
    public void sendEnrollmentNotification(StudentFeeAllocation allocation, List<StudentInstallmentPlan> installments, BigDecimal penalty) {
        // Implementation for sending enrollment notification
    }
}
