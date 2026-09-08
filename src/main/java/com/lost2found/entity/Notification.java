package com.lost2found.entity;

import com.lost2found.common.entity.BaseEntity;

/**
 * Notification entity representation for Firestore collection `notifications`.
 */
public class Notification extends BaseEntity {

    private String recipientUserId;
    private String title;
    private String message;
    private NotificationType type;
    private String relatedItemId;
    private boolean read = false;

    public Notification() {
        super();
    }

    public Notification(String recipientUserId, String title, String message, NotificationType type, String relatedItemId) {
        super();
        this.recipientUserId = recipientUserId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedItemId = relatedItemId;
        this.read = false;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
