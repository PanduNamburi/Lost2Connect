package com.lost2found.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.Location;
import com.lost2found.entity.LostItem;
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
 * Repository for managing LostItem CRUD and Firestore collection data.
 */
@Repository
public class LostItemRepository {

    private static final Logger log = LoggerFactory.getLogger(LostItemRepository.class);
    private static final String COLLECTION_NAME = "lost_items";

    private final Firestore firestore;
    private final Map<String, LostItem> mockLostItemCache = new ConcurrentHashMap<>();

    public LostItemRepository(ObjectProvider<Firestore> firestoreProvider) {
        this.firestore = firestoreProvider.getIfAvailable();
    }

    public LostItem save(LostItem item) {
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(LocalDateTime.now());
        }
        item.setUpdatedAt(LocalDateTime.now());

        if (item.getId() == null || item.getId().trim().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }

        if (firestore != null) {
            try {
                CollectionReference items = firestore.collection(COLLECTION_NAME);
                DocumentReference docRef = items.document(item.getId());

                Map<String, Object> data = new HashMap<>();
                data.put("id", item.getId());
                data.put("title", item.getTitle());
                data.put("description", item.getDescription());
                data.put("category", item.getCategory() != null ? item.getCategory().name() : null);
                data.put("status", item.getStatus() != null ? item.getStatus().name() : ItemStatus.LOST.name());
                data.put("lostDate", item.getLostDate() != null ? item.getLostDate().toString() : null);

                if (item.getLocation() != null) {
                    Map<String, Object> locMap = new HashMap<>();
                    locMap.put("venueName", item.getLocation().getVenueName());
                    locMap.put("city", item.getLocation().getCity());
                    locMap.put("latitude", item.getLocation().getLatitude());
                    locMap.put("longitude", item.getLocation().getLongitude());
                    data.put("location", locMap);
                }

                List<String> sanitizedImages = new ArrayList<>();
                if (item.getImageUrls() != null) {
                    for (String url : item.getImageUrls()) {
                        if (url != null && url.startsWith("data:image") && url.length() > 300000) {
                            continue; // Skip oversized inline Base64 data strings > 300KB for Firestore doc property
                        }
                        if (url != null) sanitizedImages.add(url);
                    }
                }
                data.put("imageUrls", sanitizedImages);
                data.put("rewardOffered", item.getRewardOffered());
                data.put("contactPhoneNumber", item.getContactPhoneNumber());
                data.put("reporterId", item.getReporterId());
                data.put("reporterName", item.getReporterName());
                data.put("createdAt", item.getCreatedAt().toString());
                data.put("updatedAt", item.getUpdatedAt().toString());

                docRef.set(data).get();
            } catch (Exception ex) {
                log.debug("Firestore lost item save fallback: {}", ex.getMessage());
            }
        }

        mockLostItemCache.put(item.getId(), item);
        return item;
    }

    public Optional<LostItem> findById(String id) {
        if (id == null) return Optional.empty();

        if (firestore != null) {
            try {
                DocumentSnapshot doc = firestore.collection(COLLECTION_NAME).document(id).get().get();
                if (doc.exists()) {
                    return Optional.of(mapDocumentToLostItem(doc));
                }
            } catch (Exception ex) {
                log.debug("Firestore lost item fetch fallback: {}", ex.getMessage());
            }
        }

        return Optional.ofNullable(mockLostItemCache.get(id));
    }

    public List<LostItem> findAll() {
        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
                QuerySnapshot snapshot = future.get();
                if (!snapshot.isEmpty()) {
                    return snapshot.getDocuments().stream()
                            .map(this::mapDocumentToLostItem)
                            .collect(Collectors.toList());
                }
            } catch (Exception ex) {
                log.debug("Firestore list all fallback: {}", ex.getMessage());
            }
        }

        return new ArrayList<>(mockLostItemCache.values());
    }

    public List<LostItem> search(String keyword, ItemCategory category, String city, ItemStatus status) {
        return findAll().stream()
                .filter(item -> category == null || item.getCategory() == category)
                .filter(item -> status == null || item.getStatus() == status)
                .filter(item -> city == null || city.trim().isEmpty() ||
                        (item.getLocation() != null && item.getLocation().getCity() != null &&
                         item.getLocation().getCity().equalsIgnoreCase(city.trim())))
                .filter(item -> {
                    if (keyword == null || keyword.trim().isEmpty()) return true;
                    String kw = keyword.trim().toLowerCase();
                    boolean matchTitle = item.getTitle() != null && item.getTitle().toLowerCase().contains(kw);
                    boolean matchDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(kw);
                    boolean matchVenue = item.getLocation() != null && item.getLocation().getVenueName() != null &&
                            item.getLocation().getVenueName().toLowerCase().contains(kw);
                    return matchTitle || matchDesc || matchVenue;
                })
                .collect(Collectors.toList());
    }

    public boolean deleteById(String id) {
        if (id == null) return false;

        if (firestore != null) {
            try {
                firestore.collection(COLLECTION_NAME).document(id).delete().get();
            } catch (Exception ex) {
                log.debug("Firestore delete fallback: {}", ex.getMessage());
            }
        }

        return mockLostItemCache.remove(id) != null;
    }

    public void deleteAll() {
        mockLostItemCache.clear();
    }

    @SuppressWarnings("unchecked")
    private LostItem mapDocumentToLostItem(DocumentSnapshot doc) {
        LostItem item = new LostItem();
        item.setId(doc.getId());
        item.setTitle(doc.getString("title"));
        item.setDescription(doc.getString("description"));

        String catStr = doc.getString("category");
        if (catStr != null) item.setCategory(ItemCategory.fromString(catStr));

        String statusStr = doc.getString("status");
        if (statusStr != null) item.setStatus(ItemStatus.valueOf(statusStr));

        String lostDateStr = doc.getString("lostDate");
        if (lostDateStr != null) item.setLostDate(LocalDateTime.parse(lostDateStr));

        Map<String, Object> locMap = (Map<String, Object>) doc.get("location");
        if (locMap != null) {
            Location loc = new Location();
            loc.setVenueName((String) locMap.get("venueName"));
            loc.setCity((String) locMap.get("city"));
            if (locMap.get("latitude") != null) loc.setLatitude(((Number) locMap.get("latitude")).doubleValue());
            if (locMap.get("longitude") != null) loc.setLongitude(((Number) locMap.get("longitude")).doubleValue());
            item.setLocation(loc);
        }

        List<String> images = (List<String>) doc.get("imageUrls");
        if (images != null) item.setImageUrls(images);

        if (doc.get("rewardOffered") != null) {
            item.setRewardOffered(((Number) doc.get("rewardOffered")).doubleValue());
        }
        item.setContactPhoneNumber(doc.getString("contactPhoneNumber"));
        item.setReporterId(doc.getString("reporterId"));
        item.setReporterName(doc.getString("reporterName"));

        return item;
    }
}
