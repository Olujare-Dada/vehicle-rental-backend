package com.bptn.vehicle_project.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.bptn.vehicle_project.jpa.Profile;
import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.repository.ProfileRepository;
import com.bptn.vehicle_project.repository.UserRepository;

@Service
public class ProfileImageService {
    
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Value("${python.flask.base-url:http://localhost:5000}")
    private String flaskBaseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Map<String, Object> uploadProfileImage(MultipartFile profileImage, String username) {
        try {
            logger.debug("Uploading profile image for user: {}", username);
            
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Prepare file resource
            ByteArrayResource fileResource = new ByteArrayResource(profileImage.getBytes()) {
                @Override
                public String getFilename() {
                    return profileImage.getOriginalFilename();
                }
            };
            
            // Prepare multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("profile_image", fileResource);
            body.add("user_id", username);
            
            // Create request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Call Python Flask endpoint
            String uploadUrl = flaskBaseUrl + "/upload-profile-pic";
            ResponseEntity<Map> response = restTemplate.postForEntity(uploadUrl, requestEntity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Get the image URL from Python Flask response
                String imageUrl = (String) response.getBody().get("profile_image_url");
                
                // Update Profile table with the image URL
                updateProfileImageUrl(username, imageUrl);
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("imageUrl", imageUrl);
                result.put("message", "Profile picture uploaded successfully");
                return result;
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "Failed to upload profile image");
                return result;
            }
            
        } catch (IOException e) {
            logger.error("Error uploading profile image: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Error processing image file");
            return result;
        } catch (Exception e) {
            logger.error("Error calling Python Flask service: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Service temporarily unavailable");
            return result;
        }
    }
    
    public Map<String, Object> editProfileImage(MultipartFile profileImage, String username) {
        try {
            logger.debug("Editing profile image for user: {}", username);
            
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Prepare file resource
            ByteArrayResource fileResource = new ByteArrayResource(profileImage.getBytes()) {
                @Override
                public String getFilename() {
                    return profileImage.getOriginalFilename();
                }
            };
            
            // Prepare multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("profile_image", fileResource);
            body.add("user_id", username);
            
            // Create request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Call Python Flask endpoint
            String editUrl = flaskBaseUrl + "/edit-profile-pic";
            ResponseEntity<Map> response = restTemplate.postForEntity(editUrl, requestEntity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Get the image URL from Python Flask response
                String imageUrl = (String) response.getBody().get("profile_image_url");
                
                // Update Profile table with the new image URL
                updateProfileImageUrl(username, imageUrl);
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("imageUrl", imageUrl);
                result.put("message", "Profile picture updated successfully");
                return result;
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "Failed to update profile image");
                return result;
            }
            
        } catch (IOException e) {
            logger.error("Error editing profile image: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Error processing image file");
            return result;
        } catch (Exception e) {
            logger.error("Error calling Python Flask service: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Service temporarily unavailable");
            return result;
        }
    }
    
    public Map<String, Object> deleteProfileImage(String username) {
        try {
            logger.debug("Deleting profile image for user: {}", username);
            
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Prepare multipart body with user_id
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("user_id", username);
            
            // Create request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // Call Python Flask endpoint
            String deleteUrl = flaskBaseUrl + "/delete-profile-pic";
            ResponseEntity<Map> response = restTemplate.postForEntity(deleteUrl, requestEntity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Remove image URL from Profile table
                removeProfileImageUrl(username);
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", response.getBody().get("message"));
                return result;
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("error", "Failed to delete profile image");
                return result;
            }
            
        } catch (Exception e) {
            logger.error("Error calling Python Flask service: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "Service temporarily unavailable");
            return result;
        }
    }
    
    /**
     * Update the profile image URL in the Profile table
     */
    private void updateProfileImageUrl(String username, String imageUrl) {
        try {
            // Find the user
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Find or create profile
                Optional<Profile> profileOpt = profileRepository.findByUserUsername(username);
                Profile profile;
                
                if (profileOpt.isPresent()) {
                    // Update existing profile
                    profile = profileOpt.get();
                    profile.setPicture(imageUrl);
                } else {
                    // Create new profile
                    profile = new Profile();
                    profile.setUser(user);
                    profile.setPicture(imageUrl);
                    // Set default values for other fields
                    profile.setBio("");
                    profile.setCity("");
                    profile.setCountry("");
                    profile.setHeadline("");
                }
                
                // Save the profile
                profileRepository.save(profile);
                logger.debug("Profile image URL updated for user: {}", username);
            } else {
                logger.error("User not found for profile image update: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error updating profile image URL for user {}: {}", username, e.getMessage());
        }
    }
    
    /**
     * Remove the profile image URL from the Profile table
     */
    private void removeProfileImageUrl(String username) {
        try {
            // Find the profile
            Optional<Profile> profileOpt = profileRepository.findByUserUsername(username);
            if (profileOpt.isPresent()) {
                Profile profile = profileOpt.get();
                profile.setPicture(null); // or empty string depending on your preference
                profileRepository.save(profile);
                logger.debug("Profile image URL removed for user: {}", username);
            } else {
                logger.debug("No profile found for user: {}", username);
            }
        } catch (Exception e) {
            logger.error("Error removing profile image URL for user {}: {}", username, e.getMessage());
        }
    }
} 