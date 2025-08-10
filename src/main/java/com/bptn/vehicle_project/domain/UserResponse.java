package com.bptn.vehicle_project.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class UserResponse {
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("license")
    private String license;
    
    @JsonProperty("currentBalance")
    private BigDecimal currentBalance;
    
    @JsonProperty("emailVerified")
    private Boolean emailVerified;
    
    @JsonProperty("createdOn")
    private Timestamp createdOn;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("zipcode")
    private String zipcode;
    
    @JsonProperty("licenseExpiry")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date licenseExpiry;
    
    @JsonProperty("licenseState")
    private String licenseState;
    
    @JsonProperty("dateOfBirth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    
    public UserResponse() {
    }
    
    public UserResponse(String username, String firstName, String lastName, String email, 
                       String phone, String address, String license, BigDecimal currentBalance,
                       Boolean emailVerified, Timestamp createdOn, String city, String state,
                       String zipcode, Date licenseExpiry, String licenseState, Date dateOfBirth) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.license = license;
        this.currentBalance = currentBalance;
        this.emailVerified = emailVerified;
        this.createdOn = createdOn;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.licenseExpiry = licenseExpiry;
        this.licenseState = licenseState;
        this.dateOfBirth = dateOfBirth;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getLicense() {
        return license;
    }
    
    public void setLicense(String license) {
        this.license = license;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
    
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public Timestamp getCreatedOn() {
        return createdOn;
    }
    
    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipcode() {
        return zipcode;
    }
    
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    
    public Date getLicenseExpiry() {
        return licenseExpiry;
    }
    
    public void setLicenseExpiry(Date licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }
    
    public String getLicenseState() {
        return licenseState;
    }
    
    public void setLicenseState(String licenseState) {
        this.licenseState = licenseState;
    }
    
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
} 