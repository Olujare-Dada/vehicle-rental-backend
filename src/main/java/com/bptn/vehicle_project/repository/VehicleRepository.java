package com.bptn.vehicle_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bptn.vehicle_project.jpa.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
	
	Optional<Vehicle> findByVehicleId(Integer vehicleId);
	Optional<Vehicle> findByVehicleRentalStatus(Integer vehicleRentalStatus);
}
