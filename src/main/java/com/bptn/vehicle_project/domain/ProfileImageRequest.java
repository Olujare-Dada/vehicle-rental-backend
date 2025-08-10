package com.bptn.vehicle_project.domain;

import org.springframework.web.multipart.MultipartFile;

public class ProfileImageRequest {
    private MultipartFile profileImage;
    private String username;

    public ProfileImageRequest() {
    }

    public ProfileImageRequest(MultipartFile profileImage, String username) {
        this.profileImage = profileImage;
        this.username = username;
    }

    public MultipartFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
} 