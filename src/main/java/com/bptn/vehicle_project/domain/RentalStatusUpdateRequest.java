package com.bptn.vehicle_project.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalStatusUpdateRequest {
    private String status;
    private LocalDate actualReturnDate;
    private BigDecimal lateFeeAmount;

    public RentalStatusUpdateRequest() {
    }

    public RentalStatusUpdateRequest(String status, LocalDate actualReturnDate, BigDecimal lateFeeAmount) {
        this.status = status;
        this.actualReturnDate = actualReturnDate;
        this.lateFeeAmount = lateFeeAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public BigDecimal getLateFeeAmount() {
        return lateFeeAmount;
    }

    public void setLateFeeAmount(BigDecimal lateFeeAmount) {
        this.lateFeeAmount = lateFeeAmount;
    }
}
