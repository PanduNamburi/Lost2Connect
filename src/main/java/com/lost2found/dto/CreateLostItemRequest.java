package com.lost2found.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lost2found.entity.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Request payload for creating a Lost Item post.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLostItemRequest {

    @NotBlank(message = "Item title is required")
    @Size(min = 2, max = 150, message = "Title must be between 2 and 150 characters")
    private String title;

    @NotBlank(message = "Item description is required")
    @Size(min = 3, max = 2000, message = "Description must be between 3 and 2000 characters")
    private String description;

    @NotNull(message = "Item category is required")
    private ItemCategory category;

    @NotBlank(message = "Lost date is required (format: yyyy-MM-dd HH:mm:ss)")
    @com.fasterxml.jackson.annotation.JsonAlias({"lostDateTime", "lostDate"})
    private String lostDate;

    @NotBlank(message = "Venue name or landmark is required")
    @com.fasterxml.jackson.annotation.JsonAlias({"venueLandmark", "venueName"})
    private String venueName;

    private String city = "Main Campus";

    private Double latitude;
    private Double longitude;

    private Double rewardOffered;
    private String contactPhoneNumber;
    private List<String> imageUrls = new ArrayList<>();

    public CreateLostItemRequest() {
    }

    public CreateLostItemRequest(String title, String description, ItemCategory category, String lostDate, String venueName, String city, Double latitude, Double longitude, Double rewardOffered, String contactPhoneNumber) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.lostDate = lostDate;
        this.venueName = venueName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rewardOffered = rewardOffered;
        this.contactPhoneNumber = contactPhoneNumber;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }
}

