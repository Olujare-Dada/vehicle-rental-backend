package com.bptn.vehicle_project.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalDetailsResponse {
    private RentalInfo rental;
    private LateFeeInfo lateFeeInfo;

    public RentalDetailsResponse() {
    }

    public RentalDetailsResponse(RentalInfo rental, LateFeeInfo lateFeeInfo) {
        this.rental = rental;
        this.lateFeeInfo = lateFeeInfo;
    }

    public RentalInfo getRental() {
        return rental;
    }

    public void setRental(RentalInfo rental) {
        this.rental = rental;
    }

    public LateFeeInfo getLateFeeInfo() {
        return lateFeeInfo;
    }

    public void setLateFeeInfo(LateFeeInfo lateFeeInfo) {
        this.lateFeeInfo = lateFeeInfo;
    }

    public static class RentalInfo {
        private Integer rentalId;
        private Integer vehicleId;
        private String vehicleName;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate actualReturnDate;
        private BigDecimal totalCost;
        private String status;
        private String additionalNotes;
        private LocalDateTime createdOn;

        public RentalInfo() {
        }

        public RentalInfo(Integer rentalId, Integer vehicleId, String vehicleName, LocalDate startDate, 
                         LocalDate endDate, LocalDate actualReturnDate, BigDecimal totalCost, 
                         String status, String additionalNotes, LocalDateTime createdOn) {
            this.rentalId = rentalId;
            this.vehicleId = vehicleId;
            this.vehicleName = vehicleName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.actualReturnDate = actualReturnDate;
            this.totalCost = totalCost;
            this.status = status;
            this.additionalNotes = additionalNotes;
            this.createdOn = createdOn != null ? createdOn : LocalDateTime.now();
        }
        
        // Constructor without createdOn parameter
        public RentalInfo(Integer rentalId, Integer vehicleId, String vehicleName, LocalDate startDate, 
                         LocalDate endDate, LocalDate actualReturnDate, BigDecimal totalCost, 
                         String status, String additionalNotes) {
            this(rentalId, vehicleId, vehicleName, startDate, endDate, actualReturnDate, 
                 totalCost, status, additionalNotes, null);
        }

        // Getters and Setters
        public Integer getRentalId() { return rentalId; }
        public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }

        public Integer getVehicleId() { return vehicleId; }
        public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }

        public String getVehicleName() { return vehicleName; }
        public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

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

        public LocalDateTime getCreatedOn() { return createdOn; }
        public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    }

    public static class LateFeeInfo {
        private Boolean isLate;
        private Integer daysLate;
        private BigDecimal lateFeeAmount;
        private BigDecimal dailyRate;

        public LateFeeInfo() {
        }

        public LateFeeInfo(Boolean isLate, Integer daysLate, BigDecimal lateFeeAmount, BigDecimal dailyRate) {
            this.isLate = isLate;
            this.daysLate = daysLate;
            this.lateFeeAmount = lateFeeAmount;
            this.dailyRate = dailyRate;
        }

        // Getters and Setters
        public Boolean getIsLate() { return isLate; }
        public void setIsLate(Boolean isLate) { this.isLate = isLate; }

        public Integer getDaysLate() { return daysLate; }
        public void setDaysLate(Integer daysLate) { this.daysLate = daysLate; }

        public BigDecimal getLateFeeAmount() { return lateFeeAmount; }
        public void setLateFeeAmount(BigDecimal lateFeeAmount) { this.lateFeeAmount = lateFeeAmount; }

        public BigDecimal getDailyRate() { return dailyRate; }
        public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }
    }
}
