package com.bptn.vehicle_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bptn.vehicle_project.jpa.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
}
