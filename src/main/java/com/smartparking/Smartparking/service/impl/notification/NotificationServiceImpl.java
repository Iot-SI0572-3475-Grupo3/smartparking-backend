package com.smartparking.Smartparking.service.impl.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.notification.Notification;
import com.smartparking.Smartparking.entity.notification.NotificationPreference;
import com.smartparking.Smartparking.entity.notification.NotificationQueue;
import com.smartparking.Smartparking.entity.notification.NotificationTemplate;
import com.smartparking.Smartparking.repository.notification.*;
import com.smartparking.Smartparking.service.notification.NotificationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPreferenceRepository preferenceRepo;
    private final NotificationTemplateRepository templateRepo;
    private final NotificationRepository notificationRepo;
    private final NotificationQueueRepository queueRepo;
    private final UserDeviceTokenRepository tokenRepo;
    @PersistenceContext  // ← CORRECTO (no @Autowired)
    private EntityManager entityManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendIfEnabled(String userId, NotificationPreference.NotificationType type, Map<String, Object> data) {
        Optional<NotificationPreference> prefOpt = preferenceRepo
                .findByUser_UserIdAndNotificationType(userId, type);

        if (prefOpt.isEmpty() || !Boolean.TRUE.equals(prefOpt.get().getIsEnabled())) {
            log.debug("Notificación {} deshabilitada para usuario {}", type, userId);
            return;
        }

        NotificationPreference pref = prefOpt.get();

        // Respetar horarios silenciosos
        if (!isWithinQuietHours(pref)) {
            log.debug("Usuario {} en horario silencioso", userId);
            return;
        }

        // Buscar plantilla activa
        NotificationTemplate template = templateRepo
                .findByTypeAndIsActiveTrue(NotificationTemplate.Type.valueOf(type.name()))
                .orElse(null);

        if (template == null) {
            log.warn("No hay plantilla activa para tipo: {}", type);
            return;
        }

        // Renderizar
        String title = render(template.getTitleTemplate(), data);
        String message = render(template.getMessageTemplate(), data);

        // Crear notificación
        Notification notif = new Notification();
        notif.setUser(entityManager.getReference(User.class, userId));
        String channelStr = pref.getChannel().trim().toUpperCase();
        Notification.Channel channel = Notification.Channel.valueOf(channelStr);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setType(Notification.Type.valueOf(type.name()));
        notif.setChannel(channel);
        notif.setPriority(Notification.Priority.medium);
        notif.setStatus(Notification.Status.pending);
        notif.setMetadata(toJson(data));
        notificationRepo.save(notif);

        // Encolar
        NotificationQueue queue = new NotificationQueue();
        queue.setNotification(notif);
        queue.setPriority(NotificationQueue.Priority.medium);
        queue.setStatus(NotificationQueue.Status.pending);
        queue.setScheduledAt(LocalDateTime.now());
        queueRepo.save(queue);

        log.info("Notificación {} encolada para usuario {}", type, userId);
    }

    @Override
    public void sendToMultipleUsers(List<String> userIds, NotificationPreference.NotificationType type, Map<String, Object> data) {
        for (String userId : userIds) {
            try {
                sendIfEnabled(userId, type, data);
            } catch (Exception e) {
                log.error("Error enviando notificación a usuario {}: {}", userId, e.getMessage());
            }
        }
    }

    // === UTILIDADES ===

    private boolean isWithinQuietHours(NotificationPreference pref) {
        LocalTime start = pref.getQuietHoursStart();
        LocalTime end = pref.getQuietHoursEnd();
        if (start == null || end == null) return true;

        LocalTime now = LocalTime.now();
        if (start.isBefore(end)) {
            return !(now.isAfter(start) && now.isBefore(end));
        } else {
            return !(now.isAfter(start) || now.isBefore(end));
        }
    }

    private String render(String template, Map<String, Object> data) {
        String result = template;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    private String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("Error serializando metadata: {}", e.getMessage());
            return "{}";
        }
    }
}