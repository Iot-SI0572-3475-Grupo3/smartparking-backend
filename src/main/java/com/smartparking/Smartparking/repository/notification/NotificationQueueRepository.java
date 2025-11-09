package com.smartparking.Smartparking.repository.notification;

import com.smartparking.Smartparking.entity.notification.NotificationQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, String> {

    List<NotificationQueue> findByStatusAndScheduledAtBefore(
            NotificationQueue.Status status, LocalDateTime now);
}