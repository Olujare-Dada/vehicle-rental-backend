package com.bptn.vehicle_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bptn.vehicle_project.jpa.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
	
	Optional<Vehicle> findByVehicleId(Integer vehicleId);
	Optional<Vehicle> findByVehicleRentalStatus(String vehicleRentalStatus);
	
	// Get all available vehicles with pagination
	Page<Vehicle> findByVehicleRentalStatus(String vehicleRentalStatus, Pageable pageable);
	
	// Get all vehicles with pagination
	Page<Vehicle> findAll(Pageable pageable);
	
	// Get vehicles by category with pagination
	Page<Vehicle> findByCategoryCategoryId(Integer categoryId, Pageable pageable);
	
	// Get available vehicles by category with pagination
	Page<Vehicle> findByCategoryCategoryIdAndVehicleRentalStatus(Integer categoryId, String vehicleRentalStatus, Pageable pageable);
}
