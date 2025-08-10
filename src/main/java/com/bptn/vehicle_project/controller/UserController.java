package com.bptn.vehicle_project.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bptn.vehicle_project.domain.BalanceDebitRequest;
import com.bptn.vehicle_project.domain.BalanceUpdateRequest;
import com.bptn.vehicle_project.domain.FleetResponse;
import com.bptn.vehicle_project.domain.InsufficientBalanceException;
import com.bptn.vehicle_project.domain.LoginRequest;
import com.bptn.vehicle_project.domain.LogoutRequest;
import com.bptn.vehicle_project.domain.LogoutResponse;
import com.bptn.vehicle_project.domain.ProfileResponse;
import com.bptn.vehicle_project.domain.RentalRequest;
import com.bptn.vehicle_project.domain.UserResponse;
import com.bptn.vehicle_project.jpa.Profile;
import com.bptn.vehicle_project.jpa.Rental;
import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.jpa.Vehicle;
import com.bptn.vehicle_project.security.JwtService;
import com.bptn.vehicle_project.service.FleetService;
import com.bptn.vehicle_project.service.ProfileService;
import com.bptn.vehicle_project.service.RentalService;
import com.bptn.vehicle_project.service.UserService;
import com.bptn.vehicle_project.service.ProfileImageService;
import com.bptn.vehicle_project.service.LogoutService;
import com.bptn.vehicle_project.repository.VehicleRepository;
import com.bptn.vehicle_project.repository.RentalRepository;

@RestController
public class UserController {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	UserService userService;
	
	@Autowired
	RentalService rentalService;
	
	@Autowired
	FleetService fleetService;
	
	@Autowired
	ProfileService profileService;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	VehicleRepository vehicleRepository;
	
	@Autowired
	RentalRepository rentalRepository;
	
	@Autowired
	ProfileImageService profileImageService;
	
	@Autowired
	LogoutService logoutService;
	
	@GetMapping("/test")
	public String testController() {
		logger.debug("The testController() method was invoked!");
		return "The feedApp application is up and running";
	}
	
	@PostMapping("/test-post")
	public String testPostController() {
		logger.debug("The testPostController() method was invoked!");
		return "POST endpoint is working";
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
		logger.info("Login endpoint called with username: {}", loginRequest.getUsername());
		try {
			logger.debug("Login attempt for username: {}", loginRequest.getUsername());
			
			// Create a User object for authentication
			User user = new User();
			user.setUsername(loginRequest.getUsername());
			user.setPassword(loginRequest.getPassword());
			
			// Authenticate user
			User authenticatedUser = this.userService.authenticate(user);
			
			// Generate JWT token
			String jwtToken = this.userService.generateJwtToken(authenticatedUser.getUsername());
			
			// Return JWT token in response
			return ResponseEntity.ok("{\"message\": \"Login successful\", \"token\": \"" + jwtToken + "\", \"user\": {\"username\": \"" + authenticatedUser.getUsername() + "\", \"email\": \"" + authenticatedUser.getEmail() + "\", \"firstName\": \"" + authenticatedUser.getFirstName() + "\", \"lastName\": \"" + authenticatedUser.getLastName() + "\"}}");
		} catch (IllegalArgumentException e) {
			logger.error("Authentication error: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (Exception e) {
			logger.error("Login error: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("{\"error\": \"Invalid username or password\"}");
		}
	}
	
	@GetMapping("/user/{username}")
	public ResponseEntity<String> findByUsername(@PathVariable String username) {
		try {
			logger.debug("The findByUsername() method was invoked!, username= {}", username);
			
			// Get current authenticated user
			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
			
			// Only allow users to access their own data or admin access
			if (!currentUsername.equals(username)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("{\"error\": \"Access denied. You can only view your own profile.\"}");
			}
			
			Optional<User> user = this.userService.findByUsername(username);
			if (user.isPresent()) {
				return ResponseEntity.ok("{\"message\": \"User found\", \"user\": {\"username\": \"" + user.get().getUsername() + 
						"\", \"email\": \"" + user.get().getEmail() + "\", \"firstName\": \"" + user.get().getFirstName() + 
						"\", \"lastName\": \"" + user.get().getLastName() + "\", \"currentBalance\": \"" + user.get().getCurrentBalance() + "\"}}");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("{\"error\": \"User not found\"}");
			}
		} catch (Exception e) {
			logger.error("Error finding user: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred while retrieving user data\"}");
		}
	}
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody User user) {
		try {
			logger.debug("Signing up, username: {}", user.getUsername());
			
			this.userService.signup(user);
			
			return ResponseEntity.ok("{\"message\": \"User registered successfully. Please check your email for verification.\"}");
		} catch (IllegalArgumentException e) {
			logger.error("Validation error during signup: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (Exception e) {
			logger.error("Error during signup: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred during registration. Please try again.\"}");
		}
	}
	
	@GetMapping("/user/verifyEmail")
	public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
		try {
			logger.debug("Verifying email with token: {}", token);
			this.userService.verifyEmailByToken(token);
			
			// Redirect to frontend with success status
			return ResponseEntity.status(HttpStatus.FOUND)
					.header("Location", "http://localhost:3000/email-verification?status=success")
					.build();
		} catch (Exception e) {
			logger.error("Error verifying email: {}", e.getMessage());
			
			// Redirect to frontend with error status
			return ResponseEntity.status(HttpStatus.FOUND)
					.header("Location", "http://localhost:3000/email-verification?status=error&message=" + e.getMessage())
					.build();
		}
	}
	//username, firstname, lastname, email, phone, address, password, license, currentBalance
	@PostMapping("/rent")
	public ResponseEntity<String> rentVehicle(@RequestBody RentalRequest rentalRequest) {
		try {
			logger.debug("Rental request for vehicle: {}, startDate: {}, endDate: {}", 
					rentalRequest.getVehicleId(), rentalRequest.getStartDate(), rentalRequest.getEndDate());
			
			Rental rental = rentalService.rentVehicle(rentalRequest);
			
			return ResponseEntity.ok("{\"message\": \"Vehicle rented successfully\", \"rentalId\": " + rental.getRentalId() + 
					", \"totalCost\": \"" + rental.getTotalCost() + "\"}");
		} catch (InsufficientBalanceException e) {
			logger.error("Insufficient balance error during rental: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (IllegalArgumentException e) {
			logger.error("Validation error during rental: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (Exception e) {
			logger.error("Error during rental: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred during rental. Please try again.\"}");
		}
	}
	
	@GetMapping("/fleet")
	public ResponseEntity<FleetResponse> getFleet(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			logger.debug("Fleet request - page: {}, size: {}", page, size);
			
			FleetResponse fleetResponse = fleetService.getFleet(page, size);
			
			if (fleetResponse.isSuccess()) {
				return ResponseEntity.ok(fleetResponse);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fleetResponse);
			}
		} catch (Exception e) {
			logger.error("Error retrieving fleet: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new FleetResponse(false, "Error retrieving fleet data", null));
		}
	}
	
	@GetMapping("/fleet/available")
	public ResponseEntity<FleetResponse> getAvailableFleet(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			logger.debug("Available fleet request - page: {}, size: {}", page, size);
			
			FleetResponse fleetResponse = fleetService.getAvailableFleet(page, size);
			
			if (fleetResponse.isSuccess()) {
				return ResponseEntity.ok(fleetResponse);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fleetResponse);
			}
		} catch (Exception e) {
			logger.error("Error retrieving available fleet: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new FleetResponse(false, "Error retrieving available fleet data", null));
		}
	}
	
	@GetMapping("/fleet/category/{categoryId}")
	public ResponseEntity<FleetResponse> getFleetByCategory(
			@PathVariable Integer categoryId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			logger.debug("Fleet by category request - categoryId: {}, page: {}, size: {}", categoryId, page, size);
			
			FleetResponse fleetResponse = fleetService.getFleetByCategory(categoryId, page, size);
			
			if (fleetResponse.isSuccess()) {
				return ResponseEntity.ok(fleetResponse);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fleetResponse);
			}
		} catch (Exception e) {
			logger.error("Error retrieving fleet by category: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new FleetResponse(false, "Error retrieving fleet data by category", null));
		}
	}
	
	@GetMapping("/profile/{username}")
	public ResponseEntity<ProfileResponse> getProfileByUsername(@PathVariable String username) {
		try {
			logger.debug("Profile get request for username: {}", username);
			
			Profile profile = profileService.getProfileByUsername(username);
			
			// Create response DTO matching frontend expectations
			ProfileResponse response = new ProfileResponse(
				profile.getProfileId(),
				username,
				profile.getBio() != null ? profile.getBio() : "",
				profile.getCity() != null ? profile.getCity() : "",
				profile.getCountry() != null ? profile.getCountry() : "",
				profile.getHeadline() != null ? profile.getHeadline() : "",
				profile.getPicture() != null ? profile.getPicture() : ""
			);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error retrieving profile for username {}: {}", username, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PutMapping("/profile")
	public ResponseEntity<String> updateProfile(@RequestBody Profile profile) {
		try {
			logger.debug("Profile update request for user");
			
			// Get current authenticated user
			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
			
			Profile updatedProfile = profileService.updateProfile(currentUsername, profile);
			
			return ResponseEntity.ok("{\"message\": \"Profile updated successfully\", \"profile\": {\"bio\": \"" + updatedProfile.getBio() + "\", \"city\": \"" + updatedProfile.getCity() + "\", \"country\": \"" + updatedProfile.getCountry() + "\", \"headline\": \"" + updatedProfile.getHeadline() + "\", \"picture\": \"" + updatedProfile.getPicture() + "\"}}");
		} catch (IllegalArgumentException e) {
			logger.error("Validation error during profile update: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (Exception e) {
			logger.error("Error during profile update: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred during profile update. Please try again.\"}");
		}
	}
	
	@PostMapping("/verify-token")
	public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authHeader) {
		try {
			logger.debug("JWT token verification request");
			
			// Remove "Bearer " prefix
			String token = authHeader.substring(7);
			
			// Verify the JWT token
			String username = jwtService.getSubject(token);
			
			// Get user details
			Optional<User> userOpt = userService.findByUsername(username);
			if (userOpt.isPresent()) {
				User user = userOpt.get();
				
				// Create UserResponse DTO to avoid lazy loading issues
				UserResponse userResponse = new UserResponse(
					user.getUsername(),
					user.getFirstName(),
					user.getLastName(),
					user.getEmail(),
					user.getPhone(),
					user.getAddress(),
					user.getLicense(),
					user.getCurrentBalance(),
					user.getEmailVerified(),
					user.getCreatedOn(),
					user.getCity(),
					user.getState(),
					user.getZipcode(),
					user.getLicenseExpiry(),
					user.getLicenseState(),
					user.getDateOfBirth()
				);
				
				Map<String, Object> response = new HashMap<>();
				response.put("valid", true);
				response.put("user", userResponse); // Return DTO instead of entity
				response.put("message", "Token is valid");
				
				return ResponseEntity.ok(response);
			} else {
				Map<String, Object> response = new HashMap<>();
				response.put("valid", false);
				response.put("message", "User not found");
				
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
		} catch (Exception e) {
			logger.error("Error verifying JWT token: {}", e.getMessage());
			
			Map<String, Object> response = new HashMap<>();
			response.put("valid", false);
			response.put("message", "Invalid or expired token");
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
	}
	
	@PostMapping("/balance/add")
	public ResponseEntity<String> addBalance(@RequestBody BalanceUpdateRequest balanceRequest) {
		try {
			logger.debug("Balance add request - amount: {}, description: {}", 
					balanceRequest.getAmount(), balanceRequest.getDescription());
			
			// Get current authenticated user
			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
			
			// Update balance
			User updatedUser = userService.updateBalance(currentUsername, balanceRequest.getAmount(), balanceRequest.getDescription());
			
			return ResponseEntity.ok("{\"message\": \"Balance updated successfully\", \"newBalance\": \"" + updatedUser.getCurrentBalance() + "\", \"addedAmount\": \"" + balanceRequest.getAmount() + "\"}");
		} catch (IllegalArgumentException e) {
			logger.error("Validation error during balance update: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (Exception e) {
			logger.error("Error during balance update: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred during balance update. Please try again.\"}");
		}
	}
	
	@GetMapping("/balance")
	public ResponseEntity<String> getBalance() {
		try {
			logger.debug("Balance check request");
			
			// Get current authenticated user
			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
			
			// Get balance
			BigDecimal balance = userService.getBalance(currentUsername);
			
			return ResponseEntity.ok("{\"balance\": \"" + balance + "\", \"message\": \"Balance retrieved successfully\"}");
		} catch (Exception e) {
			logger.error("Error retrieving balance: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred while retrieving balance\"}");
		}
	}
	
	@PostMapping("/balance/debit")
	public ResponseEntity<String> debitBalance(@RequestBody BalanceDebitRequest debitRequest) {
		try {
			logger.debug("Balance debit request - amount: {}, description: {}", 
					debitRequest.getAmount(), debitRequest.getDescription());
			
			// Get current authenticated user
			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
			
			// Debit balance
			User updatedUser = userService.debitBalance(currentUsername, debitRequest.getAmount(), debitRequest.getDescription());
			
			return ResponseEntity.ok("{\"message\": \"Balance debited successfully\", \"newBalance\": \"" + updatedUser.getCurrentBalance() + "\", \"debitedAmount\": \"" + debitRequest.getAmount() + "\"}");
		} catch (InsufficientBalanceException e) {
			logger.error("Insufficient balance error during debit: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (IllegalArgumentException e) {
			logger.error("Validation error during balance debit: {}", e.getMessage());
			return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (Exception e) {
			logger.error("Error during balance debit: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred during balance debit. Please try again.\"}");
		}
	}
	
	@GetMapping("/debug/vehicle-statuses")
	public ResponseEntity<String> getVehicleStatuses() {
		try {
			logger.debug("Debug request for vehicle statuses");
			
			// Get all vehicles and their statuses
			List<Vehicle> vehicles = vehicleRepository.findAll();
			StringBuilder response = new StringBuilder();
			response.append("{\"vehicleStatuses\": [");
			
			for (int i = 0; i < vehicles.size(); i++) {
				Vehicle vehicle = vehicles.get(i);
				response.append("{");
				response.append("\"vehicleId\": ").append(vehicle.getVehicleId()).append(",");
				response.append("\"name\": \"").append(vehicle.getMake()).append(" ").append(vehicle.getModel()).append("\",");
				response.append("\"status\": \"").append(vehicle.getVehicleRentalStatus()).append("\"");
				response.append("}");
				if (i < vehicles.size() - 1) {
					response.append(",");
				}
			}
			
			response.append("]}");
			
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			logger.error("Error retrieving vehicle statuses: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred while retrieving vehicle statuses\"}");
		}
	}
	
	@GetMapping("/rentals/user")
	public ResponseEntity<String> getUserRentals(@RequestHeader("Authorization") String authHeader) {
		try {
			logger.debug("User rentals request");
			
			// Remove "Bearer " prefix and get username from token
			String token = authHeader.substring(7);
			String username = jwtService.getSubject(token);
			
			// Get user's rentals
			List<Rental> userRentals = rentalRepository.findByUserUsername(username);
			
			StringBuilder response = new StringBuilder();
			response.append("{\"rentals\": [");
			
			for (int i = 0; i < userRentals.size(); i++) {
				Rental rental = userRentals.get(i);
				Vehicle vehicle = rental.getVehicle();
				
				response.append("{");
				response.append("\"rentalId\": ").append(rental.getRentalId()).append(",");
				response.append("\"vehicleId\": ").append(vehicle.getVehicleId()).append(",");
				response.append("\"vehicleName\": \"").append(vehicle.getMake()).append(" ").append(vehicle.getModel()).append("\",");
				response.append("\"vehicleType\": \"").append(vehicle.getCategory() != null ? vehicle.getCategory().getName() : "Unknown").append("\",");
				response.append("\"startDate\": \"").append(rental.getStartDate().toString()).append("\",");
				response.append("\"endDate\": \"").append(rental.getEndDate().toString()).append("\",");
				response.append("\"totalCost\": \"$").append(rental.getTotalCost()).append("\",");
				response.append("\"status\": \"").append("true".equals(rental.getReturnFlag()) ? "Returned" : "Active").append("\",");
				response.append("\"additionalNotes\": \"").append(rental.getAdditionalNotes() != null ? rental.getAdditionalNotes() : "").append("\"");
				response.append("}");
				
				if (i < userRentals.size() - 1) {
					response.append(",");
				}
			}
			
			response.append("]}");
			
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			logger.error("Error retrieving user rentals: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred while retrieving user rentals\"}");
		}
	}
	
	@PostMapping("/profile/upload-image")
	public ResponseEntity<Map<String, Object>> uploadProfileImage(
			@RequestParam("profile_image") MultipartFile profileImage,
			@RequestHeader("Authorization") String authHeader) {
		try {
			logger.debug("Profile image upload request");
			
			// Validate file
			if (profileImage.isEmpty()) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("error", "No file uploaded");
				return ResponseEntity.badRequest().body(response);
			}
			
			// Validate file size (10MB limit)
			if (profileImage.getSize() > 10 * 1024 * 1024) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("error", "File size too large");
				return ResponseEntity.badRequest().body(response);
			}
			
			// Get username from JWT token
			String token = authHeader.substring(7);
			String username = jwtService.getSubject(token);
			
			// Call profile image service
			Map<String, Object> result = profileImageService.uploadProfileImage(profileImage, username);
			
			if ((Boolean) result.get("success")) {
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.badRequest().body(result);
			}
			
		} catch (Exception e) {
			logger.error("Error uploading profile image: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("error", "An error occurred while uploading profile image");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@PostMapping("/profile/edit-image")
	public ResponseEntity<Map<String, Object>> editProfileImage(
			@RequestParam("profile_image") MultipartFile profileImage,
			@RequestHeader("Authorization") String authHeader) {
		try {
			logger.debug("Profile image edit request");
			
			// Validate file
			if (profileImage.isEmpty()) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("error", "No file uploaded");
				return ResponseEntity.badRequest().body(response);
			}
			
			// Validate file size (10MB limit)
			if (profileImage.getSize() > 10 * 1024 * 1024) {
				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("error", "File size too large");
				return ResponseEntity.badRequest().body(response);
			}
			
			// Get username from JWT token
			String token = authHeader.substring(7);
			String username = jwtService.getSubject(token);
			
			// Call profile image service
			Map<String, Object> result = profileImageService.editProfileImage(profileImage, username);
			
			if ((Boolean) result.get("success")) {
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.badRequest().body(result);
			}
			
		} catch (Exception e) {
			logger.error("Error editing profile image: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("error", "An error occurred while editing profile image");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@PostMapping("/profile/delete-image")
	public ResponseEntity<Map<String, Object>> deleteProfileImage(
			@RequestHeader("Authorization") String authHeader) {
		try {
			logger.debug("Profile image delete request");
			
			// Get username from JWT token
			String token = authHeader.substring(7);
			String username = jwtService.getSubject(token);
			
			// Call profile image service
			Map<String, Object> result = profileImageService.deleteProfileImage(username);
			
			if ((Boolean) result.get("success")) {
				return ResponseEntity.ok(result);
			} else {
				return ResponseEntity.badRequest().body(result);
			}
			
		} catch (Exception e) {
			logger.error("Error deleting profile image: {}", e.getMessage());
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("error", "An error occurred while deleting profile image");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@GetMapping("/vehicle/{vehicleId}")
	public ResponseEntity<String> getVehicleById(@PathVariable Integer vehicleId) {
		try {
			logger.debug("Vehicle request for vehicleId: {}", vehicleId);
			
			// Get vehicle
			Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId)
					.orElseThrow(() -> new RuntimeException("Vehicle not found"));
			
			// Create response with vehicle details
			String response = "{\"vehicleId\": " + vehicle.getVehicleId() + 
				", \"make\": \"" + vehicle.getMake() + "\"" +
				", \"model\": \"" + vehicle.getModel() + "\"" +
				", \"year\": " + vehicle.getYear() +
				", \"color\": \"" + vehicle.getColor() + "\"" +
				", \"rentalCostPerDay\": \"" + vehicle.getRentalCostPerDay() + "\"" +
				", \"vehicleRentalStatus\": \"" + vehicle.getVehicleRentalStatus() + "\"" +
				", \"vehicleImageUrl\": \"" + (vehicle.getVehicleImageUrl() != null ? vehicle.getVehicleImageUrl() : "") + "\"" +
				", \"category\": \"" + (vehicle.getCategory() != null ? vehicle.getCategory().getName() : "") + "\"" +
				", \"message\": \"Vehicle retrieved successfully\"}";
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error retrieving vehicle: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"An error occurred while retrieving vehicle information\"}");
		}
	}
	
	/**
	 * Logout endpoint
	 * POST /logout
	 */
	@PostMapping("/logout")
	public ResponseEntity<LogoutResponse> logout(@RequestBody LogoutRequest logoutRequest, 
											   @RequestHeader("Authorization") String authHeader) {
		try {
			logger.debug("Logout request received for user: {}", logoutRequest.getUsername());
			
			LogoutResponse response = logoutService.processLogout(logoutRequest, authHeader);
			
			if (response.isSuccess()) {
				return ResponseEntity.ok(response);
			} else {
				// Return appropriate HTTP status based on the error
				if (response.getError() != null && response.getError().contains("JWT token validation failed")) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
				} else if (response.getError() != null && response.getError().contains("Username is required")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
				}
			}
			
		} catch (Exception e) {
			logger.error("Error processing logout request: {}", e.getMessage(), e);
			LogoutResponse errorResponse = new LogoutResponse(
				false, 
				"Logout failed", 
				"Internal server error occurred", 
				java.time.LocalDateTime.now()
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	
}
	
