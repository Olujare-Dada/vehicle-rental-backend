package com.bptn.vehicle_project.domain;

import java.time.LocalDate;

public class RentalRequest {
    private Integer vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String additionalNotes;

    public RentalRequest() {
    }

    public RentalRequest(Integer vehicleId, LocalDate startDate, LocalDate endDate, String additionalNotes) {
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.additionalNotes = additionalNotes;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
} 