package com.bptn.vehicle_project.service;

import com.bptn.vehicle_project.domain.LogoutRequest;
import com.bptn.vehicle_project.domain.LogoutResponse;
import com.bptn.vehicle_project.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogoutService {
    
    private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);
    
    @Autowired
    private JwtService jwtService;
    
    // In-memory storage for invalidated tokens (in production, use Redis or database)
    private final ConcurrentHashMap<String, LocalDateTime> invalidatedTokens = new ConcurrentHashMap<>();
    
    /**
     * Process logout request
     */
    public LogoutResponse processLogout(LogoutRequest request, String authHeader) {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            
            // Validate request
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                logger.warn("Logout request failed: Username is required");
                return new LogoutResponse(false, "Invalid logout request", "Username is required", currentTime);
            }
            
            // Extract and validate JWT token
            String token = extractTokenFromHeader(authHeader);
            if (token == null) {
                logger.warn("Logout request failed: Invalid authorization header");
                return new LogoutResponse(false, "Invalid logout request", "Invalid authorization header", currentTime);
            }
            
            // Verify token and extract username
            String tokenUsername = jwtService.getSubject(token);
            if (tokenUsername == null || !tokenUsername.equals(request.getUsername())) {
                logger.warn("Logout request failed: JWT token validation failed for user: {}", request.getUsername());
                return new LogoutResponse(false, "Invalid or expired token", "JWT token validation failed", currentTime);
            }
            
            // Invalidate the token
            invalidateToken(token);
            
            // Log the logout
            logger.info("User {} logged out successfully. Reason: {}, Session: {}", 
                request.getUsername(), 
                request.getLogoutReason() != null ? request.getLogoutReason() : "Not specified",
                request.getSessionId() != null ? request.getSessionId() : "Not specified");
            
            // Create success response
            LogoutResponse.LogoutData logoutData = new LogoutResponse.LogoutData(
                request.getUsername(),
                currentTime,
                true,  // sessionCleared
                true   // tokenInvalidated
            );
            
            return new LogoutResponse(true, "Logout successful", logoutData, currentTime);
            
        } catch (Exception e) {
            logger.error("Error during logout process: {}", e.getMessage(), e);
            return new LogoutResponse(false, "Logout failed", "Internal server error occurred", LocalDateTime.now());
        }
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
    
    /**
     * Invalidate a JWT token
     */
    private void invalidateToken(String token) {
        // Add token to invalidated tokens list with expiration time
        // In production, you might want to store this in Redis with TTL
        invalidatedTokens.put(token, LocalDateTime.now().plusHours(24)); // Keep for 24 hours
        
        // Clean up expired invalidated tokens (older than 24 hours)
        cleanupExpiredTokens();
    }
    
    /**
     * Check if a token is invalidated
     */
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.containsKey(token);
    }
    
    /**
     * Clean up expired invalidated tokens
     */
    private void cleanupExpiredTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        invalidatedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }
    
    /**
     * Get count of invalidated tokens (for monitoring)
     */
    public int getInvalidatedTokenCount() {
        return invalidatedTokens.size();
    }
} 