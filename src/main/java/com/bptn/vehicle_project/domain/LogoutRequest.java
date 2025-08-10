package com.bptn.vehicle_project.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class LogoutRequest {
    private String username;
    private String logoutReason;
    private String sessionId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;

    // Default constructor
    public LogoutRequest() {}

    // Constructor with all fields
    public LogoutRequest(String username, String logoutReason, String sessionId, LocalDateTime timestamp) {
        this.username = username;
        this.logoutReason = logoutReason;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLogoutReason() {
        return logoutReason;
    }

    public void setLogoutReason(String logoutReason) {
        this.logoutReason = logoutReason;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 