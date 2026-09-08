package com.lost2found.dto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for User Profile details.
 */
public class UserProfileResponse {

    private String id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private Set<String> roles;
    private LocalDateTime createdAt;

    private String dob;
    private String gender;
    private String bloodGroup;
    private String rollNumber;
    private String department;
    private String yearOfStudy;
    private String collegeName;
    private String address;
    private String currentAddress;
    private String bio;
    private String interests;
    private String avatarUrl;
    private String coverUrl;
    private int communityPoints;

    public UserProfileResponse() {
    }

    public UserProfileResponse(String id, String name, String username, String email, String phoneNumber, Set<String> roles, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public static UserProfileResponse fromUser(com.lost2found.entity.User user) {
        UserProfileResponse resp = new UserProfileResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setPhoneNumber(user.getPhoneNumber());
        resp.setRoles(user.getRoles());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setDob(user.getDob());
        resp.setGender(user.getGender());
        resp.setBloodGroup(user.getBloodGroup());
        resp.setRollNumber(user.getRollNumber());
        resp.setDepartment(user.getDepartment());
        resp.setYearOfStudy(user.getYearOfStudy());
        resp.setCollegeName(user.getCollegeName());
        resp.setAddress(user.getAddress());
        resp.setCurrentAddress(user.getCurrentAddress());
        resp.setBio(user.getBio());
        resp.setInterests(user.getInterests());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setCoverUrl(user.getCoverUrl());
        resp.setCommunityPoints(user.getCommunityPoints());
        return resp;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCommunityPoints() {
        return communityPoints;
    }

    public void setCommunityPoints(int communityPoints) {
        this.communityPoints = communityPoints;
    }
}
