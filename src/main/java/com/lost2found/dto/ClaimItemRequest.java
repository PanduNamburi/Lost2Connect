package com.lost2found.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for claiming a matched found item.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimItemRequest {

    @NotBlank(message = "Lost Item ID is required")
    private String lostItemId;

    @NotBlank(message = "Found Item ID is required")
    private String foundItemId;

    @NotBlank(message = "Verification answer or proof description is required")
    private String verificationAnswer;

    private String contactPhoneNumber;

    public ClaimItemRequest() {
    }

    public ClaimItemRequest(String lostItemId, String foundItemId, String verificationAnswer, String contactPhoneNumber) {
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.verificationAnswer = verificationAnswer;
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getLostItemId() {
        return lostItemId;
    }

    public void setLostItemId(String lostItemId) {
        this.lostItemId = lostItemId;
    }

    public String getFoundItemId() {
        return foundItemId;
    }

    public void setFoundItemId(String foundItemId) {
        this.foundItemId = foundItemId;
    }

    public String getVerificationAnswer() {
        return verificationAnswer;
    }

    public void setVerificationAnswer(String verificationAnswer) {
        this.verificationAnswer = verificationAnswer;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }
}
