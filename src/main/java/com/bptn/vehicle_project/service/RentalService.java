package com.bptn.vehicle_project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bptn.vehicle_project.domain.InsufficientBalanceException;
import com.bptn.vehicle_project.domain.RentalRequest;
import com.bptn.vehicle_project.domain.VehicleNotAvailableException;
import com.bptn.vehicle_project.domain.VehicleNotFoundException;
import com.bptn.vehicle_project.jpa.Rental;
import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.jpa.Vehicle;
import com.bptn.vehicle_project.repository.RentalRepository;
import com.bptn.vehicle_project.repository.UserRepository;
import com.bptn.vehicle_project.repository.VehicleRepository;

@Service
public class RentalService {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RentalRepository rentalRepository;
	
	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Transactional
	public Rental rentVehicle(RentalRequest rentalRequest) {
		// Get current authenticated user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get vehicle
		Vehicle vehicle = vehicleRepository.findByVehicleId(rentalRequest.getVehicleId())
				.orElseThrow(() -> new VehicleNotFoundException("Vehicle not found"));
		
		// Validate rental request
		validateRentalRequest(rentalRequest, vehicle, user);
		
		// Calculate total cost
		BigDecimal totalCost = calculateTotalCost(rentalRequest.getStartDate(), 
				rentalRequest.getEndDate(), vehicle.getRentalCostPerDay());
		
		// Check if user has sufficient balance
		if (user.getCurrentBalance().compareTo(totalCost) < 0) {
			throw new InsufficientBalanceException("Insufficient balance. Required: " + totalCost + ", Available: " + user.getCurrentBalance());
		}
		
		// Create rental record
		Rental rental = new Rental();
		rental.setUser(user);
		rental.setVehicle(vehicle);
		rental.setStartDate(rentalRequest.getStartDate());
		rental.setEndDate(rentalRequest.getEndDate());
		rental.setTotalCost(totalCost);
		rental.setReturnFlag("ACTIVE");
		rental.setAdditionalNotes(rentalRequest.getAdditionalNotes());
		
		// Save rental
		Rental savedRental = rentalRepository.save(rental);
		
		// Deduct money from user balance
		user.setCurrentBalance(user.getCurrentBalance().subtract(totalCost));
		userRepository.save(user);
		
		// Update vehicle status to rented
		vehicle.setVehicleRentalStatus("RENTED");
		vehicleRepository.save(vehicle);
		
		// Send receipt email
		emailService.sendRentalReceiptEmail(user, vehicle, savedRental);
		
		logger.info("Vehicle rental successful. Rental ID: {}, User: {}, Vehicle: {}", 
				savedRental.getRentalId(), username, vehicle.getVehicleId());
		
		return savedRental;
	}
	
	private void validateRentalRequest(RentalRequest rentalRequest, Vehicle vehicle, User user) {
		// Check if dates are valid
		if (rentalRequest.getStartDate().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Start date cannot be in the past");
		}
		
		if (rentalRequest.getEndDate().isBefore(rentalRequest.getStartDate())) {
			throw new IllegalArgumentException("End date must be after start date");
		}
		
		// Check vehicle availability
		if (!"AVAILABLE".equalsIgnoreCase(vehicle.getVehicleRentalStatus())) {
			throw new VehicleNotAvailableException("Vehicle is not available for rental. Status: " + vehicle.getVehicleRentalStatus());
		}
		
		// Check for overlapping rentals for this vehicle
		List<Rental> overlappingVehicleRentals = rentalRepository
				.findByVehicleVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
						vehicle.getVehicleId(), rentalRequest.getEndDate(), rentalRequest.getStartDate());
		
		if (!overlappingVehicleRentals.isEmpty()) {
			throw new VehicleNotAvailableException("Vehicle is already rented during the selected period");
		}
		
		// Check for overlapping rentals for this user
		List<Rental> overlappingUserRentals = rentalRepository
				.findByUserUsernameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
						user.getUsername(), rentalRequest.getEndDate(), rentalRequest.getStartDate());
		
		if (!overlappingUserRentals.isEmpty()) {
			throw new RuntimeException("You already have a rental during the selected period");
		}
		
		// Check if user already has an active rental (one car per customer rule)
		long activeRentalsCount = rentalRepository.countByUserUsernameAndReturnFlagNot(user.getUsername(), "RETURNED");
		if (activeRentalsCount > 0) {
			throw new RuntimeException("You can only rent one car at a time. Please return your current rental before renting another vehicle.");
		}
	}
	
	private BigDecimal calculateTotalCost(LocalDate startDate, LocalDate endDate, BigDecimal costPerDay) {
		long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both start and end dates
		return costPerDay.multiply(BigDecimal.valueOf(days));
	}
} 