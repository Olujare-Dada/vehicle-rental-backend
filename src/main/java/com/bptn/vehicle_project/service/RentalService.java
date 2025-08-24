package com.bptn.vehicle_project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import com.bptn.vehicle_project.domain.InsufficientBalanceException;
import com.bptn.vehicle_project.domain.RentalRequest;
import com.bptn.vehicle_project.domain.LateFeePaymentRequest;
import com.bptn.vehicle_project.domain.LateFeeCalculationRequest;
import com.bptn.vehicle_project.domain.LateFeeCalculationResponse;
import com.bptn.vehicle_project.domain.RentalStatusUpdateRequest;
import com.bptn.vehicle_project.domain.RentalDetailsResponse;
import com.bptn.vehicle_project.domain.ReturnRequest;
import com.bptn.vehicle_project.domain.ReturnResponse;
import com.bptn.vehicle_project.domain.VehicleNotAvailableException;
import com.bptn.vehicle_project.domain.VehicleNotFoundException;
import com.bptn.vehicle_project.domain.EnhancedProfileResponse;
import com.bptn.vehicle_project.jpa.Profile;
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
	
	// Fixed daily late fee rate: $15.00 per day (no grace period)
	private static final BigDecimal DAILY_LATE_FEE_RATE = new BigDecimal("15.00");
	
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
	
	@Autowired
	private EntityManager entityManager;
	
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
		
		// Check for overlapping rentals for this user (excluding returned rentals)
		List<Rental> overlappingUserRentals = rentalRepository
				.findByUserUsernameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
						user.getUsername(), rentalRequest.getEndDate(), rentalRequest.getStartDate());
		
		// Filter out returned rentals
		List<Rental> activeOverlappingRentals = overlappingUserRentals.stream()
				.filter(rental -> !"RETURNED".equals(rental.getReturnFlag()))
				.collect(Collectors.toList());
		
		if (!activeOverlappingRentals.isEmpty()) {
			throw new RuntimeException("You already have an active rental during the selected period");
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
		
		// Debug logging for return request
		logger.info("Return request received - Rental ID: {} (type: {}), Return Date: {}, Notes: {}", 
			returnRequest.getRentalId(), 
			returnRequest.getRentalId() != null ? returnRequest.getRentalId().getClass().getSimpleName() : "NULL",
			returnRequest.getReturnDate(), 
			returnRequest.getReturnNotes());
		
		// Additional debugging for rental ID
		if (returnRequest.getRentalId() != null) {
			logger.info("Rental ID details - Value: {}, toString(): {}, hashCode(): {}", 
				returnRequest.getRentalId(), 
				returnRequest.getRentalId().toString(), 
				returnRequest.getRentalId().hashCode());
			
			// Check for potential precision issues
			if (returnRequest.getRentalId() instanceof Number) {
				Number num = (Number) returnRequest.getRentalId();
				logger.info("Rental ID as Number - intValue: {}, longValue: {}, doubleValue: {}", 
					num.intValue(), num.longValue(), num.doubleValue());
			}
		}
		
		// Check if rentalId is null
		if (returnRequest.getRentalId() == null) {
			throw new RuntimeException("Rental ID cannot be null");
		}
		
		// Check if rentalId is a valid positive integer
		if (returnRequest.getRentalId() <= 0) {
			throw new RuntimeException("Rental ID must be a positive integer, received: " + returnRequest.getRentalId());
		}
		
		// Debug: List all rentals for this user to see what's available
		List<Rental> userRentals = rentalRepository.findByUserUsername(username);
		logger.info("User {} has {} rentals total", username, userRentals.size());
		for (Rental r : userRentals) {
			logger.info("  Rental ID: {} (type: {}), Status: {}, Vehicle: {}", 
				r.getRentalId(), 
				r.getRentalId() != null ? r.getRentalId().getClass().getSimpleName() : "NULL",
				r.getReturnFlag(),
				r.getVehicle() != null ? r.getVehicle().getVehicleId() : "NULL");
		}
		
		// Debug: Try to find rental by ID and log the result
		Optional<Rental> rentalOpt = rentalRepository.findByRentalId(returnRequest.getRentalId());
		logger.info("Looking for rental with ID: {} (type: {}), Found: {}", 
			returnRequest.getRentalId(), 
			returnRequest.getRentalId() != null ? returnRequest.getRentalId().getClass().getSimpleName() : "NULL",
			rentalOpt.isPresent());
		
		// Debug: Get all rentals for comprehensive debugging
		List<Rental> allRentals = rentalRepository.findAll();
		
		// Debug: Try alternative approaches to find the rental
		if (!rentalOpt.isPresent()) {
			logger.warn("Rental not found by ID, trying alternative approaches...");
			
			// Try to find by user and vehicle combination (reuse existing userRentals)
			for (Rental r : userRentals) {
				if (r.getRentalId().equals(returnRequest.getRentalId())) {
					logger.info("Found rental by user search with matching ID: {}", r.getRentalId());
				}
			}
			
			// Try to find any rental with similar ID (in case of type mismatch)
			for (Rental r : allRentals) {
				if (r.getRentalId() != null && r.getRentalId().toString().equals(returnRequest.getRentalId().toString())) {
					logger.info("Found rental with string-matching ID: {} (actual: {})", 
						returnRequest.getRentalId(), r.getRentalId());
				}
			}
		}
		
		// Debug: Show total rentals count
		logger.info("Total rentals in database: {}", allRentals.size());
		if (!allRentals.isEmpty()) {
			Rental firstRental = allRentals.get(0);
			logger.info("First rental in database - ID: {} (type: {}), User: {}, Vehicle: {}", 
				firstRental.getRentalId(), 
				firstRental.getRentalId() != null ? firstRental.getRentalId().getClass().getSimpleName() : "NULL",
				firstRental.getUser() != null ? firstRental.getUser().getUsername() : "NULL",
				firstRental.getVehicle() != null ? firstRental.getVehicle().getVehicleId() : "NULL");
		}
		
		// Get rental record
		Rental rental = rentalOpt.orElseThrow(() -> {
			// Enhanced error message with debugging info
			StringBuilder errorMsg = new StringBuilder();
			errorMsg.append("Rental not found with ID: ").append(returnRequest.getRentalId());
			errorMsg.append(" (type: ").append(returnRequest.getRentalId() != null ? returnRequest.getRentalId().getClass().getSimpleName() : "NULL").append(")");
			errorMsg.append(". User: ").append(username);
			errorMsg.append(". Total rentals in database: ").append(allRentals.size());
			errorMsg.append(". User rentals: ").append(userRentals.size());
			
			// List available rental IDs for this user
			if (!userRentals.isEmpty()) {
				errorMsg.append(". Available rental IDs: ");
				for (Rental r : userRentals) {
					errorMsg.append(r.getRentalId()).append("(").append(r.getReturnFlag()).append(") ");
				}
			}
			
			return new RuntimeException(errorMsg.toString());
		});
		
		// Debug logging for rental object
		logger.info("Rental object loaded - ID: {}, User: {}, Vehicle: {}, Status: {}", 
			rental.getRentalId(), 
			rental.getUser() != null ? rental.getUser().getUsername() : "NULL", 
			rental.getVehicle() != null ? rental.getVehicle().getVehicleId() : "NULL", 
			rental.getReturnFlag());
		
		// Validate that the rental belongs to the current user
		if (!rental.getUser().getUsername().equals(username)) {
			throw new RuntimeException("You can only return vehicles from your own rentals");
		}
		
		// Validate that the rental is active - but allow re-processing if needed
		if ("RETURNED".equals(rental.getReturnFlag())) {
			logger.warn("Rental {} was already marked as returned, but processing return again", rental.getRentalId());
		}
		
		// Get vehicle
		Vehicle vehicle = rental.getVehicle();
		
		// Calculate late fees if returned after end date
		BigDecimal lateFees = BigDecimal.ZERO;
		if (returnRequest.getReturnDate().isAfter(rental.getEndDate())) {
			// Calculate late days with Math.ceil() rounding for partial days
			double lateDaysRaw = ChronoUnit.DAYS.between(rental.getEndDate(), returnRequest.getReturnDate());
			int lateDays = (int) Math.ceil(lateDaysRaw);
			
			// Fixed daily rate: $15.00 per day late (no grace period)
			lateFees = DAILY_LATE_FEE_RATE.multiply(BigDecimal.valueOf(lateDays));
			
			logger.info("Late fee calculation - Username: {}, Rental ID: {}, Days Late: {}, Amount: {}", 
				username, rental.getRentalId(), lateDays, lateFees);
			
			// Check if user has sufficient balance to pay late fees
			if (user.getCurrentBalance().compareTo(lateFees) < 0) {
				throw new InsufficientBalanceException(
					"Insufficient balance to pay late fees. Required: $" + lateFees + 
					", Available: $" + user.getCurrentBalance() + 
					". Please add funds to your account before returning the vehicle."
				);
			}
			
			// Validate rental ID is not null
			if (rental.getRentalId() == null) {
				throw new RuntimeException("Rental ID cannot be null when creating late fee");
			}
			
			// Create late fee record
			LateFee lateFee = new LateFee(username, rental.getRentalId(), lateDays, lateFees);
			lateFeeRepository.save(lateFee);
			
			// Deduct late fees from user balance immediately
			user.setCurrentBalance(user.getCurrentBalance().subtract(lateFees));
			userRepository.save(user);
			
			// Create payment record in late_fee_payments table
			LateFeePayment payment = new LateFeePayment(
				lateFee.getId(),
				username,
				lateFees,
				"Automatic payment for late return - " + lateDays + " days late"
			);
			lateFeePaymentRepository.save(payment);
			
			// Update the late fee amount_paid field
			lateFee.setAmountPaid(lateFees);
			lateFeeRepository.save(lateFee);
			
			logger.info("Late fee paid immediately for rental {}: ${} for {} days. New balance: ${}", 
				rental.getRentalId(), lateFees, lateDays, user.getCurrentBalance());
		}
		
		// Update rental record
		rental.setReturnFlag("RETURNED");
		rental.setReturnDate(returnRequest.getReturnDate());
		rental.setAdditionalNotes(returnRequest.getReturnNotes());
		Rental savedRental = rentalRepository.save(rental);
		
		// Update vehicle status to available
		logger.debug("Before update - Vehicle {} status: {}", vehicle.getVehicleId(), vehicle.getVehicleRentalStatus());
		vehicle.setVehicleRentalStatus("AVAILABLE");
		logger.debug("After setting status - Vehicle {} status: {}", vehicle.getVehicleId(), vehicle.getVehicleRentalStatus());
		
		logger.debug("About to save vehicle with ID: {} and status: {}", vehicle.getVehicleId(), vehicle.getVehicleRentalStatus());
		Vehicle savedVehicle = vehicleRepository.save(vehicle);
		logger.debug("After save - Vehicle {} status: {}", savedVehicle.getVehicleId(), savedVehicle.getVehicleRentalStatus());
		
		// Force a flush to ensure the update is written to the database
		vehicleRepository.flush();
		logger.debug("After flush - Vehicle {} status: {}", savedVehicle.getVehicleId(), savedVehicle.getVehicleRentalStatus());
		
		// Verify the updates were saved
		logger.info("Rental {} updated to status: {}", savedRental.getRentalId(), savedRental.getReturnFlag());
		logger.info("Vehicle {} updated to status: {}", savedVehicle.getVehicleId(), savedVehicle.getVehicleRentalStatus());
		
		// Double-check by reloading the vehicle from database
		Vehicle reloadedVehicle = vehicleRepository.findById(vehicle.getVehicleId()).orElse(null);
		if (reloadedVehicle != null) {
			logger.info("Reloaded vehicle {} status from database: {}", reloadedVehicle.getVehicleId(), reloadedVehicle.getVehicleRentalStatus());
		} else {
			logger.error("Failed to reload vehicle {} from database", vehicle.getVehicleId());
		}
		
		// Additional debugging for vehicle status update
		logger.debug("Final vehicle status check - Vehicle {} status: {}", vehicle.getVehicleId(), vehicle.getVehicleRentalStatus());
		
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
		
		// Update the late fee amount_paid field
		lateFee.setAmountPaid(lateFee.getAmountPaid().add(paymentRequest.getPaymentAmount()));
		lateFeeRepository.save(lateFee);
		
		// Update user balance (add payment to balance)
		user.setCurrentBalance(user.getCurrentBalance().add(paymentRequest.getPaymentAmount()));
		userRepository.save(user);
		
		// Calculate new remaining amount
		BigDecimal newRemainingAmount = remainingToPay.subtract(paymentRequest.getPaymentAmount());
		
		// Create response
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Late fee payment successful");
		response.put("lateFeeId", lateFee.getId());
		response.put("paymentId", payment.getId());
		response.put("paymentAmount", paymentRequest.getPaymentAmount());
		response.put("totalAmountPaid", lateFee.getAmountPaid());
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
	
	/**
	 * Find unpaid late fees for a user
	 */
	public List<LateFee> findUnpaidLateFeesByUsername(String username) {
		List<LateFee> allLateFees = lateFeeRepository.findByUsername(username);
		
		// Filter to only unpaid late fees
		return allLateFees.stream()
			.filter(lateFee -> {
				// Return true if still unpaid (amountPaid < totalCost)
				return lateFee.getAmountPaid().compareTo(lateFee.getTotalCost()) < 0;
			})
			.collect(Collectors.toList());
	}
	
	/**
	 * Calculate total outstanding late fees for a user
	 */
	public BigDecimal calculateTotalOutstandingLateFees(String username) {
		List<LateFee> allLateFees = lateFeeRepository.findByUsername(username);
		
		BigDecimal totalOutstanding = BigDecimal.ZERO;
		
		for (LateFee lateFee : allLateFees) {
			// Calculate outstanding amount directly from amountPaid field
			BigDecimal outstanding = lateFee.getTotalCost().subtract(lateFee.getAmountPaid());
			if (outstanding.compareTo(BigDecimal.ZERO) > 0) {
				totalOutstanding = totalOutstanding.add(outstanding);
			}
		}
		
		return totalOutstanding;
	}
	
	/**
	 * Check if user has any outstanding late fees
	 */
	public boolean hasOutstandingLateFees(String username) {
		return calculateTotalOutstandingLateFees(username).compareTo(BigDecimal.ZERO) > 0;
	}
	
	/**
	 * Check if user can afford to return a vehicle with late fees
	 */
	public Map<String, Object> checkReturnAffordability(Integer rentalId, LocalDate returnDate) {
		Map<String, Object> result = new HashMap<>();
		
		// Get rental
		Rental rental = rentalRepository.findById(rentalId)
				.orElseThrow(() -> new RuntimeException("Rental not found"));
		
		// Get user
		User user = rental.getUser();
		
		// Calculate late fees if returned after end date
		BigDecimal lateFees = BigDecimal.ZERO;
		boolean isLate = false;
		int daysLate = 0;
		
		if (returnDate.isAfter(rental.getEndDate())) {
			// Calculate late days with Math.ceil() rounding for partial days
			double lateDaysRaw = ChronoUnit.DAYS.between(rental.getEndDate(), returnDate);
			daysLate = (int) Math.ceil(lateDaysRaw);
			
			// Fixed daily rate: $15.00 per day late (no grace period)
			lateFees = DAILY_LATE_FEE_RATE.multiply(BigDecimal.valueOf(daysLate));
			isLate = true;
		}
		
		// Check if user can afford the late fees
		boolean canAfford = user.getCurrentBalance().compareTo(lateFees) >= 0;
		
		result.put("canReturn", canAfford);
		result.put("isLate", isLate);
		result.put("daysLate", daysLate);
		result.put("lateFees", lateFees);
		result.put("userBalance", user.getCurrentBalance());
		result.put("requiredBalance", lateFees);
		result.put("message", canAfford ? 
			"Vehicle can be returned. Late fees: $" + lateFees : 
			"Insufficient balance. Required: $" + lateFees + ", Available: $" + user.getCurrentBalance());
		
		return result;
	}
	
	/**
	 * Calculate late fees for a rental
	 */
	public LateFeeCalculationResponse calculateLateFees(LateFeeCalculationRequest request) {
		logger.debug("Calculating late fees for rental ID: {}", request.getRentalId());
		
		// Get rental
		Rental rental = rentalRepository.findById(request.getRentalId())
				.orElseThrow(() -> new RuntimeException("Rental not found"));
		
		// Get vehicle for daily rate
		Vehicle vehicle = vehicleRepository.findByVehicleId(rental.getVehicle().getVehicleId())
				.orElseThrow(() -> new RuntimeException("Vehicle not found"));
		
		// Calculate days late
		LocalDate expectedReturn = request.getExpectedReturnDate() != null ? 
			request.getExpectedReturnDate() : rental.getEndDate();
		LocalDate actualReturn = request.getActualReturnDate();
		
		// Calculate late days with Math.ceil() rounding for partial days
		double lateDaysRaw = ChronoUnit.DAYS.between(expectedReturn, actualReturn);
		int daysLate = (int) Math.ceil(lateDaysRaw);
		
		// If not late, return zero fees
		if (daysLate <= 0) {
			return new LateFeeCalculationResponse(
				BigDecimal.ZERO, 0, DAILY_LATE_FEE_RATE, 
				"No late fees - vehicle returned on time or early"
			);
		}
		
		// Calculate late fee (fixed daily rate * days late)
		BigDecimal lateFeeAmount = DAILY_LATE_FEE_RATE.multiply(BigDecimal.valueOf(daysLate));
		
		logger.info("Late fee calculated. Rental ID: {}, Days Late: {}, Daily Rate: {}, Late Fee: {}", 
				request.getRentalId(), daysLate, DAILY_LATE_FEE_RATE, lateFeeAmount);
		
		return new LateFeeCalculationResponse(
			lateFeeAmount, (int) daysLate, DAILY_LATE_FEE_RATE, 
			"Late fee calculated successfully"
		);
	}
	
	/**
	 * Update rental status
	 */
	@Transactional
	public Map<String, Object> updateRentalStatus(Integer rentalId, RentalStatusUpdateRequest request) {
		logger.debug("Updating rental status for rental ID: {}", rentalId);
		
		// Get rental
		Rental rental = rentalRepository.findById(rentalId)
				.orElseThrow(() -> new RuntimeException("Rental not found"));
		
		// Update rental fields
		if (request.getStatus() != null) {
			rental.setReturnFlag(request.getStatus());
		}
		if (request.getActualReturnDate() != null) {
			rental.setReturnDate(request.getActualReturnDate());
		}
		
		// Save updated rental
		Rental updatedRental = rentalRepository.save(rental);
		
		// If there are late fees, create late fee record
		if (request.getLateFeeAmount() != null && request.getLateFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
			// Get current user to check balance
			User currentUser = rental.getUser();
			
			// Check if user has sufficient balance to pay late fees
			if (currentUser.getCurrentBalance().compareTo(request.getLateFeeAmount()) < 0) {
				throw new InsufficientBalanceException(
					"Insufficient balance to pay late fees. Required: $" + request.getLateFeeAmount() + 
					", Available: $" + currentUser.getCurrentBalance() + 
					". Please add funds to your account before updating rental status."
				);
			}
			
			// Calculate days late for the late fee record
			LocalDate expectedReturn = rental.getEndDate();
			LocalDate actualReturn = request.getActualReturnDate();
			int daysLate = 0;
			
			if (actualReturn != null && expectedReturn != null) {
				// Calculate late days with Math.ceil() rounding for partial days
				double lateDaysRaw = ChronoUnit.DAYS.between(expectedReturn, actualReturn);
				daysLate = (int) Math.ceil(lateDaysRaw);
			}
			
			LateFee lateFee = new LateFee(
				currentUser.getUsername(), 
				rentalId, 
				daysLate, 
				request.getLateFeeAmount()
			);
			lateFeeRepository.save(lateFee);
			
			// Deduct late fees from user balance immediately
			currentUser.setCurrentBalance(currentUser.getCurrentBalance().subtract(request.getLateFeeAmount()));
			userRepository.save(currentUser);
			
			// Create payment record in late_fee_payments table
			LateFeePayment payment = new LateFeePayment(
				lateFee.getId(),
				currentUser.getUsername(),
				request.getLateFeeAmount(),
				"Payment for rental status update - " + daysLate + " days late"
			);
			lateFeePaymentRepository.save(payment);
			
			// Update the late fee amount_paid field
			lateFee.setAmountPaid(request.getLateFeeAmount());
			lateFeeRepository.save(lateFee);
			
			logger.info("Late fee paid immediately for rental {}: ${} for {} days. New balance: ${}", 
				rentalId, request.getLateFeeAmount(), daysLate, currentUser.getCurrentBalance());
		}
		
		// Create response
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Rental status updated successfully");
		response.put("rental", updatedRental);
		
		logger.info("Rental status updated successfully. Rental ID: {}, New Status: {}", rentalId, request.getStatus());
		
		return response;
	}
	
	/**
	 * Get rental details with late fee information
	 */
	public RentalDetailsResponse getRentalDetails(Integer rentalId) {
		logger.debug("Getting rental details for rental ID: {}", rentalId);
		
		// Get rental
		Rental rental = rentalRepository.findById(rentalId)
				.orElseThrow(() -> new RuntimeException("Rental not found"));
		
		// Get vehicle
		Vehicle vehicle = vehicleRepository.findByVehicleId(rental.getVehicle().getVehicleId())
				.orElseThrow(() -> new RuntimeException("Vehicle not found"));
		
		// Create rental info
		RentalDetailsResponse.RentalInfo rentalInfo = new RentalDetailsResponse.RentalInfo(
			rental.getRentalId(),
			vehicle.getVehicleId(),
			vehicle.getMake() + " " + vehicle.getModel(),
			rental.getStartDate(),
			rental.getEndDate(),
			rental.getReturnDate(),
			rental.getTotalCost(),
			rental.getReturnFlag(),
			rental.getAdditionalNotes()
		);
		
		// Calculate late fee info
		LocalDate expectedReturn = rental.getEndDate();
		LocalDate actualReturn = rental.getReturnDate();
		
		boolean isLate = false;
		int daysLate = 0;
		BigDecimal lateFeeAmount = BigDecimal.ZERO;
		BigDecimal dailyRate = DAILY_LATE_FEE_RATE; // Fixed rate for late fees
		
		if (actualReturn != null && expectedReturn != null) {
			// Calculate late days with Math.ceil() rounding for partial days
			double lateDaysRaw = ChronoUnit.DAYS.between(expectedReturn, actualReturn);
			daysLate = (int) Math.ceil(lateDaysRaw);
			isLate = daysLate > 0;
			
			if (isLate) {
				// Use fixed daily rate for late fees
				lateFeeAmount = DAILY_LATE_FEE_RATE.multiply(BigDecimal.valueOf(daysLate));
			}
		}
		
		// Create late fee info
		RentalDetailsResponse.LateFeeInfo lateFeeInfo = new RentalDetailsResponse.LateFeeInfo(
			isLate, daysLate, lateFeeAmount, dailyRate
		);
		
		logger.info("Rental details retrieved successfully. Rental ID: {}", rentalId);
		
		return new RentalDetailsResponse(rentalInfo, lateFeeInfo);
	}
	
	/**
	 * Get enhanced profile information including rental history with vehicle images
	 */
	public EnhancedProfileResponse getEnhancedProfile(String username) {
		logger.debug("Getting enhanced profile for username: {}", username);
		
		// Get user
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		// Get profile
		Profile profile = user.getProfile();
		if (profile == null) {
			throw new RuntimeException("Profile not found for user: " + username);
		}
		
		// Get all rentals for the user
		List<Rental> userRentals = rentalRepository.findByUserUsername(username);
		
		// Convert rentals to rental history with vehicle images
		List<EnhancedProfileResponse.RentalHistory> rentalHistory = userRentals.stream()
				.map(rental -> {
					Vehicle vehicle = rental.getVehicle();
					
					// Calculate late fees if applicable
					BigDecimal lateFees = BigDecimal.ZERO;
					boolean isLate = false;
					
					if (rental.getReturnDate() != null && rental.getEndDate() != null) {
						if (rental.getReturnDate().isAfter(rental.getEndDate())) {
							double lateDaysRaw = ChronoUnit.DAYS.between(rental.getEndDate(), rental.getReturnDate());
							int lateDays = (int) Math.ceil(lateDaysRaw);
							lateFees = DAILY_LATE_FEE_RATE.multiply(BigDecimal.valueOf(lateDays));
							isLate = true;
						}
					}
					
					return new EnhancedProfileResponse.RentalHistory(
						rental.getRentalId(),
						vehicle.getVehicleId(),
						vehicle.getMake() + " " + vehicle.getModel(),
						vehicle.getVehicleImageUrl(),
						vehicle.getMake(),
						vehicle.getModel(),
						vehicle.getYear(),
						vehicle.getColor(),
						rental.getStartDate(),
						rental.getEndDate(),
						rental.getReturnDate(),
						rental.getTotalCost(),
						rental.getReturnFlag(),
						rental.getAdditionalNotes(),
						null, // Rental doesn't have createdOn field
						lateFees,
						isLate
					);
				})
				.collect(Collectors.toList());
		
		// Calculate statistics
		int totalRentals = userRentals.size();
		int activeRentals = (int) userRentals.stream()
				.filter(rental -> !"RETURNED".equals(rental.getReturnFlag()))
				.count();
		
		// Create profile info
		EnhancedProfileResponse.ProfileInfo profileInfo = new EnhancedProfileResponse.ProfileInfo(
			profile.getProfileId(),
			profile.getUser().getUsername(),
			profile.getBio() != null ? profile.getBio() : "",
			profile.getCity() != null ? profile.getCity() : "",
			profile.getCountry() != null ? profile.getCountry() : "",
			profile.getHeadline() != null ? profile.getHeadline() : "",
			profile.getPicture() != null ? profile.getPicture() : ""
		);
		
		// Create user info
		EnhancedProfileResponse.UserInfo userInfo = new EnhancedProfileResponse.UserInfo(
			user.getUsername(),
			user.getFirstName(),
			user.getLastName(),
			user.getEmail(),
			user.getPhone(),
			user.getAddress(),
			user.getLicense(),
			user.getLicenseState(),
			user.getLicenseExpiry(),
			user.getDateOfBirth(),
			user.getCreatedOn()
		);
		
		logger.info("Enhanced profile retrieved successfully for user: {}", username);
		
		return new EnhancedProfileResponse(
			profileInfo,
			userInfo,
			rentalHistory,
			user.getCurrentBalance(),
			totalRentals,
			activeRentals
		);
	}
} 