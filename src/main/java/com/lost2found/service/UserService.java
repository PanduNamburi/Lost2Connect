package com.lost2found.service;

import com.lost2found.common.exception.ResourceNotFoundException;
import com.lost2found.dto.UserProfileResponse;
import com.lost2found.entity.User;
import com.lost2found.repository.UserRepository;
import com.lost2found.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * Service managing user profile retrieval and management operations using Firestore.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileResponse getCurrentUserProfile(UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        return UserProfileResponse.fromUser(user);
    }

    public UserProfileResponse updateCurrentUserProfile(UserPrincipal currentUser, com.lost2found.dto.RegisterRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        if (request.getName() != null && !request.getName().trim().isEmpty()) user.setName(request.getName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDob() != null) user.setDob(request.getDob());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getRollNumber() != null) user.setRollNumber(request.getRollNumber());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        if (request.getYearOfStudy() != null) user.setYearOfStudy(request.getYearOfStudy());
        if (request.getCollegeName() != null) user.setCollegeName(request.getCollegeName());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getCoverUrl() != null) user.setCoverUrl(request.getCoverUrl());

        User saved = userRepository.save(user);
        return UserProfileResponse.fromUser(saved);
    }
}
