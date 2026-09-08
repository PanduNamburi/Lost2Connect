package com.lost2found.service;

import com.lost2found.common.exception.BadRequestException;
import com.lost2found.common.exception.DuplicateResourceException;
import com.lost2found.common.exception.ResourceNotFoundException;
import com.lost2found.dto.JwtResponse;
import com.lost2found.dto.LoginRequest;
import com.lost2found.dto.RegisterRequest;
import com.lost2found.dto.UserProfileResponse;
import com.lost2found.entity.Role;
import com.lost2found.entity.RoleName;
import com.lost2found.entity.User;
import com.lost2found.repository.RoleRepository;
import com.lost2found.repository.UserRepository;
import com.lost2found.security.JwtTokenProvider;
import com.lost2found.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service orchestrating user authentication and registration workflows with Firestore.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public UserProfileResponse registerUser(RegisterRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(request.getUsername()))) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = new User(
                request.getName(),
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhoneNumber()
        );
        user.setDob(request.getDob());
        user.setGender(request.getGender());
        user.setRollNumber(request.getRollNumber() != null ? request.getRollNumber() : request.getUsername());
        user.setDepartment(request.getDepartment());
        user.setYearOfStudy(request.getYearOfStudy());
        user.setCollegeName(request.getCollegeName());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getCoverUrl() != null) user.setCoverUrl(request.getCoverUrl());

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
        user.setRoles(Collections.singleton(userRole.getName().name()));

        User savedUser = userRepository.save(user);

        return UserProfileResponse.fromUser(savedUser);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception ex) {
            throw new BadRequestException("Invalid username/email or password credentials");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new JwtResponse(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getName(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles
        );
    }
}
