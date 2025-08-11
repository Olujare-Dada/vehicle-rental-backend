package com.bptn.vehicle_project.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="\"rentalDb\"")
public class Rental {
	
	@ManyToOne
	@JoinColumn(name="\"username\"")
	private User user;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="\"rentalId\"")
	private Integer rentalId;
	
	
	@Column(name="\"startDate\"")
	private LocalDate startDate;
	
	@Column(name="\"endDate\"")
	private LocalDate endDate;
	
	@Column(name = "\"totalCost\"")
	private BigDecimal totalCost;
	
	@Column(name= "\"returnFlag\"")
	private String returnFlag;
	
	@Column(name= "\"additional_notes\"")
	private String additionalNotes;
	
	@Column(name= "\"return_date\"")
	private LocalDate returnDate;
	
	// Getters and setters for existing fields
	public Integer getRentalId() {
		return rentalId;
	}

	public void setRentalId(Integer rentalId) {
		this.rentalId = rentalId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public String getReturnFlag() {
		return returnFlag;
	}

	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}
	
	public String getAdditionalNotes() {
		return additionalNotes;
	}
	
	public void setAdditionalNotes(String additionalNotes) {
		this.additionalNotes = additionalNotes;
	}
	
	public LocalDate getReturnDate() {
		return returnDate;
	}
	
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	
	@ManyToOne
	@JoinColumn(name="\"vehicleId\"")
	private Vehicle vehicle;
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user=user;
	}
	
}
