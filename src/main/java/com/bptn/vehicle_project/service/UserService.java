package com.bptn.vehicle_project.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.repository.UserRepository;

public class UserService {
	
	@Autowired
	User user;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EmailService emailService;
	
	public Optional<User> findByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}
	
	public User signup(User user) {
		user.setUsername(user.getUsername().toLowerCase());
		user.setFirstName(user.getFirstName().toLowerCase());
		user.setLastName(user.getLastName().toLowerCase());
		user.setEmail(user.getEmail().toLowerCase());
		user.setEmailVerified(false);
		user.setPhone(user.getPhone());
		user.setLicense(user.getLicense().toLowerCase());
		user.setAddress(user.getAddress().toLowerCase());
		user.setPassword(user.getPassword().toLowerCase());
		user.setCreatedOn(Timestamp.from(Instant.now()));
		this.userRepository.save(user);
		return user;
	}
	
	public void createUser(User user) {
		this.userRepository.save(user);
		this.emailService.sendVerificationEmail(user);
		
	}
}
