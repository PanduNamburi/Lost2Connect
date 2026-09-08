package com.lost2found.controller;

import com.lost2found.common.api.ApiResponse;
import com.lost2found.common.api.PaginatedResponse;
import com.lost2found.common.utils.AppConstants;
import com.lost2found.dto.CreateLostItemRequest;
import com.lost2found.dto.LostItemResponse;
import com.lost2found.dto.UpdateLostItemRequest;
import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;
import com.lost2found.security.UserPrincipal;
import com.lost2found.service.LostItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
 * REST Controller exposing Lost Item Management APIs.
 */
@RestController
@RequestMapping("/api/v1/lost-items")
public class LostItemController {

    private final LostItemService lostItemService;

    public LostItemController(LostItemService lostItemService) {
        this.lostItemService = lostItemService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LostItemResponse>> createLostItem(
            @Valid @RequestBody CreateLostItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LostItemResponse response = lostItemService.createLostItem(request, currentUser);
        return new ResponseEntity<>(
                ApiResponse.created("Lost item reported successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LostItemResponse>> getLostItemById(@PathVariable String id) {
        LostItemResponse response = lostItemService.getLostItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Lost item details retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LostItemResponse>> updateLostItem(
            @PathVariable String id,
            @RequestBody UpdateLostItemRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LostItemResponse response = lostItemService.updateLostItem(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Lost item report updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLostItem(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        lostItemService.deleteLostItem(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Lost item report deleted successfully"));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LostItemResponse>> uploadItemImages(
            @PathVariable String id,
            @RequestParam("images") List<MultipartFile> images,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LostItemResponse response = lostItemService.uploadItemImages(id, images, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Images uploaded successfully", response));
    }

    @GetMapping({"", "/search"})
    public ResponseEntity<ApiResponse<PaginatedResponse<LostItemResponse>>> searchLostItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        PaginatedResponse<LostItemResponse> response = lostItemService.searchLostItems(keyword, category, city, status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lost items matching criteria retrieved successfully", response));
    }
}
