package com.lost2found.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lost2found.entity.Notification;
import com.lost2found.entity.NotificationType;

import java.time.LocalDateTime;

/**
 * Response DTO representing a user notification.
 */
public class NotificationResponse {

    private String id;
    private String recipientUserId;
    private String title;
    private String message;
    private NotificationType type;
    private String relatedItemId;

    @JsonProperty("isRead")
    private boolean read;

    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public static NotificationResponse fromEntity(Notification notification) {
        NotificationResponse resp = new NotificationResponse();
        resp.setId(notification.getId());
        resp.setRecipientUserId(notification.getRecipientUserId());
        resp.setTitle(notification.getTitle());
        resp.setMessage(notification.getMessage());
        resp.setType(notification.getType());
        resp.setRelatedItemId(notification.getRelatedItemId());
        resp.setRead(notification.isRead());
        resp.setCreatedAt(notification.getCreatedAt());
        return resp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getRelatedItemId() {
        return relatedItemId;
    }

    public void setRelatedItemId(String relatedItemId) {
        this.relatedItemId = relatedItemId;
    }

    @JsonProperty("isRead")
    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
