package com.smartparking.Smartparking.repository.notification;

import com.smartparking.Smartparking.entity.notification.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByTypeAndIsActiveTrue(NotificationTemplate.Type type);
}