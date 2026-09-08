package com.lost2found.entity;

import com.lost2found.common.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LostItem Entity representation for Firestore database.
 */
public class LostItem extends BaseEntity {

    private String title;
    private String description;
    private ItemCategory category;
    private ItemStatus status = ItemStatus.LOST;
    private LocalDateTime lostDate;
    private Location location;
    private List<String> imageUrls = new ArrayList<>();
    private Double rewardOffered;
    private String contactPhoneNumber;
    private String reporterId;
    private String reporterName;

    public LostItem() {
        super();
    }

    public LostItem(String title, String description, ItemCategory category, LocalDateTime lostDate, Location location, Double rewardOffered, String contactPhoneNumber, String reporterId, String reporterName) {
        super();
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = ItemStatus.LOST;
        this.lostDate = lostDate;
        this.location = location;
        this.rewardOffered = rewardOffered;
        this.contactPhoneNumber = contactPhoneNumber;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.imageUrls = new ArrayList<>();
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
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
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
}
