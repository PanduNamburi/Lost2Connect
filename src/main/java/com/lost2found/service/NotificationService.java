package com.lost2found.service;

import com.lost2found.common.exception.ResourceNotFoundException;
import com.lost2found.dto.NotificationResponse;
import com.lost2found.entity.Notification;
import com.lost2found.entity.NotificationType;
import com.lost2found.repository.NotificationRepository;
import com.lost2found.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing user Notifications.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse sendNotification(String recipientUserId, String title, String message,
                                                 NotificationType type, String relatedItemId) {
        Notification notification = new Notification(recipientUserId, title, message, type, relatedItemId);
        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }

    public NotificationResponse notifyAllUsers(String title, String message, NotificationType type, String relatedItemId) {
        return sendNotification("ALL", title, message, type, relatedItemId);
    }

    public List<NotificationResponse> getMyNotifications(UserPrincipal currentUser) {
        String recipientId = currentUser != null ? currentUser.getId() : "ALL";
        List<Notification> notifications = notificationRepository.findByRecipientUserId(recipientId);
        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public NotificationResponse markAsRead(String notificationId, UserPrincipal currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getRecipientUserId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }
}
