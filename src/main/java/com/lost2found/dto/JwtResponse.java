package com.lost2found.dto;

import com.lost2found.common.utils.AppConstants;

import java.util.List;

/**
 * Response payload DTO containing JWT authentication details.
 */
public class JwtResponse {

    private String accessToken;
    private String tokenType = AppConstants.BEARER_PREFIX.trim();
    private String id;
    private String name;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse() {
    }

    public JwtResponse(String accessToken, String id, String name, String username, String email, List<String> roles) {
        this.accessToken = accessToken;
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
