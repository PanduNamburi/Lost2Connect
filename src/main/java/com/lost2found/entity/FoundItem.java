package com.lost2found.entity;

import com.lost2found.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FoundItem Entity representation for Firestore database collection `found_items`.
 */
public class FoundItem extends BaseEntity {

    private String title;
    private String description;
    private ItemCategory category;
    private ItemStatus status = ItemStatus.FOUND;
    private LocalDateTime foundDate;
    private String storageLocation;
    private Location location;
    private List<String> imageUrls = new ArrayList<>();
    private List<String> verificationQuestions = new ArrayList<>();
    private String contactPhoneNumber;
    private String finderId;
    private String finderName;

    public FoundItem() {
        super();
    }

    public FoundItem(String title, String description, ItemCategory category, LocalDateTime foundDate,
                     String storageLocation, Location location, String contactPhoneNumber,
                     String finderId, String finderName) {
        super();
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = ItemStatus.FOUND;
        this.foundDate = foundDate;
        this.storageLocation = storageLocation;
        this.location = location;
        this.contactPhoneNumber = contactPhoneNumber;
        this.finderId = finderId;
        this.finderName = finderName;
        this.imageUrls = new ArrayList<>();
        this.verificationQuestions = new ArrayList<>();
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
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public List<String> getVerificationQuestions() {
        return verificationQuestions;
    }

    public void setVerificationQuestions(List<String> verificationQuestions) {
        this.verificationQuestions = verificationQuestions != null ? verificationQuestions : new ArrayList<>();
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
}
