package com.bptn.vehicle_project.domain;

import java.math.BigDecimal;

public class LateFeeCalculationResponse {
    private BigDecimal lateFeeAmount;
    private Integer daysLate;
    private BigDecimal dailyRate;
    private String message;

    public LateFeeCalculationResponse() {
    }

    public LateFeeCalculationResponse(BigDecimal lateFeeAmount, Integer daysLate, BigDecimal dailyRate, String message) {
        this.lateFeeAmount = lateFeeAmount;
        this.daysLate = daysLate;
        this.dailyRate = dailyRate;
        this.message = message;
    }

    public BigDecimal getLateFeeAmount() {
        return lateFeeAmount;
    }

    public void setLateFeeAmount(BigDecimal lateFeeAmount) {
        this.lateFeeAmount = lateFeeAmount;
    }

    public Integer getDaysLate() {
        return daysLate;
    }

    public void setDaysLate(Integer daysLate) {
        this.daysLate = daysLate;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
