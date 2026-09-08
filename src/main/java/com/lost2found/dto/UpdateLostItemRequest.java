package com.lost2found.dto;

import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;

/**
 * Request payload for updating an existing Lost Item report.
 */
public class UpdateLostItemRequest {

    private String title;
    private String description;
    private ItemCategory category;
    private ItemStatus status;
    private String lostDate;
    private String venueName;
    private String city;
    private Double latitude;
    private Double longitude;
    private Double rewardOffered;
    private String contactPhoneNumber;

    public UpdateLostItemRequest() {
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

    public String getLostDate() {
        return lostDate;
    }

    public void setLostDate(String lostDate) {
        this.lostDate = lostDate;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
}
