package com.bptn.vehicle_project.domain;

import java.math.BigDecimal;

public class LateFeePaymentRequest {
    
    private Integer lateFeeId;
    private BigDecimal paymentAmount;
    private String paymentNotes;
    
    // Default constructor
    public LateFeePaymentRequest() {}
    
    // Constructor with all fields
    public LateFeePaymentRequest(Integer lateFeeId, BigDecimal paymentAmount, String paymentNotes) {
        this.lateFeeId = lateFeeId;
        this.paymentAmount = paymentAmount;
        this.paymentNotes = paymentNotes;
    }
    
    // Getters and setters
    public Integer getLateFeeId() {
        return lateFeeId;
    }
    
    public void setLateFeeId(Integer lateFeeId) {
        this.lateFeeId = lateFeeId;
    }
    
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }
    
    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    
    public String getPaymentNotes() {
        return paymentNotes;
    }
    
    public void setPaymentNotes(String paymentNotes) {
        this.paymentNotes = paymentNotes;
    }
} 