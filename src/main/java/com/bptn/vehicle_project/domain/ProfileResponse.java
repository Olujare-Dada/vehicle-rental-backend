package com.bptn.vehicle_project.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileResponse {
    
    @JsonProperty("profileId")
    private Integer profileId;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("bio")
    private String bio;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("country")
    private String country;
    
    @JsonProperty("headline")
    private String headline;
    
    @JsonProperty("picture")
    private String picture;
    
    public ProfileResponse() {
    }
    
    public ProfileResponse(Integer profileId, String username, String bio, String city, String country, String headline, String picture) {
        this.profileId = profileId;
        this.username = username;
        this.bio = bio;
        this.city = city;
        this.country = country;
        this.headline = headline;
        this.picture = picture;
    }
    
    // Getters and Setters
    public Integer getProfileId() {
        return profileId;
    }
    
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getHeadline() {
        return headline;
    }
    
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    
    public String getPicture() {
        return picture;
    }
    
    public void setPicture(String picture) {
        this.picture = picture;
    }
} 