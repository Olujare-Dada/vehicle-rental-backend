package com.bptn.vehicle_project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bptn.vehicle_project.domain.InsufficientBalanceException;
import com.bptn.vehicle_project.domain.RentalRequest;
import com.bptn.vehicle_project.domain.LateFeePaymentRequest;
import com.bptn.vehicle_project.domain.ReturnRequest;
import com.bptn.vehicle_project.domain.ReturnResponse;
import com.bptn.vehicle_project.domain.VehicleNotAvailableException;
import com.bptn.vehicle_project.domain.VehicleNotFoundException;
import com.bptn.vehicle_project.jpa.LateFee;
import com.bptn.vehicle_project.jpa.LateFeePayment;
import com.bptn.vehicle_project.jpa.Rental;
import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.jpa.Vehicle;
import com.bptn.vehicle_project.repository.LateFeePaymentRepository;
import com.bptn.vehicle_project.repository.LateFeeRepository;
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
	
	@Autowired
	private LateFeeRepository lateFeeRepository;
	
	@Autowired
	private LateFeePaymentRepository lateFeePaymentRepository;
	
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
		
		// Check if user has outstanding late fees (negative balance)
		if (user.getCurrentBalance().compareTo(BigDecimal.ZERO) < 0) {
			throw new RuntimeException("You cannot rent a vehicle while you have outstanding late fees. Current balance: " + user.getCurrentBalance());
		}
	}
	
	private BigDecimal calculateTotalCost(LocalDate startDate, LocalDate endDate, BigDecimal costPerDay) {
		long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both start and end dates
		return costPerDay.multiply(BigDecimal.valueOf(days));
	}
	
	@Transactional
	public ReturnResponse returnVehicle(ReturnRequest returnRequest) {
		// Get current authenticated user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get rental record
		Rental rental = rentalRepository.findByRentalId(returnRequest.getRentalId())
				.orElseThrow(() -> new RuntimeException("Rental not found"));
		
		// Validate that the rental belongs to the current user
		if (!rental.getUser().getUsername().equals(username)) {
			throw new RuntimeException("You can only return vehicles from your own rentals");
		}
		
		// Validate that the rental is active
		if (!"ACTIVE".equals(rental.getReturnFlag())) {
			throw new RuntimeException("This rental has already been returned");
		}
		
		// Get vehicle
		Vehicle vehicle = rental.getVehicle();
		
		// Calculate late fees if returned after end date
		BigDecimal lateFees = BigDecimal.ZERO;
		if (returnRequest.getReturnDate().isAfter(rental.getEndDate())) {
			long lateDays = ChronoUnit.DAYS.between(rental.getEndDate(), returnRequest.getReturnDate());
			lateFees = vehicle.getRentalCostPerDay().multiply(BigDecimal.valueOf(lateDays));
			
			// Create late fee record
			LateFee lateFee = new LateFee(username, rental.getRentalId(), (int) lateDays, lateFees);
			lateFeeRepository.save(lateFee);
			
			// Update user balance to negative (owing customer)
			user.setCurrentBalance(user.getCurrentBalance().subtract(lateFees));
			userRepository.save(user);
		}
		
		// Update rental record
		rental.setReturnFlag("RETURNED");
		rental.setReturnDate(returnRequest.getReturnDate());
		rental.setAdditionalNotes(returnRequest.getReturnNotes());
		rentalRepository.save(rental);
		
		// Update vehicle status to available
		vehicle.setVehicleRentalStatus("AVAILABLE");
		vehicleRepository.save(vehicle);
		
		// Create return response
		ReturnResponse returnResponse = new ReturnResponse(
			rental.getRentalId(),
			vehicle.getVehicleId(),
			vehicle.getMake() + " " + vehicle.getModel(),
			returnRequest.getReturnDate(),
			rental.getEndDate(),
			"$" + rental.getTotalCost().toString(),
			"$" + lateFees.toString(),
			"$" + (rental.getTotalCost().add(lateFees)).toString(),
			"Returned",
			returnRequest.getReturnNotes()
		);
		
		// Send return confirmation email
		emailService.sendReturnConfirmationEmail(user, vehicle, returnResponse);
		
		logger.info("Vehicle return successful. Rental ID: {}, User: {}, Vehicle: {}, Late Fees: {}", 
				rental.getRentalId(), username, vehicle.getVehicleId(), lateFees);
		
		return returnResponse;
	}
	
	@Transactional
	public Map<String, Object> payLateFee(LateFeePaymentRequest paymentRequest) {
		// Get current authenticated user
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get late fee record
		LateFee lateFee = lateFeeRepository.findById(paymentRequest.getLateFeeId())
				.orElseThrow(() -> new RuntimeException("Late fee record not found"));
		
		// Validate that the late fee belongs to the current user
		if (!lateFee.getUsername().equals(username)) {
			throw new RuntimeException("You can only pay late fees from your own account");
		}
		
		// Calculate remaining amount to pay
		BigDecimal remainingToPay = lateFee.getTotalCost().subtract(lateFee.getAmountPaid());
		
		// Validate payment amount doesn't exceed remaining amount
		if (paymentRequest.getPaymentAmount().compareTo(remainingToPay) > 0) {
			throw new RuntimeException("Payment amount cannot exceed the remaining amount to pay. Remaining: " + remainingToPay);
		}
		
		// Create payment record
		LateFeePayment payment = new LateFeePayment(
			lateFee.getId(), 
			username, 
			paymentRequest.getPaymentAmount(), 
			paymentRequest.getPaymentNotes()
		);
		lateFeePaymentRepository.save(payment);
		
		// Update late fee amount_paid
		BigDecimal newAmountPaid = lateFee.getAmountPaid().add(paymentRequest.getPaymentAmount());
		lateFee.setAmountPaid(newAmountPaid);
		lateFeeRepository.save(lateFee);
		
		// Update user balance
		user.setCurrentBalance(user.getCurrentBalance().add(paymentRequest.getPaymentAmount()));
		userRepository.save(user);
		
		// Calculate new remaining amount
		BigDecimal newRemainingAmount = lateFee.getTotalCost().subtract(newAmountPaid);
		
		// Create response
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Late fee payment successful");
		response.put("lateFeeId", lateFee.getId());
		response.put("paymentId", payment.getId());
		response.put("paymentAmount", paymentRequest.getPaymentAmount());
		response.put("totalAmountPaid", newAmountPaid);
		response.put("remainingAmount", newRemainingAmount);
		response.put("newBalance", user.getCurrentBalance());
		response.put("isFullyPaid", newRemainingAmount.compareTo(BigDecimal.ZERO) <= 0);
		
		logger.info("Late fee payment successful. Late Fee ID: {}, Payment ID: {}, User: {}, Payment: {}, Remaining: {}", 
				lateFee.getId(), payment.getId(), username, paymentRequest.getPaymentAmount(), newRemainingAmount);
		
		return response;
	}
	
	public List<LateFee> getUserLateFees(String username) {
		return lateFeeRepository.findByUsername(username);
	}
	
	public List<LateFeePayment> getLateFeePaymentHistory(Integer lateFeeId) {
		return lateFeePaymentRepository.findByLateFeeId(lateFeeId);
	}
	
	public List<LateFeePayment> getUserPaymentHistory(String username) {
		return lateFeePaymentRepository.findByUsername(username);
	}
} 