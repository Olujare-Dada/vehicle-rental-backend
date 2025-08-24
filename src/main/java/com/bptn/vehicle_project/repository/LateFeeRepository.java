package com.bptn.vehicle_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bptn.vehicle_project.jpa.LateFee;

public interface LateFeeRepository extends JpaRepository<LateFee, Integer> {
	
	// Find all late fees for a specific user
	List<LateFee> findByUsername(String username);
	
	// Find late fees for a specific rental
	List<LateFee> findByRentalId(Integer rentalId);
} 