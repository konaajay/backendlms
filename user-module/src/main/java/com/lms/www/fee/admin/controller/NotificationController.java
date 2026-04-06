package com.lms.www.fee.admin.controller;

import com.lms.www.fee.dto.NotificationRequest;
import com.lms.www.fee.dto.NotificationResponse;
import com.lms.www.fee.service.NotificationService;
import com.lms.www.security.UserContext;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserContext userContext;

    @PostMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_CREATE')")
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createNotification(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_VIEW')")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('NOTIFICATION_VIEW_SELF')")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getNotificationsByUserIdSecure(userContext.getCurrentUserId()));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('NOTIFICATION_VIEW_ADMIN')")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public ResponseEntity<NotificationResponse> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_DELETE')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send/payment-success")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND_PAYMENT')")
    public ResponseEntity<Void> sendPaymentSuccess(
            @RequestParam Long userId,
            @RequestParam Long paymentId,
            @RequestParam String email) {
        notificationService.sendPaymentSuccessNotification(userId, paymentId, email);
        return ResponseEntity.ok().build();
    }
}
