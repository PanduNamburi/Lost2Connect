package com.lost2found.dto;

import com.lost2found.entity.FoundItem;
import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.Location;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Detailed Response DTO representing a Found Item.
 */
public class FoundItemResponse {

    private String id;
    private String title;
    private String description;
    private ItemCategory category;
    private ItemStatus status;
    private LocalDateTime foundDate;
    private String storageLocation;
    private Location location;
    private List<String> imageUrls;
    private List<String> verificationQuestions;
    private String contactPhoneNumber;
    private String finderId;
    private String finderName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FoundItemResponse() {
    }

    public static FoundItemResponse fromEntity(FoundItem item) {
        FoundItemResponse response = new FoundItemResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setCategory(item.getCategory());
        response.setStatus(item.getStatus());
        response.setFoundDate(item.getFoundDate());
        response.setStorageLocation(item.getStorageLocation());
        response.setLocation(item.getLocation());
        response.setImageUrls(item.getImageUrls());
        response.setVerificationQuestions(item.getVerificationQuestions());
        response.setContactPhoneNumber(item.getContactPhoneNumber());
        response.setFinderId(item.getFinderId());
        response.setFinderName(item.getFinderName());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public LocalDateTime getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(LocalDateTime foundDate) {
        this.foundDate = foundDate;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getVerificationQuestions() {
        return verificationQuestions;
    }

    public void setVerificationQuestions(List<String> verificationQuestions) {
        this.verificationQuestions = verificationQuestions;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getFinderId() {
        return finderId;
    }

    public void setFinderId(String finderId) {
        this.finderId = finderId;
    }

    public String getFinderName() {
        return finderName;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
