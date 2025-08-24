package com.bptn.vehicle_project.domain;

import java.time.LocalDate;

public class LateFeeCalculationRequest {
    private Integer rentalId;
    private LocalDate actualReturnDate;
    private LocalDate expectedReturnDate;

    public LateFeeCalculationRequest() {
    }

    public LateFeeCalculationRequest(Integer rentalId, LocalDate actualReturnDate, LocalDate expectedReturnDate) {
        this.rentalId = rentalId;
        this.actualReturnDate = actualReturnDate;
        this.expectedReturnDate = expectedReturnDate;
    }

    public Integer getRentalId() {
        return rentalId;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }
}
