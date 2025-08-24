package com.bptn.vehicle_project.security;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bptn.vehicle_project.provider.ResourceProvider;
import com.bptn.vehicle_project.service.LogoutService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ResourceProvider provider;
    
    @Autowired
    private LogoutService logoutService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        logger.debug("Processing request: {} {}", method, requestURI);
        
        // Check if the request path should be excluded from JWT authentication
        if (isExcludedPath(requestURI)) {
            logger.debug("Excluded path detected: {}", requestURI);
            // For excluded paths, just pass through without any authentication checks
            filterChain.doFilter(request, response);
            return;
        }

        // Only process JWT authentication for non-excluded paths
        try {
            // Get JWT token from request header
            String jwt = getJwtFromRequest(request);
            
            logger.debug("JWT token extracted: {}", jwt != null ? jwt.substring(0, Math.min(20, jwt.length())) + "..." : "NULL");

            // Validate JWT token
            if (StringUtils.hasText(jwt)) {
                try {
                    // Check if token has been invalidated (logged out)
                    if (logoutService.isTokenInvalidated(jwt)) {
                        logger.warn("JWT token has been invalidated (logged out): {}", jwt.substring(0, Math.min(20, jwt.length())) + "...");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"Token has been invalidated due to logout\"}");
                        return;
                    }
                    
                    // Verify the JWT token
                    String username = jwtService.getSubject(jwt);

                    // Load user details
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.debug("JWT authentication successful for user: {}", username);

                } catch (Exception e) {
                    logger.error("JWT authentication failed: {}", e.getMessage());
                    // Don't clear context here, let Spring Security handle it
                }
            } else {
                logger.debug("No JWT token found in request for protected endpoint");
                // Don't clear context here, let Spring Security handle it
            }

        } catch (Exception e) {
            logger.error("Error in JWT authentication filter: {}", e.getMessage());
            // Don't clear context here, let Spring Security handle it
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isExcludedPath(String requestURI) {
        String[] excludedUrls = provider.getJwtExcludedUrls();
        logger.debug("Checking if path is excluded: {}", requestURI);
        logger.debug("Excluded URLs: {}", Arrays.toString(excludedUrls));
        
        boolean isExcluded = Arrays.stream(excludedUrls)
                .anyMatch(excludedUrl -> {
                    // Handle wildcard patterns
                    if (excludedUrl.endsWith("/**")) {
                        String basePath = excludedUrl.substring(0, excludedUrl.length() - 2);
                        boolean matches = requestURI.startsWith(basePath);
                        logger.debug("Wildcard pattern {} matches {}: {}", excludedUrl, requestURI, matches);
                        return matches;
                    } else if (excludedUrl.endsWith("/*")) {
                        String basePath = excludedUrl.substring(0, excludedUrl.length() - 1);
                        boolean matches = requestURI.startsWith(basePath);
                        logger.debug("Wildcard pattern {} matches {}: {}", excludedUrl, requestURI, matches);
                        return matches;
                    } else {
                        boolean matches = requestURI.equals(excludedUrl);
                        logger.debug("Exact pattern {} matches {}: {}", excludedUrl, requestURI, matches);
                        return matches;
                    }
                });
        
        logger.debug("Path {} is excluded: {}", requestURI, isExcluded);
        return isExcluded;
    }
} 