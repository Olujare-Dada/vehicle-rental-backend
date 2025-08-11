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
@Table(name = "late_fee_payments")
public class LateFeePayment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "late_fee_id")
	private Integer lateFeeId;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "payment_amount")
	private BigDecimal paymentAmount;
	
	@Column(name = "payment_notes")
	private String paymentNotes;
	
	@Column(name = "payment_date")
	private Timestamp paymentDate;
	
	// Relationships
	@ManyToOne
	@JoinColumn(name = "late_fee_id", insertable = false, updatable = false)
	private LateFee lateFee;
	
	@ManyToOne
	@JoinColumn(name = "username", insertable = false, updatable = false)
	private User user;
	
	// Default constructor
	public LateFeePayment() {}
	
	// Constructor with all fields
	public LateFeePayment(Integer lateFeeId, String username, BigDecimal paymentAmount, String paymentNotes) {
		this.lateFeeId = lateFeeId;
		this.username = username;
		this.paymentAmount = paymentAmount;
		this.paymentNotes = paymentNotes;
		this.paymentDate = new Timestamp(System.currentTimeMillis());
	}
	
	// Getters and setters
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getLateFeeId() {
		return lateFeeId;
	}
	
	public void setLateFeeId(Integer lateFeeId) {
		this.lateFeeId = lateFeeId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}
	
	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	
	public String getPaymentNotes() {
		return paymentNotes;
	}
	
	public void setPaymentNotes(String paymentNotes) {
		this.paymentNotes = paymentNotes;
	}
	
	public Timestamp getPaymentDate() {
		return paymentDate;
	}
	
	public void setPaymentDate(Timestamp paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	public LateFee getLateFee() {
		return lateFee;
	}
	
	public void setLateFee(LateFee lateFee) {
		this.lateFee = lateFee;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
} 