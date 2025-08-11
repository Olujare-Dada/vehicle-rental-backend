package com.bptn.vehicle_project.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReturnResponse {
    
    private Integer rentalId;
    private Integer vehicleId;
    private String vehicleName;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate originalEndDate;
    
    private String totalRentalCost;
    private String lateFees;
    private String finalAmount;
    private String returnStatus;
    private String returnNotes;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    // Default constructor
    public ReturnResponse() {}
    
    // Constructor with all fields
    public ReturnResponse(Integer rentalId, Integer vehicleId, String vehicleName, 
                         LocalDate returnDate, LocalDate originalEndDate, 
                         String totalRentalCost, String lateFees, String finalAmount, 
                         String returnStatus, String returnNotes) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.returnDate = returnDate;
        this.originalEndDate = originalEndDate;
        this.totalRentalCost = totalRentalCost;
        this.lateFees = lateFees;
        this.finalAmount = finalAmount;
        this.returnStatus = returnStatus;
        this.returnNotes = returnNotes;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and setters
    public Integer getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }
    
    public Integer getVehicleId() {
        return vehicleId;
    }
    
    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    public String getVehicleName() {
        return vehicleName;
    }
    
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public LocalDate getOriginalEndDate() {
        return originalEndDate;
    }
    
    public void setOriginalEndDate(LocalDate originalEndDate) {
        this.originalEndDate = originalEndDate;
    }
    
    public String getTotalRentalCost() {
        return totalRentalCost;
    }
    
    public void setTotalRentalCost(String totalRentalCost) {
        this.totalRentalCost = totalRentalCost;
    }
    
    public String getLateFees() {
        return lateFees;
    }
    
    public void setLateFees(String lateFees) {
        this.lateFees = lateFees;
    }
    
    public String getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public String getReturnStatus() {
        return returnStatus;
    }
    
    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }
    
    public String getReturnNotes() {
        return returnNotes;
    }
    
    public void setReturnNotes(String returnNotes) {
        this.returnNotes = returnNotes;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 