package com.bptn.vehicle_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bptn.vehicle_project.jpa.Rental;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
	
	List<Rental> findByRentalId(Integer rentalId);
	Optional<Rental> findTopByUsernameOrderByStartDateDesc(String username);
	Optional<Rental> findTopByVehicleIdOrderByStartDateDesc(Integer vehicleId);
	

}
