package com.lost2found.controller;

import com.lost2found.common.api.ApiResponse;
import com.lost2found.common.api.PaginatedResponse;
import com.lost2found.common.utils.AppConstants;
import com.lost2found.dto.CreateFoundItemRequest;
import com.lost2found.dto.FoundItemResponse;
import com.lost2found.dto.UpdateFoundItemRequest;
import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;
import com.lost2found.security.UserPrincipal;
import com.lost2found.service.FoundItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller exposing Found Item Management APIs.
 */
@RestController
@RequestMapping("/api/v1/found-items")
public class FoundItemController {

    private final FoundItemService foundItemService;

    public FoundItemController(FoundItemService foundItemService) {
        this.foundItemService = foundItemService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FoundItemResponse>> createFoundItem(
            @Valid @RequestBody CreateFoundItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        FoundItemResponse response = foundItemService.createFoundItem(request, currentUser);
        return new ResponseEntity<>(
                ApiResponse.created("Found item reported successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoundItemResponse>> getFoundItemById(@PathVariable String id) {
        FoundItemResponse response = foundItemService.getFoundItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Found item details retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FoundItemResponse>> updateFoundItem(
            @PathVariable String id,
            @RequestBody UpdateFoundItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        FoundItemResponse response = foundItemService.updateFoundItem(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Found item report updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FoundItemResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam ItemStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        FoundItemResponse response = foundItemService.updateStatus(id, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Found item status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFoundItem(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        foundItemService.deleteFoundItem(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Found item report deleted successfully"));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FoundItemResponse>> uploadItemImages(
            @PathVariable String id,
            @RequestParam("images") List<MultipartFile> images,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        FoundItemResponse response = foundItemService.uploadItemImages(id, images, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Images uploaded successfully", response));
    }

    @GetMapping({"", "/search"})
    public ResponseEntity<ApiResponse<PaginatedResponse<FoundItemResponse>>> searchFoundItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        PaginatedResponse<FoundItemResponse> response = foundItemService.searchFoundItems(keyword, category, city, status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Found items matching criteria retrieved successfully", response));
    }
}
