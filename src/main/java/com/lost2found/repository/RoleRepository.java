package com.lost2found.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.lost2found.entity.Role;
import com.lost2found.entity.RoleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository performing Firestore CRUD operations for the 'roles' collection.
 */
@Repository
public class RoleRepository {

    private static final Logger log = LoggerFactory.getLogger(RoleRepository.class);
    private static final String COLLECTION_NAME = "roles";

    private final Firestore firestore;
    private final Map<String, Role> mockRoleCache = new ConcurrentHashMap<>();

    public RoleRepository(ObjectProvider<Firestore> firestoreProvider) {
        this.firestore = firestoreProvider.getIfAvailable();
    }

    public Optional<Role> findByName(RoleName name) {
        if (firestore != null) {
            try {
                CollectionReference roles = firestore.collection(COLLECTION_NAME);
                ApiFuture<QuerySnapshot> future = roles.whereEqualTo("name", name.name()).get();
                QuerySnapshot querySnapshot = future.get();

                if (!querySnapshot.isEmpty()) {
                    QueryDocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                    Role role = new Role(
                            doc.getId(),
                            RoleName.valueOf(doc.getString("name")),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                    return Optional.of(role);
                }
            } catch (Exception ex) {
                log.debug("Firestore role query fallback: {}", ex.getMessage());
            }
        }

        return Optional.ofNullable(mockRoleCache.get(name.name()));
    }

    public boolean existsByName(RoleName name) {
        return findByName(name).isPresent();
    }

    public Role save(Role role) {
        if (role.getCreatedAt() == null) {
            role.setCreatedAt(LocalDateTime.now());
        }
        role.setUpdatedAt(LocalDateTime.now());

        if (role.getId() == null) {
            role.setId(role.getName().name());
        }

        if (firestore != null) {
            try {
                CollectionReference roles = firestore.collection(COLLECTION_NAME);
                DocumentReference docRef = roles.document(role.getId());

                Map<String, Object> data = new HashMap<>();
                data.put("id", role.getId());
                data.put("name", role.getName().name());
                data.put("createdAt", role.getCreatedAt().toString());
                data.put("updatedAt", role.getUpdatedAt().toString());

                docRef.set(data).get();
            } catch (Exception ex) {
                log.debug("Firestore role save fallback: {}", ex.getMessage());
            }
        }

        mockRoleCache.put(role.getName().name(), role);
        return role;
    }
}
