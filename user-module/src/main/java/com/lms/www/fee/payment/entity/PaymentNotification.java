package com.lms.www.fee.payment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "payment_notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum NotificationType {
        PAYMENT_SUCCESS, PAYMENT_FAILED, DUE_REMINDER, OVERDUE_WARNING, RECEIPT_SENT
    }

    public enum DeliveryStatus {
        PENDING, SENT, FAILED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static PaymentNotificationBuilder builder() { return new PaymentNotificationBuilder(); }
    public static class PaymentNotificationBuilder {
        private PaymentNotification n = new PaymentNotification();
        public PaymentNotificationBuilder userId(Long id) { n.userId = id; return this; }
        public PaymentNotificationBuilder title(String title) { n.title = title; return this; }
        public PaymentNotificationBuilder notificationType(NotificationType type) { n.notificationType = type; return this; }
        public PaymentNotificationBuilder message(String message) { n.message = message; return this; }
        public PaymentNotificationBuilder email(String email) { n.email = email; return this; }
        public PaymentNotificationBuilder phone(String phone) { n.phone = phone; return this; }
        public PaymentNotificationBuilder sentAt(LocalDateTime sentAt) { n.sentAt = sentAt; return this; }
        public PaymentNotificationBuilder deliveryStatus(DeliveryStatus status) { n.deliveryStatus = status; return this; }
        public PaymentNotification build() { return n; }
    }
}
