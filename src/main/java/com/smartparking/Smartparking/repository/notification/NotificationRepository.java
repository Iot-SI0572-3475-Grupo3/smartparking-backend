package com.smartparking.Smartparking.repository.notification;

import com.smartparking.Smartparking.entity.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    Page<Notification> findByUser_UserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Optional<Notification> findByNotificationIdAndUser_UserId(String notificationId, String userId);
}