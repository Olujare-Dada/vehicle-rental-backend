package com.bptn.vehicle_project.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.service.UserService;

public class UserController {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	UserService userService;
	
	
	@GetMapping("/{username}")
	public Optional<User> findByUsername(@PathVariable String username) {
		logger.debug("The findByUsername() method was invoked!, username= {}", username);
		return this.userService.findByUsername(username);
	}
	
	@PostMapping("/signup")
	public void signup(@RequestBody User user) {
		
		logger.debug("Signing up, username: {}", user.getUsername());
		
		this.userService.signup(user);
	}
	//username, firstname, lastname, email, phone, address, password, license, currentBalance
	@PostMapping
	public String createUser(
			@RequestBody String username, 
			@RequestBody String firstName, 
			@RequestBody String lastName, 
			@RequestBody String email, 
			@RequestBody String phone, 
			@RequestBody String address,
			@RequestBody String password,
			@RequestBody String license,
			@RequestBody Double currentBalance) {
		
		return "";
	}
	
}
	
