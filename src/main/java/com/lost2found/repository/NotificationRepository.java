package com.lost2found.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.lost2found.entity.Notification;
import com.lost2found.entity.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for managing Notification CRUD and Firestore collection `notifications` data.
 */
@Repository
public class NotificationRepository {

    private static final Logger log = LoggerFactory.getLogger(NotificationRepository.class);
    private static final String COLLECTION_NAME = "notifications";

    private final Firestore firestore;
    private final Map<String, Notification> mockNotificationCache = new ConcurrentHashMap<>();

    public NotificationRepository(ObjectProvider<Firestore> firestoreProvider) {
        this.firestore = firestoreProvider.getIfAvailable();
    }

    public Notification save(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        notification.setUpdatedAt(LocalDateTime.now());

        if (notification.getId() == null || notification.getId().trim().isEmpty()) {
            notification.setId(UUID.randomUUID().toString());
        }

        if (firestore != null) {
            try {
                CollectionReference notifications = firestore.collection(COLLECTION_NAME);
                DocumentReference docRef = notifications.document(notification.getId());

                Map<String, Object> data = new HashMap<>();
                data.put("id", notification.getId());
                data.put("recipientUserId", notification.getRecipientUserId());
                data.put("title", notification.getTitle());
                data.put("message", notification.getMessage());
                data.put("type", notification.getType() != null ? notification.getType().name() : null);
                data.put("relatedItemId", notification.getRelatedItemId());
                data.put("read", notification.isRead());
                data.put("createdAt", notification.getCreatedAt().toString());
                data.put("updatedAt", notification.getUpdatedAt().toString());

                docRef.set(data).get();
            } catch (Exception ex) {
                log.debug("Firestore notification save fallback: {}", ex.getMessage());
            }
        }

        mockNotificationCache.put(notification.getId(), notification);
        return notification;
    }

    public Optional<Notification> findById(String id) {
        if (id == null) return Optional.empty();

        if (firestore != null) {
            try {
                DocumentSnapshot doc = firestore.collection(COLLECTION_NAME).document(id).get().get();
                if (doc.exists()) {
                    return Optional.of(mapDocumentToNotification(doc));
                }
            } catch (Exception ex) {
                log.debug("Firestore notification fetch fallback: {}", ex.getMessage());
            }
        }

        return Optional.ofNullable(mockNotificationCache.get(id));
    }

    public List<Notification> findByRecipientUserId(String recipientUserId) {
        List<Notification> result = new ArrayList<>();
        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> futureUser = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("recipientUserId", recipientUserId)
                        .get();
                QuerySnapshot snapshotUser = futureUser.get();
                if (!snapshotUser.isEmpty()) {
                    result.addAll(snapshotUser.getDocuments().stream().map(this::mapDocumentToNotification).collect(Collectors.toList()));
                }

                ApiFuture<QuerySnapshot> futureAll = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("recipientUserId", "ALL")
                        .get();
                QuerySnapshot snapshotAll = futureAll.get();
                if (!snapshotAll.isEmpty()) {
                    result.addAll(snapshotAll.getDocuments().stream().map(this::mapDocumentToNotification).collect(Collectors.toList()));
                }

                if (!result.isEmpty()) {
                    Map<String, Notification> map = new HashMap<>();
                    for (Notification n : result) {
                        map.put(n.getId(), n);
                    }
                    return new ArrayList<>(map.values());
                }
            } catch (Exception ex) {
                log.debug("Firestore query by recipient fallback: {}", ex.getMessage());
            }
        }

        return mockNotificationCache.values().stream()
                .filter(n -> recipientUserId.equals(n.getRecipientUserId()) || "ALL".equalsIgnoreCase(n.getRecipientUserId()))
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        mockNotificationCache.clear();
    }

    private Notification mapDocumentToNotification(DocumentSnapshot doc) {
        Notification notification = new Notification();
        notification.setId(doc.getId());
        notification.setRecipientUserId(doc.getString("recipientUserId"));
        notification.setTitle(doc.getString("title"));
        notification.setMessage(doc.getString("message"));

        String typeStr = doc.getString("type");
        if (typeStr != null) notification.setType(NotificationType.valueOf(typeStr));

        notification.setRelatedItemId(doc.getString("relatedItemId"));
        Boolean readVal = doc.getBoolean("read");
        notification.setRead(readVal != null ? readVal : false);

        return notification;
    }
}
