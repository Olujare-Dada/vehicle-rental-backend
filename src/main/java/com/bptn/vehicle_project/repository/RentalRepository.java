package com.bptn.vehicle_project.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bptn.vehicle_project.jpa.Rental;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
	
	List<Rental> findByRentalId(Integer rentalId);
	List<Rental> findByUserUsername(String username);
	Optional<Rental> findTopByUserUsernameOrderByStartDateDesc(String username);
//	Optional<Rental> findTopByVehicleIdOrderByStartDateDesc(Integer vehicleId);
	Optional<Rental> findTopByVehicleVehicleIdOrderByStartDateDesc(Integer vehicleId);
	
	// Check for overlapping rentals for a specific vehicle
	List<Rental> findByVehicleVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
		Integer vehicleId, LocalDate endDate, LocalDate startDate);
	
	// Check for overlapping rentals for a specific user
	List<Rental> findByUserUsernameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
		String username, LocalDate endDate, LocalDate startDate);
	
	// Check for active rentals by user (not returned)
	List<Rental> findByUserUsernameAndReturnFlagNot(String username, String returnFlag);
	
	// Count active rentals by user
	long countByUserUsernameAndReturnFlagNot(String username, String returnFlag);

}
