package com.smartparking.Smartparking.repository.notification;

import com.smartparking.Smartparking.entity.notification.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, String> {
    List<NotificationPreference> findByUser_UserId(String userId);

    Optional<NotificationPreference> findByUser_UserIdAndNotificationType(
            String userId, NotificationPreference.NotificationType type);
}