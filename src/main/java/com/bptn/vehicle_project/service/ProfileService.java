package com.bptn.vehicle_project.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bptn.vehicle_project.jpa.Profile;
import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.repository.ProfileRepository;
import com.bptn.vehicle_project.repository.UserRepository;

@Service
public class ProfileService {
    
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Profile getProfileByUsername(String username) {
        logger.debug("Getting profile for username: {}", username);
        
        Optional<Profile> profile = profileRepository.findByUserUsername(username);
        
        if (profile.isPresent()) {
            return profile.get();
        } else {
            // Create a default profile if none exists
            logger.debug("No profile found for username: {}, creating default profile", username);
            return createDefaultProfile(username);
        }
    }
    
    public Profile updateProfile(String username, Profile profileUpdate) {
        logger.debug("Updating profile for username: {}", username);
        
        Optional<Profile> existingProfile = profileRepository.findByUserUsername(username);
        
        if (existingProfile.isPresent()) {
            Profile profile = existingProfile.get();
            
            // Update fields if provided
            if (profileUpdate.getBio() != null) {
                profile.setBio(profileUpdate.getBio());
            }
            if (profileUpdate.getCity() != null) {
                profile.setCity(profileUpdate.getCity());
            }
            if (profileUpdate.getCountry() != null) {
                profile.setCountry(profileUpdate.getCountry());
            }
            if (profileUpdate.getHeadline() != null) {
                profile.setHeadline(profileUpdate.getHeadline());
            }
            if (profileUpdate.getPicture() != null) {
                profile.setPicture(profileUpdate.getPicture());
            }
            
            return profileRepository.save(profile);
        } else {
            throw new RuntimeException("Profile not found for username: " + username);
        }
    }
    
    private Profile createDefaultProfile(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent()) {
            Profile profile = new Profile();
            profile.setUser(user.get());
            profile.setBio("Welcome to our vehicle rental platform!");
            profile.setHeadline("New member at Vehicle Rental System");
            profile.setCity(user.get().getCity());
            profile.setCountry(user.get().getState());
            
            return profileRepository.save(profile);
        } else {
            throw new RuntimeException("User not found for username: " + username);
        }
    }
} 