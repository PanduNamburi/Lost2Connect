package com.lost2found.entity;

import com.lost2found.common.entity.BaseEntity;

/**
 * Claim entity representation for Firestore collection `claims`.
 */
public class Claim extends BaseEntity {

    private String lostItemId;
    private String foundItemId;
    private String claimantUserId;
    private String claimantName;
    private String finderUserId;
    private String verificationProof;
    private String contactPhoneNumber;
    private ClaimStatus status = ClaimStatus.PENDING;

    public Claim() {
        super();
    }

    public Claim(String lostItemId, String foundItemId, String claimantUserId, String claimantName,
                 String finderUserId, String verificationProof, String contactPhoneNumber) {
        super();
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.claimantUserId = claimantUserId;
        this.claimantName = claimantName;
        this.finderUserId = finderUserId;
        this.verificationProof = verificationProof;
        this.contactPhoneNumber = contactPhoneNumber;
        this.status = ClaimStatus.PENDING;
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
}
