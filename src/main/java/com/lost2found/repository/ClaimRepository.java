package com.lost2found.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.lost2found.entity.Claim;
import com.lost2found.entity.ClaimStatus;
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
 * Repository for managing Claim CRUD and Firestore collection `claims` data.
 */
@Repository
public class ClaimRepository {

    private static final Logger log = LoggerFactory.getLogger(ClaimRepository.class);
    private static final String COLLECTION_NAME = "claims";

    private final Firestore firestore;
    private final Map<String, Claim> mockClaimCache = new ConcurrentHashMap<>();

    public ClaimRepository(ObjectProvider<Firestore> firestoreProvider) {
        this.firestore = firestoreProvider.getIfAvailable();
    }

    public Claim save(Claim claim) {
        if (claim.getCreatedAt() == null) {
            claim.setCreatedAt(LocalDateTime.now());
        }
        claim.setUpdatedAt(LocalDateTime.now());

        if (claim.getId() == null || claim.getId().trim().isEmpty()) {
            claim.setId(UUID.randomUUID().toString());
        }

        if (firestore != null) {
            try {
                CollectionReference claims = firestore.collection(COLLECTION_NAME);
                DocumentReference docRef = claims.document(claim.getId());

                Map<String, Object> data = new HashMap<>();
                data.put("id", claim.getId());
                data.put("lostItemId", claim.getLostItemId());
                data.put("foundItemId", claim.getFoundItemId());
                data.put("claimantUserId", claim.getClaimantUserId());
                data.put("claimantName", claim.getClaimantName());
                data.put("finderUserId", claim.getFinderUserId());
                data.put("verificationProof", claim.getVerificationProof());
                data.put("contactPhoneNumber", claim.getContactPhoneNumber());
                data.put("status", claim.getStatus() != null ? claim.getStatus().name() : ClaimStatus.PENDING.name());
                data.put("createdAt", claim.getCreatedAt().toString());
                data.put("updatedAt", claim.getUpdatedAt().toString());

                docRef.set(data).get();
            } catch (Exception ex) {
                log.debug("Firestore claim save fallback: {}", ex.getMessage());
            }
        }

        mockClaimCache.put(claim.getId(), claim);
        return claim;
    }

    public Optional<Claim> findById(String id) {
        if (id == null) return Optional.empty();

        if (firestore != null) {
            try {
                DocumentSnapshot doc = firestore.collection(COLLECTION_NAME).document(id).get().get();
                if (doc.exists()) {
                    return Optional.of(mapDocumentToClaim(doc));
                }
            } catch (Exception ex) {
                log.debug("Firestore claim fetch fallback: {}", ex.getMessage());
            }
        }

        return Optional.ofNullable(mockClaimCache.get(id));
    }

    public List<Claim> findByClaimantUserId(String claimantUserId) {
        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("claimantUserId", claimantUserId)
                        .get();
                QuerySnapshot snapshot = future.get();
                if (!snapshot.isEmpty()) {
                    return snapshot.getDocuments().stream()
                            .map(this::mapDocumentToClaim)
                            .collect(Collectors.toList());
                }
            } catch (Exception ex) {
                log.debug("Firestore claim query by claimant fallback: {}", ex.getMessage());
            }
        }

        return mockClaimCache.values().stream()
                .filter(c -> claimantUserId.equals(c.getClaimantUserId()))
                .collect(Collectors.toList());
    }

    public List<Claim> findByFinderUserId(String finderUserId) {
        if (firestore != null) {
            try {
                ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("finderUserId", finderUserId)
                        .get();
                QuerySnapshot snapshot = future.get();
                if (!snapshot.isEmpty()) {
                    return snapshot.getDocuments().stream()
                            .map(this::mapDocumentToClaim)
                            .collect(Collectors.toList());
                }
            } catch (Exception ex) {
                log.debug("Firestore claim query by finder fallback: {}", ex.getMessage());
            }
        }

        return mockClaimCache.values().stream()
                .filter(c -> finderUserId.equals(c.getFinderUserId()))
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        mockClaimCache.clear();
    }

    private Claim mapDocumentToClaim(DocumentSnapshot doc) {
        Claim claim = new Claim();
        claim.setId(doc.getId());
        claim.setLostItemId(doc.getString("lostItemId"));
        claim.setFoundItemId(doc.getString("foundItemId"));
        claim.setClaimantUserId(doc.getString("claimantUserId"));
        claim.setClaimantName(doc.getString("claimantName"));
        claim.setFinderUserId(doc.getString("finderUserId"));
        claim.setVerificationProof(doc.getString("verificationProof"));
        claim.setContactPhoneNumber(doc.getString("contactPhoneNumber"));

        String statusStr = doc.getString("status");
        if (statusStr != null) claim.setStatus(ClaimStatus.valueOf(statusStr));

        return claim;
    }
}
