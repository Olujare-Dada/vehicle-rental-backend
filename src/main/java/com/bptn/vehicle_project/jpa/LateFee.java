package com.bptn.vehicle_project.jpa;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "late_fees")
public class LateFee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "rentalId")
	private Integer rentalId;
	
	@Column(name = "days_late")
	private Integer daysLate;
	
	@Column(name = "total_cost")
	private BigDecimal totalCost;
	
	@Column(name = "amount_paid")
	private BigDecimal amountPaid;
	
	@Column(name = "created_at")
	private Timestamp createdAt;
	
	// Relationships
	@ManyToOne
	@JoinColumn(name = "username", insertable = false, updatable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "rentalId", insertable = false, updatable = false)
	private Rental rental;
	
	// Default constructor
	public LateFee() {}
	
	// Constructor with all fields
	public LateFee(String username, Integer rentalId, Integer daysLate, BigDecimal totalCost) {
		this.username = username;
		this.rentalId = rentalId;
		this.daysLate = daysLate;
		this.totalCost = totalCost;
		this.amountPaid = BigDecimal.ZERO; // Initialize with 0 paid
		this.createdAt = new Timestamp(System.currentTimeMillis());
	}
	
	// Getters and setters
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Integer getRentalId() {
		return rentalId;
	}
	
	public void setRentalId(Integer rentalId) {
		this.rentalId = rentalId;
	}
	
	public Integer getDaysLate() {
		return daysLate;
	}
	
	public void setDaysLate(Integer daysLate) {
		this.daysLate = daysLate;
	}
	
	public BigDecimal getTotalCost() {
		return totalCost;
	}
	
	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}
	
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Rental getRental() {
		return rental;
	}
	
	public void setRental(Rental rental) {
		this.rental = rental;
	}
} 