package com.lost2found.dto;

/**
 * Response DTO pairing a lost item and a found item with calculated match metrics.
 */
public class ItemMatchResponse {

    private LostItemResponse lostItem;
    private FoundItemResponse foundItem;
    private MatchScore matchScore;

    public ItemMatchResponse() {
    }

    public ItemMatchResponse(LostItemResponse lostItem, FoundItemResponse foundItem, MatchScore matchScore) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.matchScore = matchScore;
    }

    public LostItemResponse getLostItem() {
        return lostItem;
    }

    public void setLostItem(LostItemResponse lostItem) {
        this.lostItem = lostItem;
    }

    public FoundItemResponse getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(FoundItemResponse foundItem) {
        this.foundItem = foundItem;
    }

    public MatchScore getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(MatchScore matchScore) {
        this.matchScore = matchScore;
    }
}
