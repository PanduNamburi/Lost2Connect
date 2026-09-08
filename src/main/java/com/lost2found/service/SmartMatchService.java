package com.lost2found.service;

import com.lost2found.common.exception.AppException;
import com.lost2found.common.exception.ResourceNotFoundException;
import com.lost2found.dto.ClaimItemRequest;
import com.lost2found.dto.FoundItemResponse;
import com.lost2found.dto.ItemMatchResponse;
import com.lost2found.dto.LostItemResponse;
import com.lost2found.dto.MatchScore;
import com.lost2found.entity.FoundItem;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.LostItem;
import com.lost2found.repository.FoundItemRepository;
import com.lost2found.repository.LostItemRepository;
import com.lost2found.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Smart Match Engine for computing similarity scores between Lost Items and Found Items.
 */
@Service
public class SmartMatchService {

    private static final double MIN_MATCH_THRESHOLD = 40.0;

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;

    public SmartMatchService(LostItemRepository lostItemRepository, FoundItemRepository foundItemRepository) {
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
    }

    public List<ItemMatchResponse> findMatchesForLostItem(String lostItemId) {
        LostItem lostItem = lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new ResourceNotFoundException("LostItem", "id", lostItemId));

        List<FoundItem> allFoundItems = foundItemRepository.findAll();

        return allFoundItems.stream()
                .filter(found -> found.getStatus() == ItemStatus.FOUND || found.getStatus() == ItemStatus.LOST)
                .map(found -> createMatchResponse(lostItem, found))
                .filter(resp -> resp.getMatchScore().isMatch())
                .sorted(Comparator.comparingDouble((ItemMatchResponse r) -> r.getMatchScore().getTotalScore()).reversed())
                .collect(Collectors.toList());
    }

    public List<ItemMatchResponse> findMatchesForFoundItem(String foundItemId) {
        FoundItem foundItem = foundItemRepository.findById(foundItemId)
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", foundItemId));

        List<LostItem> allLostItems = lostItemRepository.findAll();

        return allLostItems.stream()
                .filter(lost -> lost.getStatus() == ItemStatus.LOST)
                .map(lost -> createMatchResponse(lost, foundItem))
                .filter(resp -> resp.getMatchScore().isMatch())
                .sorted(Comparator.comparingDouble((ItemMatchResponse r) -> r.getMatchScore().getTotalScore()).reversed())
                .collect(Collectors.toList());
    }

    public List<ItemMatchResponse> findAllMatches() {
        List<LostItem> allLostItems = lostItemRepository.findAll();
        List<FoundItem> allFoundItems = foundItemRepository.findAll();

        List<ItemMatchResponse> matches = new ArrayList<>();

        for (LostItem lost : allLostItems) {
            for (FoundItem found : allFoundItems) {
                ItemMatchResponse matchResp = createMatchResponse(lost, found);
                if (matchResp.getMatchScore().isMatch()) {
                    matches.add(matchResp);
                }
            }
        }

        matches.sort(Comparator.comparingDouble((ItemMatchResponse r) -> r.getMatchScore().getTotalScore()).reversed());
        return matches;
    }

    public ItemMatchResponse claimMatchedItem(ClaimItemRequest request, UserPrincipal currentUser) {
        LostItem lostItem = lostItemRepository.findById(request.getLostItemId())
                .orElseThrow(() -> new ResourceNotFoundException("LostItem", "id", request.getLostItemId()));

        FoundItem foundItem = foundItemRepository.findById(request.getFoundItemId())
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", request.getFoundItemId()));

        if (foundItem.getStatus() == ItemStatus.CLAIMED || foundItem.getStatus() == ItemStatus.RESOLVED) {
            throw new AppException("This item has already been claimed or resolved", HttpStatus.BAD_REQUEST);
        }

        lostItem.setStatus(ItemStatus.CLAIMED);
        foundItem.setStatus(ItemStatus.CLAIMED);

        lostItemRepository.save(lostItem);
        foundItemRepository.save(foundItem);

        return createMatchResponse(lostItem, foundItem);
    }

    public ItemMatchResponse createMatchResponse(LostItem lostItem, FoundItem foundItem) {
        MatchScore score = calculateMatchScore(lostItem, foundItem);
        return new ItemMatchResponse(
                LostItemResponse.fromEntity(lostItem),
                FoundItemResponse.fromEntity(foundItem),
                score
        );
    }

    public MatchScore calculateMatchScore(LostItem lost, FoundItem found) {
        // 1. Category Score (Max 40 points)
        double categoryScore = 0.0;
        if (lost.getCategory() != null && lost.getCategory() == found.getCategory()) {
            categoryScore = 40.0;
        }

        // 2. Text Similarity Score (Max 30 points)
        double textSimilarityScore = computeTextSimilarity(
                (lost.getTitle() + " " + lost.getDescription()).toLowerCase(),
                (found.getTitle() + " " + found.getDescription()).toLowerCase()
        ) * 30.0;

        // 3. Location Score (Max 20 points)
        double locationScore = 0.0;
        if (lost.getLocation() != null && found.getLocation() != null) {
            String lostCity = lost.getLocation().getCity();
            String foundCity = found.getLocation().getCity();
            if (lostCity != null && foundCity != null && lostCity.equalsIgnoreCase(foundCity)) {
                locationScore += 10.0;
            }

            String lostVenue = lost.getLocation().getVenueName();
            String foundVenue = found.getLocation().getVenueName();
            if (lostVenue != null && foundVenue != null && computeTextSimilarity(lostVenue.toLowerCase(), foundVenue.toLowerCase()) > 0.3) {
                locationScore += 10.0;
            }
        }
        locationScore = Math.min(20.0, locationScore);

        // 4. Date Score (Max 10 points)
        double dateScore = 0.0;
        if (lost.getLostDate() != null && found.getFoundDate() != null) {
            LocalDateTime lostDt = lost.getLostDate();
            LocalDateTime foundDt = found.getFoundDate();
            long daysApart = Math.abs(Duration.between(lostDt, foundDt).toDays());

            if (foundDt.isAfter(lostDt) || foundDt.isEqual(lostDt)) {
                if (daysApart <= 7) {
                    dateScore = 10.0;
                } else if (daysApart <= 30) {
                    dateScore = 6.0;
                }
            } else if (daysApart <= 3) {
                dateScore = 5.0;
            }
        }

        double totalScore = categoryScore + textSimilarityScore + locationScore + dateScore;
        boolean isMatch = totalScore >= MIN_MATCH_THRESHOLD;

        return new MatchScore(totalScore, categoryScore, textSimilarityScore, locationScore, dateScore, isMatch);
    }

    private double computeTextSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.trim().isEmpty() || text2.trim().isEmpty()) {
            return 0.0;
        }

        Set<String> words1 = new HashSet<>(Arrays.asList(text1.toLowerCase().split("\\W+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(text2.toLowerCase().split("\\W+")));

        words1.removeIf(w -> w.length() < 3);
        words2.removeIf(w -> w.length() < 3);

        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return (double) intersection.size() / union.size();
    }
}
