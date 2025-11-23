package com.smartparking.Smartparking.controller.notification;

import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.notification.Notification;
import com.smartparking.Smartparking.entity.notification.NotificationPreference;
import com.smartparking.Smartparking.entity.notification.UserDeviceToken;
import com.smartparking.Smartparking.repository.notification.NotificationPreferenceRepository;
import com.smartparking.Smartparking.repository.notification.NotificationRepository;
import com.smartparking.Smartparking.repository.notification.UserDeviceTokenRepository;
import com.smartparking.Smartparking.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;
    private final UserDeviceTokenRepository tokenRepository;
    private final NotificationService notificationService;

    @GetMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationPreference>> getPreferences(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(preferenceRepository.findByUser_UserId(user.getUserId()));
    }

    @PutMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationPreference>> updatePreferences(
            @AuthenticationPrincipal User user,
            @RequestBody List<NotificationPreference> updates) {

        List<NotificationPreference> saved = updates.stream()
                .map(update -> {
                    NotificationPreference pref = preferenceRepository
                            .findByUser_UserIdAndNotificationType(user.getUserId(), update.getNotificationType())
                            .orElseGet(() -> {
                                NotificationPreference np = new NotificationPreference();
                                np.setUser(user);
                                np.setNotificationType(update.getNotificationType());
                                return np;
                            });

                    pref.setIsEnabled(update.getIsEnabled());
                    pref.setChannel(update.getChannel());
                    pref.setQuietHoursStart(update.getQuietHoursStart());
                    pref.setQuietHoursEnd(update.getQuietHoursEnd());
                    pref.setUpdatedAt(LocalDateTime.now());

                    return preferenceRepository.save(pref);
                })
                .toList();

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> registerToken(
            @AuthenticationPrincipal User user,
            @RequestBody RegisterTokenRequest request) {

        UserDeviceToken token = tokenRepository
                .findByUser_UserIdAndToken(user.getUserId(), request.token())
                .orElse(new UserDeviceToken());

        token.setUser(user);
        token.setToken(request.token());
        token.setPlatform(UserDeviceToken.Platform.valueOf(request.platform().toUpperCase()));
        token.setIsActive(true);
        token.setLastUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        return ResponseEntity.ok().build();
    }



    record RegisterTokenRequest(String token, String platform) {}

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> pageResult = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(
                user.getUserId(), pageable
        );

        return ResponseEntity.ok(pageResult.getContent());
    }

    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable String notificationId) {

        Optional<Notification> opt = notificationRepository
                .findByNotificationIdAndUser_UserId(notificationId, user.getUserId());

        if (opt.isPresent()) {
            Notification notif = opt.get();
            notif.setStatus(Notification.Status.delivered);
            notificationRepository.save(notif);
            return ResponseEntity.<Void>noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> broadcast(
            @RequestBody BroadcastRequest request) {

        Map<String, Object> data = request.data() != null ? request.data() : Map.of();

        notificationService.sendToMultipleUsers(
                request.userIds(),
                request.type(),
                data
        );

        return ResponseEntity.ok().build();
    }

    record BroadcastRequest(
            List<String> userIds,
            NotificationPreference.NotificationType type,
            Map<String, Object> data
    ) {}
}