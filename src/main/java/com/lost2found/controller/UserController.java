package com.lost2found.controller;

import com.lost2found.common.api.ApiResponse;
import com.lost2found.dto.UserProfileResponse;
import com.lost2found.security.UserPrincipal;
import com.lost2found.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for User Account & Profile Operations.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserProfileResponse userProfile = userService.getCurrentUserProfile(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Current user profile retrieved successfully", userProfile));
    }

    @org.springframework.web.bind.annotation.PutMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUser(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @org.springframework.web.bind.annotation.RequestBody com.lost2found.dto.RegisterRequest request) {
        UserProfileResponse userProfile = userService.updateCurrentUserProfile(currentUser, request);
        return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", userProfile));
    }
}
