package com.lost2found.controller;

import com.lost2found.common.api.ApiResponse;
import com.lost2found.dto.ClaimItemRequest;
import com.lost2found.dto.ClaimResponse;
import com.lost2found.dto.UpdateClaimStatusRequest;
import com.lost2found.security.UserPrincipal;
import com.lost2found.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing Claim Resolution APIs.
 */
@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClaimResponse>> submitClaim(
            @Valid @RequestBody ClaimItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ClaimResponse response = claimService.submitClaim(request, currentUser);
        return new ResponseEntity<>(
                ApiResponse.created("Claim submitted successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getMyClaims(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<ClaimResponse> claims = claimService.getMyClaims(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Submitted claims retrieved successfully", claims));
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getReceivedClaims(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<ClaimResponse> claims = claimService.getReceivedClaims(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Received claims retrieved successfully", claims));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClaimResponse>> updateClaimStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateClaimStatusRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ClaimResponse response = claimService.updateClaimStatus(id, request.getStatus(), currentUser);
        return ResponseEntity.ok(ApiResponse.success("Claim status updated successfully", response));
    }
}
