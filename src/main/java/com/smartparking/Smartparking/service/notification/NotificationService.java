package com.smartparking.Smartparking.service.notification;


import com.smartparking.Smartparking.entity.notification.NotificationPreference;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    void sendIfEnabled(String userId, NotificationPreference.NotificationType type, Map<String, Object> data);

    void sendToMultipleUsers(List<String> userIds, NotificationPreference.NotificationType type, Map<String, Object> data);
}