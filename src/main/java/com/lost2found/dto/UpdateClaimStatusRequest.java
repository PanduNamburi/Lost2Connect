package com.lost2found.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lost2found.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for approving or rejecting a claim.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateClaimStatusRequest {

    @NotNull(message = "Claim status is required")
    private ClaimStatus status;

    public UpdateClaimStatusRequest() {
    }

    public UpdateClaimStatusRequest(ClaimStatus status) {
        this.status = status;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }
}
