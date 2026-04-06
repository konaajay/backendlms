package com.lms.www.marketing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.marketing.model.PushNotification;

public interface PushNotificationRepository extends JpaRepository<PushNotification, Long> {
    List<PushNotification> findAllByOrderByCreatedAtDesc();
}
