package com.lost2found.dto;

import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.Location;
import com.lost2found.entity.LostItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Detailed Response DTO representing a Lost Item.
 */
public class LostItemResponse {

    private String id;
    private String title;
    private String description;
    private ItemCategory category;
    private ItemStatus status;
    private LocalDateTime lostDate;
    private Location location;
    private List<String> imageUrls;
    private Double rewardOffered;
    private String contactPhoneNumber;
    private String reporterId;
    private String reporterName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LostItemResponse() {
    }

    public static LostItemResponse fromEntity(LostItem item) {
        LostItemResponse response = new LostItemResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setCategory(item.getCategory());
        response.setStatus(item.getStatus());
        response.setLostDate(item.getLostDate());
        response.setLocation(item.getLocation());
        response.setImageUrls(item.getImageUrls());
        response.setRewardOffered(item.getRewardOffered());
        response.setContactPhoneNumber(item.getContactPhoneNumber());
        response.setReporterId(item.getReporterId());
        response.setReporterName(item.getReporterName());
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

    public LocalDateTime getLostDate() {
        return lostDate;
    }

    public void setLostDate(LocalDateTime lostDate) {
        this.lostDate = lostDate;
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

    public Double getRewardOffered() {
        return rewardOffered;
    }

    public void setRewardOffered(Double rewardOffered) {
        this.rewardOffered = rewardOffered;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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
