package com.lost2found.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lost2found.entity.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Request payload for creating a Found Item report.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateFoundItemRequest {

    @NotBlank(message = "Item title is required")
    @Size(min = 2, max = 150, message = "Title must be between 2 and 150 characters")
    private String title;

    @NotBlank(message = "Item description is required")
    @Size(min = 3, max = 2000, message = "Description must be between 3 and 2000 characters")
    private String description;

    @NotNull(message = "Item category is required")
    private ItemCategory category;

    @NotBlank(message = "Found date is required")
    @com.fasterxml.jackson.annotation.JsonAlias({"foundDateTime", "foundDate"})
    private String foundDate;

    @com.fasterxml.jackson.annotation.JsonAlias({"custodyLocation", "storageLocation"})
    private String storageLocation;

    @NotBlank(message = "Venue name or landmark is required")
    @com.fasterxml.jackson.annotation.JsonAlias({"venueLandmark", "venueName"})
    private String venueName;

    private String city = "Main Campus";

    private Double latitude;
    private Double longitude;

    private String contactPhoneNumber;
    private List<String> verificationQuestions = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    public CreateFoundItemRequest() {
    }

    public CreateFoundItemRequest(String title, String description, ItemCategory category, String foundDate,
                                  String storageLocation, String venueName, String city, Double latitude,
                                  Double longitude, String contactPhoneNumber, List<String> verificationQuestions) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.foundDate = foundDate;
        this.storageLocation = storageLocation;
        this.venueName = venueName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactPhoneNumber = contactPhoneNumber;
        this.verificationQuestions = verificationQuestions != null ? verificationQuestions : new ArrayList<>();
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

    public String getFoundDate() {
        return foundDate;
    }

    public void setFoundDate(String foundDate) {
        this.foundDate = foundDate;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
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

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public List<String> getVerificationQuestions() {
        return verificationQuestions;
    }

    public void setVerificationQuestions(List<String> verificationQuestions) {
        this.verificationQuestions = verificationQuestions != null ? verificationQuestions : new ArrayList<>();
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }
}

