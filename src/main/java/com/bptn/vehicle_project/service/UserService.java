package com.bptn.vehicle_project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.repository.UserRepository;

public class UserService {
	
	@Autowired
	User user;
	
	@Autowired
	UserRepository userRepository;
	
	
	public Optional<User> findByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}
	
	public void createUser(User user) {
		this.userRepository.save(user);
	}
}
