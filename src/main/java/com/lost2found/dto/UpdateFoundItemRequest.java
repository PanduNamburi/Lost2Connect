package com.lost2found.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;

import java.util.List;

/**
 * Request payload for updating an existing Found Item report.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateFoundItemRequest {

    private String title;
    private String description;
    private ItemCategory category;
    private ItemStatus status;
    private String foundDate;
    private String storageLocation;
    private String venueName;
    private String city;
    private Double latitude;
    private Double longitude;
    private String contactPhoneNumber;
    private List<String> verificationQuestions;

    public UpdateFoundItemRequest() {
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
        this.verificationQuestions = verificationQuestions;
    }
}
