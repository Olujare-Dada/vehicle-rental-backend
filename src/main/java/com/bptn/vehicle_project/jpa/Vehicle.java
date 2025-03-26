package com.bptn.vehicle_project.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Vehicles\"")
public class Vehicle implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "\"vehicleId\"")
	private Integer vehicleId;
	
	private String make;
	private String model;
	private String color;
	private Integer year;
	private Double mileage;
	
	@Column(name="\"rentalCostPerDay\"")
	private Double rentalCostPerDay;
	
	@Column(name="\"vehicleRentalStatus\"")
	private String vehicleRentalStatus;
	
	@OneToMany
	@JoinColumn(name="\"vehicleId\"")
	private Rental rental;

	
	public Integer getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Integer vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Double getMileage() {
		return mileage;
	}

	public void setMileage(Double mileage) {
		this.mileage = mileage;
	}

	public Double getRentalCostPerDay() {
		return rentalCostPerDay;
	}

	public void setRentalCostPerDay(Double rentalCostPerDay) {
		this.rentalCostPerDay = rentalCostPerDay;
	}

	public String getVehicleRentalStatus() {
		return vehicleRentalStatus;
	}

	public void setVehicleRentalStatus(String vehicleRentalStatus) {
		this.vehicleRentalStatus = vehicleRentalStatus;
	}

	public Rental getRental() {
		return rental;
	}

	public void setRental(Rental rental) {
		this.rental = rental;
	}
}
