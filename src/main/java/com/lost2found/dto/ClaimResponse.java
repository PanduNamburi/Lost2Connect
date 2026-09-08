package com.lost2found.dto;

import com.lost2found.entity.Claim;
import com.lost2found.entity.ClaimStatus;

import java.time.LocalDateTime;

/**
 * Response DTO representing an item claim report.
 */
public class ClaimResponse {

    private String id;
    private String lostItemId;
    private String foundItemId;
    private String claimantUserId;
    private String claimantName;
    private String finderUserId;
    private String verificationProof;
    private String contactPhoneNumber;
    private ClaimStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClaimResponse() {
    }

    public static ClaimResponse fromEntity(Claim claim) {
        ClaimResponse resp = new ClaimResponse();
        resp.setId(claim.getId());
        resp.setLostItemId(claim.getLostItemId());
        resp.setFoundItemId(claim.getFoundItemId());
        resp.setClaimantUserId(claim.getClaimantUserId());
        resp.setClaimantName(claim.getClaimantName());
        resp.setFinderUserId(claim.getFinderUserId());
        resp.setVerificationProof(claim.getVerificationProof());
        resp.setContactPhoneNumber(claim.getContactPhoneNumber());
        resp.setStatus(claim.getStatus());
        resp.setCreatedAt(claim.getCreatedAt());
        resp.setUpdatedAt(claim.getUpdatedAt());
        return resp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getClaimantUserId() {
        return claimantUserId;
    }

    public void setClaimantUserId(String claimantUserId) {
        this.claimantUserId = claimantUserId;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getFinderUserId() {
        return finderUserId;
    }

    public void setFinderUserId(String finderUserId) {
        this.finderUserId = finderUserId;
    }

    public String getVerificationProof() {
        return verificationProof;
    }

    public void setVerificationProof(String verificationProof) {
        this.verificationProof = verificationProof;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
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
