package com.lost2found.controller;

import com.lost2found.common.api.ApiResponse;
import com.lost2found.dto.ClaimItemRequest;
import com.lost2found.dto.ItemMatchResponse;
import com.lost2found.security.UserPrincipal;
import com.lost2found.service.SmartMatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing Smart Match Engine endpoints.
 */
@RestController
@RequestMapping("/api/v1/matches")
public class SmartMatchController {

    private final SmartMatchService smartMatchService;

    public SmartMatchController(SmartMatchService smartMatchService) {
        this.smartMatchService = smartMatchService;
    }

    @GetMapping("/lost/{lostItemId}")
    public ResponseEntity<ApiResponse<List<ItemMatchResponse>>> findMatchesForLostItem(@PathVariable String lostItemId) {
        List<ItemMatchResponse> matches = smartMatchService.findMatchesForLostItem(lostItemId);
        return ResponseEntity.ok(ApiResponse.success("Potential matches retrieved successfully for lost item", matches));
    }

    @GetMapping("/found/{foundItemId}")
    public ResponseEntity<ApiResponse<List<ItemMatchResponse>>> findMatchesForFoundItem(@PathVariable String foundItemId) {
        List<ItemMatchResponse> matches = smartMatchService.findMatchesForFoundItem(foundItemId);
        return ResponseEntity.ok(ApiResponse.success("Potential matches retrieved successfully for found item", matches));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ItemMatchResponse>>> findAllMatches() {
        List<ItemMatchResponse> matches = smartMatchService.findAllMatches();
        return ResponseEntity.ok(ApiResponse.success("All active system item matches retrieved successfully", matches));
    }

    @PostMapping("/claim")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ItemMatchResponse>> claimMatchedItem(
            @Valid @RequestBody ClaimItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ItemMatchResponse response = smartMatchService.claimMatchedItem(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Ownership claim submitted and item status updated to CLAIMED", response));
    }
}
