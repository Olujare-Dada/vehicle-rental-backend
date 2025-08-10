package com.bptn.vehicle_project.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class LogoutResponse {
    private boolean success;
    private String message;
    private LogoutData data;
    private String error;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;

    // Default constructor
    public LogoutResponse() {}

    // Constructor for success response
    public LogoutResponse(boolean success, String message, LogoutData data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    // Constructor for error response
    public LogoutResponse(boolean success, String message, String error, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.error = error;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogoutData getData() {
        return data;
    }

    public void setData(LogoutData data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Inner class for logout data
    public static class LogoutData {
        private String username;
        private LocalDateTime logoutTime;
        private boolean sessionCleared;
        private boolean tokenInvalidated;

        // Default constructor
        public LogoutData() {}

        // Constructor with all fields
        public LogoutData(String username, LocalDateTime logoutTime, boolean sessionCleared, boolean tokenInvalidated) {
            this.username = username;
            this.logoutTime = logoutTime;
            this.sessionCleared = sessionCleared;
            this.tokenInvalidated = tokenInvalidated;
        }

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public LocalDateTime getLogoutTime() {
            return logoutTime;
        }

        public void setLogoutTime(LocalDateTime logoutTime) {
            this.logoutTime = logoutTime;
        }

        public boolean isSessionCleared() {
            return sessionCleared;
        }

        public void setSessionCleared(boolean sessionCleared) {
            this.sessionCleared = sessionCleared;
        }

        public boolean isTokenInvalidated() {
            return tokenInvalidated;
        }

        public void setTokenInvalidated(boolean tokenInvalidated) {
            this.tokenInvalidated = tokenInvalidated;
        }
    }
} 