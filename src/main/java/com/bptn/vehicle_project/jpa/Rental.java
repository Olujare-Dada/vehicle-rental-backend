package com.bptn.vehicle_project.jpa;

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
@Table(name="\"Rental\"")
public class Rental {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer rentalId;
	
	
	@ManyToOne
	@JoinColumn(name="\"username\"")
	private String username;
	
	
	@Column(name="\"vehicleId\"")
	private Integer vehicleId;
	
	@Column(name="\"startDate\"")
	private LocalDate startDate;
	
	@Column(name="\"endDate\"")
	private LocalDate endDate;
	
	@Column(name = "\"totalCost\"")
	private Double totalCost;
	
	@Column(name= "\"returnFlag\"")
	private String returnFlag;
	
	
	@ManyToOne
	@JoinColumn(name="\"vehicleId\"")
	private Vehicle vehicle;
	
}
