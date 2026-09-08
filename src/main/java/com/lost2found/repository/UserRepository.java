package com.lost2found.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.lost2found.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository performing Firestore CRUD operations for the 'users' collection.
 */
@Repository
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private static final String COLLECTION_NAME = "users";

    private final Firestore firestore;
    private final Map<String, User> mockUserCache = new ConcurrentHashMap<>();

    public UserRepository(ObjectProvider<Firestore> firestoreProvider) {
        this.firestore = firestoreProvider.getIfAvailable();
    }

    public User save(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());

        if (user.getId() == null || user.getId().trim().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }

        if (firestore != null) {
            try {
                CollectionReference users = firestore.collection(COLLECTION_NAME);
                DocumentReference docRef = users.document(user.getId());

                Map<String, Object> data = new HashMap<>();
                data.put("id", user.getId());
                data.put("name", user.getName());
                data.put("username", user.getUsername());
                data.put("email", user.getEmail());
                data.put("password", user.getPassword());
                data.put("phoneNumber", user.getPhoneNumber());
                data.put("active", user.isActive());
                data.put("roles", new java.util.ArrayList<>(user.getRoles()));
                data.put("dob", user.getDob());
                data.put("gender", user.getGender());
                data.put("rollNumber", user.getRollNumber());
                data.put("department", user.getDepartment());
                data.put("yearOfStudy", user.getYearOfStudy());
                data.put("collegeName", user.getCollegeName());

                String avatar = user.getAvatarUrl();
                if (avatar != null && avatar.startsWith("data:image") && avatar.length() > 900000) {
                    avatar = null; // Prevent Firestore 1MB document property overflow
                }
                data.put("avatarUrl", avatar);

                String cover = user.getCoverUrl();
                if (cover != null && cover.startsWith("data:image") && cover.length() > 900000) {
                    cover = null;
                }
                data.put("coverUrl", cover);

                data.put("communityPoints", user.getCommunityPoints());
                data.put("createdAt", user.getCreatedAt().toString());
                data.put("updatedAt", user.getUpdatedAt().toString());

                docRef.set(data).get();
                log.info("Successfully saved user {} ({}) to Firestore 'users' collection", user.getName(), user.getId());
            } catch (Exception ex) {
                log.error("ERROR saving user to Firestore 'users' collection: ", ex);
            }
        }

        mockUserCache.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(String id) {
        if (id == null) return Optional.empty();

        if (firestore != null) {
            try {
                DocumentSnapshot doc = firestore.collection(COLLECTION_NAME).document(id).get().get();
                if (doc.exists()) {
                    return Optional.of(mapDocumentToUser(doc));
                }
            } catch (Exception ex) {
                log.debug("Firestore user query fallback: {}", ex.getMessage());
            }
        }

        return Optional.ofNullable(mockUserCache.get(id));
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();

        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("username", username).get();
                QuerySnapshot snapshot = future.get();
                if (!snapshot.isEmpty()) {
                    return Optional.of(mapDocumentToUser(snapshot.getDocuments().get(0)));
                }
            } catch (Exception ex) {
                log.debug("Firestore user search fallback: {}", ex.getMessage());
            }
        }

        return mockUserCache.values().stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();

        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("email", email).get();
                QuerySnapshot snapshot = future.get();
                if (!snapshot.isEmpty()) {
                    return Optional.of(mapDocumentToUser(snapshot.getDocuments().get(0)));
                }
            } catch (Exception ex) {
                log.debug("Firestore user email query fallback: {}", ex.getMessage());
            }
        }

        return mockUserCache.values().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();
    }

    public Optional<User> findByUsernameOrEmail(String username, String email) {
        Optional<User> byUsername = findByUsername(username);
        if (byUsername.isPresent()) {
            return byUsername;
        }
        return findByEmail(email);
    }

    public Boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public Boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public long countUsers() {
        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
                QuerySnapshot snapshot = future.get();
                return snapshot.size();
            } catch (Exception ex) {
                log.debug("Firestore count users fallback: {}", ex.getMessage());
            }
        }
        return mockUserCache.size();
    }

    public void deleteAll() {
        mockUserCache.clear();
    }

    @SuppressWarnings("unchecked")
    private User mapDocumentToUser(DocumentSnapshot doc) {
        List<String> rolesList = (List<String>) doc.get("roles");
        User user = new User(
                doc.getId(),
                doc.getString("name"),
                doc.getString("username"),
                doc.getString("email"),
                doc.getString("password"),
                doc.getString("phoneNumber"),
                Boolean.TRUE.equals(doc.getBoolean("active")),
                rolesList != null ? new HashSet<>(rolesList) : new HashSet<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        user.setDob(doc.getString("dob"));
        user.setGender(doc.getString("gender"));
        user.setRollNumber(doc.getString("rollNumber"));
        user.setDepartment(doc.getString("department"));
        user.setYearOfStudy(doc.getString("yearOfStudy"));
        user.setCollegeName(doc.getString("collegeName"));
        user.setAvatarUrl(doc.getString("avatarUrl"));
        user.setCoverUrl(doc.getString("coverUrl"));
        Long points = doc.getLong("communityPoints");
        if (points != null) user.setCommunityPoints(points.intValue());

        return user;
    }
}
