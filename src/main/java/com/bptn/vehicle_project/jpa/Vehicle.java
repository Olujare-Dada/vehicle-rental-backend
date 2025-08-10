package com.bptn.vehicle_project.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	private BigDecimal mileage;
	
	@Column(name="\"rentalCostPerDay\"")
	private BigDecimal rentalCostPerDay;
	
	@Column(name="\"vehicleRentalStatus\"")
	private String vehicleRentalStatus;
	
	@Column(name="\"vehicle_image_url\"")
	private String vehicleImageUrl;
	
	@ManyToOne
	@JoinColumn(name="\"category_id\"")
	private Category category;
	
	@OneToMany
	@JoinColumn(name="\"vehicleId\"")
	private List<Rental> rentals = new ArrayList<>();

	
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

	public BigDecimal getMileage() {
		return mileage;
	}

	public void setMileage(BigDecimal mileage) {
		this.mileage = mileage;
	}

	public BigDecimal getRentalCostPerDay() {
		return rentalCostPerDay;
	}

	public void setRentalCostPerDay(BigDecimal rentalCostPerDay) {
		this.rentalCostPerDay = rentalCostPerDay;
	}

	public String getVehicleRentalStatus() {
		return vehicleRentalStatus;
	}

	public void setVehicleRentalStatus(String vehicleRentalStatus) {
		this.vehicleRentalStatus = vehicleRentalStatus;
	}

	public List<Rental> getRental() {
		return rentals;
	}

	public void setRental(List<Rental> rentals) {
		this.rentals = rentals;
	}
	
	public String getVehicleImageUrl() {
		return vehicleImageUrl;
	}

	public void setVehicleImageUrl(String vehicleImageUrl) {
		this.vehicleImageUrl = vehicleImageUrl;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
