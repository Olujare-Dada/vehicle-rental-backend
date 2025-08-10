package com.bptn.vehicle_project.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bptn.vehicle_project.service.EmailService;
import com.bptn.vehicle_project.domain.EmailExistsException;
import com.bptn.vehicle_project.domain.EmailNotVerifiedException;
import com.bptn.vehicle_project.domain.InsufficientBalanceException;
import com.bptn.vehicle_project.domain.UserNotFoundException;
import com.bptn.vehicle_project.domain.UsernameExistException;
import com.bptn.vehicle_project.jpa.Profile;
import com.bptn.vehicle_project.service.UserService;
import com.bptn.vehicle_project.jpa.User;
import com.bptn.vehicle_project.repository.UserRepository;
import com.bptn.vehicle_project.provider.ResourceProvider;
import com.bptn.vehicle_project.security.JwtService;

@Service
public class UserService {
	
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	JwtService jwtService;

	@Autowired
	ResourceProvider provider;

	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EmailService emailService;
	
	public List<User> listUsers() {
		return this.userRepository.findAll();

	}

	public Optional<User> findByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}
	
	public User signup(User user) {
		// Validate required fields
		if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("Username is required");
		}
		if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("Email is required");
		}
		if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("Password is required");
		}
		
		// Validate username and email uniqueness
		this.validateUsernameAndEmail(user.getUsername(), user.getEmail());
		
		// Set basic fields
		user.setUsername(user.getUsername().toLowerCase().trim());
		user.setFirstName(user.getFirstName() != null ? user.getFirstName().toLowerCase().trim() : null);
		user.setLastName(user.getLastName() != null ? user.getLastName().toLowerCase().trim() : null);
		user.setEmail(user.getEmail().toLowerCase().trim());
		user.setEmailVerified(false);
		user.setPhone(user.getPhone() != null ? user.getPhone().trim() : null);
		user.setLicense(user.getLicense() != null ? user.getLicense().toLowerCase().trim() : null);
		user.setAddress(user.getAddress() != null ? user.getAddress().toLowerCase().trim() : null);
		
		// Handle new fields
		user.setCity(user.getCity() != null ? user.getCity().toLowerCase().trim() : null);
		user.setState(user.getState() != null ? user.getState().toLowerCase().trim() : null);
		user.setZipcode(user.getZipcode() != null ? user.getZipcode().trim() : null);
		user.setLicenseState(user.getLicenseState() != null ? user.getLicenseState().toLowerCase().trim() : null);
		
		// Set dates (keep as is, don't convert to lowercase)
		// user.setLicenseExpiry(user.getLicenseExpiry()); // Keep as provided
		// user.setDateOfBirth(user.getDateOfBirth()); // Keep as provided
		
		// Set current balance to 0 if not provided
		if (user.getCurrentBalance() == null) {
			user.setCurrentBalance(BigDecimal.ZERO);
		}
		
		// Encode password for security (only if not already encoded)
		if (!user.getPassword().startsWith("$2a$")) {
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		}
		
		// Set creation timestamp
		user.setCreatedOn(Timestamp.from(Instant.now()));
		
		// Save user
		this.userRepository.save(user);
		
		// Create and save profile
		Profile profile = new Profile();
		profile.setUser(user);
		profile.setCity(user.getCity());
		profile.setCountry(user.getState()); // Using state as country for now
		profile.setHeadline("New member at Vehicle Rental System");
		profile.setBio("Welcome to our vehicle rental platform!");
		
		// Save profile
		user.setProfile(profile);
		this.userRepository.save(user);
		
		// Send verification email
		this.emailService.sendVerificationEmail(user);
		
		this.logger.info("User signed up successfully: {}", user.getUsername());
		return user;
	}
	
	public void createUser(User user) {
		this.userRepository.save(user);
		
	}
	
	private void validateUsernameAndEmail(String username, String emailId) {

		this.userRepository.findByUsername(username).ifPresent(u -> {
			throw new UsernameExistException(String.format("Username already exists, %s", u.getUsername()));
		});

		this.userRepository.findByEmail(emailId).ifPresent(u -> {
			throw new EmailExistsException(String.format("Email already exists, %s", u.getEmail()));
		});

	}


	public void verifyEmail() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));

		user.setEmailVerified(true);

		this.userRepository.save(user);
	}
	
	public void verifyEmailByToken(String token) {
		try {
			// Verify the JWT token and extract username
			String username = this.jwtService.getSubject(token);
			
			// Find user by username
			User user = this.userRepository.findByUsername(username)
					.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
			
			// Mark email as verified
			user.setEmailVerified(true);
			this.userRepository.save(user);
			
			this.logger.info("Email verified successfully for user: {}", username);
		} catch (Exception e) {
			this.logger.error("Error verifying email with token: {}", e.getMessage());
			throw new RuntimeException("Invalid or expired verification token");
		}
	}
	



	private static User isEmailVerified(User user) {

		if (user.getEmailVerified().equals(false)) {
			throw new EmailNotVerifiedException(String.format("Email requires verification, %s", user.getEmail()));
		} 

		return user;
	}

	private Authentication authenticate(String username, String password) {
		return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
	
	public User authenticate(User user) {
		// Get user from database
		User dbUser = this.userRepository.findByUsername(user.getUsername())
				.orElseThrow(() -> new RuntimeException("Invalid username or password"));
		
		// Check if email is verified
		isEmailVerified(dbUser);
		
		// Verify password
		if (!this.passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			throw new RuntimeException("Invalid username or password");
		}
		
		return dbUser;
	}
	
	public HttpHeaders generateJwtHeader(String username) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.add(AUTHORIZATION, this.jwtService.generateJwtToken(username,this.provider.getJwtExpiration()));

	    return headers;
	}
	
	public String generateJwtToken(String username) {
	    return this.jwtService.generateJwtToken(username, this.provider.getJwtExpiration());
	}
	
	public User updateBalance(String username, BigDecimal amount, String description) {
		logger.debug("Updating balance for user: {}, amount: {}, description: {}", username, amount, description);
		
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
		
		// Validate amount
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		
		// Update balance
		BigDecimal newBalance = user.getCurrentBalance().add(amount);
		user.setCurrentBalance(newBalance);
		
		// Save updated user
		User updatedUser = this.userRepository.save(user);
		
		logger.info("Balance updated successfully for user: {}. New balance: {}", username, newBalance);
		
		return updatedUser;
	}
	
	public BigDecimal getBalance(String username) {
		logger.debug("Getting balance for user: {}", username);
		
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
		
		logger.debug("Current balance for user {}: {}", username, user.getCurrentBalance());
		return user.getCurrentBalance();
	}
	
	public User debitBalance(String username, BigDecimal amount, String description) {
		logger.debug("Debiting balance for user: {}, amount: {}, description: {}", username, amount, description);
		
		User user = this.userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
		
		// Validate amount
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		
		// Check if user has sufficient balance
		if (user.getCurrentBalance().compareTo(amount) < 0) {
			throw new InsufficientBalanceException("Insufficient balance. Required: " + amount + ", Available: " + user.getCurrentBalance());
		}
		
		// Debit balance
		BigDecimal newBalance = user.getCurrentBalance().subtract(amount);
		user.setCurrentBalance(newBalance);
		
		// Save updated user
		User updatedUser = this.userRepository.save(user);
		
		logger.info("Balance debited successfully for user: {}. New balance: {}", username, newBalance);
		
		return updatedUser;
	}
	
	public void sendResetPasswordEmail(String emailId) {

		  Optional<User> opt = this.userRepository.findByEmail(emailId);

		  if (opt.isPresent()) {
		    this.emailService.sendResetPasswordEmail(opt.get());
		  } else {
		    logger.debug("Email doesn't exist, {}", emailId);
		  }
	}
	
	public void resetPassword(String password) {

	    String username = SecurityContextHolder.getContext().getAuthentication().getName();

	    User user = this.userRepository.findByUsername(username)
	                .orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s",username)));

	    user.setPassword(this.passwordEncoder.encode(password));

	    this.userRepository.save(user);
	}
	
	
	public User getUser() {

	    String username = SecurityContextHolder.getContext().getAuthentication().getName();

	    /* Get User from the DB. */
	    return this.userRepository.findByUsername(username)
	.orElseThrow(() -> new UserNotFoundException(String.format("Username doesn't exist, %s",username)));
	}
	
	private void updateValue(Supplier<String> getter, Consumer<String> setter) {
        
	    Optional.ofNullable(getter.get())
	            //.filter(StringUtils::hasText)
	               .map(String::trim)
	               .ifPresent(setter);
	}
	
	
	private void updatePassword(Supplier<String> getter, Consumer<String> setter) {

	    Optional.ofNullable(getter.get())
	               .filter(StringUtils::hasText)
	               .map(this.passwordEncoder::encode)
	               .ifPresent(setter);
	}
	
	private User updateUser(User user, User currentUser) {
	    
		  this.updateValue(user::getFirstName, currentUser::setFirstName);
		  this.updateValue(user::getLastName, currentUser::setLastName);
		  this.updateValue(user::getPhone, currentUser::setPhone);
		  this.updateValue(user::getEmail, currentUser::setEmail);
		  this.updatePassword(user::getPassword, currentUser::setPassword);

		  return this.userRepository.save(currentUser);
		}
	
	
	public User updateUser(User user) {
        
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();

	    /* Validates the new email if provided */
	    this.userRepository.findByEmail(user.getEmail())
	                            .filter(u->!u.getUsername().equals(username))
	                            .ifPresent(u -> {throw new EmailExistsException(String.format("Email already exists, %s", u.getEmail()));});
	        
	    /* Get and Update User */   
	    return this.userRepository.findByUsername(username)
	                            .map(currentUser -> this.updateUser(user, currentUser))
	                            .orElseThrow(()-> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
	}
	
	private User updateUserProfile(Profile profile, User user) {

	    Profile currentProfile = user.getProfile();

	    if (Optional.ofNullable(currentProfile).isPresent()) {

	        this.updateValue(profile::getHeadline, currentProfile::setHeadline);
	        this.updateValue(profile::getBio, currentProfile::setBio);
	        this.updateValue(profile::getCity, currentProfile::setCity);
	        this.updateValue(profile::getCountry, currentProfile::setCountry);
	        this.updateValue(profile::getPicture, currentProfile::setPicture);
	    } 
	    else {
	        user.setProfile(profile);
	        profile.setUser(user);
	    }

	    return this.userRepository.save(user);
	}
	
	
	public User updateUserProfile(Profile profile) {
        
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();

	    /* Get and Update User */   
	    return this.userRepository.findByUsername(username)
	                  .map(user -> this.updateUserProfile(profile, user))
	                  .orElseThrow(()-> new UserNotFoundException(String.format("Username doesn't exist, %s", username)));
	}

}
