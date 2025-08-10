package com.bptn.vehicle_project.domain;

import java.math.BigDecimal;

public class BalanceDebitRequest {
    
    private BigDecimal amount;
    private String description;
    
    public BalanceDebitRequest() {
    }
    
    public BalanceDebitRequest(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
} 