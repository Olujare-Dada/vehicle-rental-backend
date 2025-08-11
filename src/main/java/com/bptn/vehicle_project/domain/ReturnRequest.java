package com.bptn.vehicle_project.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ReturnRequest {
    
    private Integer rentalId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    
    private String returnNotes;
    
    // Optional: mileage at return for future enhancements
    private Integer mileageAtReturn;
    
    // Default constructor
    public ReturnRequest() {}
    
    // Constructor with all fields
    public ReturnRequest(Integer rentalId, LocalDate returnDate, String returnNotes, Integer mileageAtReturn) {
        this.rentalId = rentalId;
        this.returnDate = returnDate;
        this.returnNotes = returnNotes;
        this.mileageAtReturn = mileageAtReturn;
    }
    
    // Getters and setters
    public Integer getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getReturnNotes() {
        return returnNotes;
    }
    
    public void setReturnNotes(String returnNotes) {
        this.returnNotes = returnNotes;
    }
    
    public Integer getMileageAtReturn() {
        return mileageAtReturn;
    }
    
    public void setMileageAtReturn(Integer mileageAtReturn) {
        this.mileageAtReturn = mileageAtReturn;
    }
} 