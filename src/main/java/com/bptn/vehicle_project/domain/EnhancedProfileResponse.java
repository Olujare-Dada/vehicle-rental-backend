package com.bptn.vehicle_project.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public class EnhancedProfileResponse {
    private ProfileInfo profile;
    private UserInfo user;
    private List<RentalHistory> rentalHistory;
    private BigDecimal currentBalance;
    private int totalRentals;
    private int activeRentals;

    public EnhancedProfileResponse() {
    }

    public EnhancedProfileResponse(ProfileInfo profile, UserInfo user, List<RentalHistory> rentalHistory, 
                                 BigDecimal currentBalance, int totalRentals, int activeRentals) {
        this.profile = profile;
        this.user = user;
        this.rentalHistory = rentalHistory;
        this.currentBalance = currentBalance;
        this.totalRentals = totalRentals;
        this.activeRentals = activeRentals;
    }

    // Getters and Setters
    public ProfileInfo getProfile() { return profile; }
    public void setProfile(ProfileInfo profile) { this.profile = profile; }

    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }

    public List<RentalHistory> getRentalHistory() { return rentalHistory; }
    public void setRentalHistory(List<RentalHistory> rentalHistory) { this.rentalHistory = rentalHistory; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public int getTotalRentals() { return totalRentals; }
    public void setTotalRentals(int totalRentals) { this.totalRentals = totalRentals; }

    public int getActiveRentals() { return activeRentals; }
    public void setActiveRentals(int activeRentals) { this.activeRentals = activeRentals; }

    public static class ProfileInfo {
        private Integer profileId;
        private String username;
        private String bio;
        private String city;
        private String country;
        private String headline;
        private String picture;

        public ProfileInfo() {
        }

        public ProfileInfo(Integer profileId, String username, String bio, String city, 
                          String country, String headline, String picture) {
            this.profileId = profileId;
            this.username = username;
            this.bio = bio;
            this.city = city;
            this.country = country;
            this.headline = headline;
            this.picture = picture;
        }

        // Getters and Setters
        public Integer getProfileId() { return profileId; }
        public void setProfileId(Integer profileId) { this.profileId = profileId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getHeadline() { return headline; }
        public void setHeadline(String headline) { this.headline = headline; }

        public String getPicture() { return picture; }
        public void setPicture(String picture) { this.picture = picture; }
    }

    public static class UserInfo {
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String license;
        private String licenseState;
        private Date licenseExpiry;
        private Date dateOfBirth;
        private Timestamp createdOn;

        public UserInfo() {
        }

        public UserInfo(String username, String firstName, String lastName, String email, 
                       String phone, String address, String license, String licenseState,
                       Date licenseExpiry, Date dateOfBirth, Timestamp createdOn) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.address = address;
            this.license = license;
            this.licenseState = licenseState;
            this.licenseExpiry = licenseExpiry;
            this.dateOfBirth = dateOfBirth;
            this.createdOn = createdOn;
        }

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getLicense() { return license; }
        public void setLicense(String license) { this.license = license; }

        public String getLicenseState() { return licenseState; }
        public void setLicenseState(String licenseState) { this.licenseState = licenseState; }

        public Date getLicenseExpiry() { return licenseExpiry; }
        public void setLicenseExpiry(Date licenseExpiry) { this.licenseExpiry = licenseExpiry; }

        public Date getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public Timestamp getCreatedOn() { return createdOn; }
        public void setCreatedOn(Timestamp createdOn) { this.createdOn = createdOn; }
    }

    public static class RentalHistory {
        private Integer rentalId;
        private Integer vehicleId;
        private String vehicleName;
        private String vehicleImageUrl;
        private String vehicleMake;
        private String vehicleModel;
        private Integer vehicleYear;
        private String vehicleColor;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate actualReturnDate;
        private BigDecimal totalCost;
        private String status;
        private String additionalNotes;
        private Timestamp rentalCreatedOn;
        private BigDecimal lateFees;
        private boolean isLate;

        public RentalHistory() {
        }

        public RentalHistory(Integer rentalId, Integer vehicleId, String vehicleName, String vehicleImageUrl,
                           String vehicleMake, String vehicleModel, Integer vehicleYear, String vehicleColor,
                           LocalDate startDate, LocalDate endDate, LocalDate actualReturnDate, 
                           BigDecimal totalCost, String status, String additionalNotes, 
                           Timestamp rentalCreatedOn, BigDecimal lateFees, boolean isLate) {
            this.rentalId = rentalId;
            this.vehicleId = vehicleId;
            this.vehicleName = vehicleName;
            this.vehicleImageUrl = vehicleImageUrl;
            this.vehicleMake = vehicleMake;
            this.vehicleModel = vehicleModel;
            this.vehicleYear = vehicleYear;
            this.vehicleColor = vehicleColor;
            this.startDate = startDate;
            this.endDate = endDate;
            this.actualReturnDate = actualReturnDate;
            this.totalCost = totalCost;
            this.status = status;
            this.additionalNotes = additionalNotes;
            this.rentalCreatedOn = rentalCreatedOn;
            this.lateFees = lateFees;
            this.isLate = isLate;
        }

        // Getters and Setters
        public Integer getRentalId() { return rentalId; }
        public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }

        public Integer getVehicleId() { return vehicleId; }
        public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

        public String getVehicleName() { return vehicleName; }
        public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

        public String getVehicleImageUrl() { return vehicleImageUrl; }
        public void setVehicleImageUrl(String vehicleImageUrl) { this.vehicleImageUrl = vehicleImageUrl; }

        public String getVehicleMake() { return vehicleMake; }
        public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }

        public String getVehicleModel() { return vehicleModel; }
        public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

        public Integer getVehicleYear() { return vehicleYear; }
        public void setVehicleYear(Integer vehicleYear) { this.vehicleYear = vehicleYear; }

        public String getVehicleColor() { return vehicleColor; }
        public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public LocalDate getActualReturnDate() { return actualReturnDate; }
        public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getAdditionalNotes() { return additionalNotes; }
        public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }

        public Timestamp getRentalCreatedOn() { return rentalCreatedOn; }
        public void setRentalCreatedOn(Timestamp rentalCreatedOn) { this.rentalCreatedOn = rentalCreatedOn; }

        public BigDecimal getLateFees() { return lateFees; }
        public void setLateFees(BigDecimal lateFees) { this.lateFees = lateFees; }

        public boolean isLate() { return isLate; }
        public void setLate(boolean late) { isLate = late; }
    }
}
