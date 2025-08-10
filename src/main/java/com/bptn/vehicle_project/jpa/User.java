package com.bptn.vehicle_project.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.bptn.vehicle_project.jpa.Profile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name= "\"userDb\"")
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// username, firstname, lastname, email, phone, address, password, license, currentBalance
	
	@Id
	@Column(name="\"username\"")
	private String username;
	
	@Column(name="\"firstName\"")
	private String firstName;
	
	@Column(name="\"lastName\"")
	private String lastName;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	
	private String email;
	
	private String phone;
	private String address;
	private String license;
	
	@Column(name = "\"currentBalance\"")
	private BigDecimal currentBalance;
	
	@Column(name = "\"emailVerified\"")
	private Boolean emailVerified;
	
	@Column(name = "\"createdOn\"")
	private Timestamp createdOn;
	
	// New fields added to database
	private String city;
	private String state;
	private String zipcode;
	
	@Column(name = "\"license_expiry\"")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date licenseExpiry;
	
	@Column(name = "\"license_state\"")
	private String licenseState;
	
	@Column(name = "\"date_of_birth\"")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateOfBirth;
	
	@JsonInclude(Include.NON_NULL)
	@OneToOne(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private Profile profile;

	
//	private Vehicle vehicle;
	
	@JsonInclude(Include.NON_NULL)
	@OneToMany(mappedBy="user")
	private List<Rental> rentals = new ArrayList<>();
	
	public User() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	
	public List<Rental> getRentals(){
		return rentals;
	}
	
	public void setRentals(List<Rental> rentals) {
		this.rentals=rentals;
	}
	
	public Profile getProfile() {
		return profile;
	}


	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	// Getters and setters for new fields
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public Date getLicenseExpiry() {
		return licenseExpiry;
	}

	public void setLicenseExpiry(Date licenseExpiry) {
		this.licenseExpiry = licenseExpiry;
	}

	public String getLicenseState() {
		return licenseState;
	}

	public void setLicenseState(String licenseState) {
		this.licenseState = licenseState;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
}
