package com.bptn.vehicle_project.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bptn.vehicle_project.domain.FleetResponse;
import com.bptn.vehicle_project.jpa.Vehicle;
import com.bptn.vehicle_project.repository.VehicleRepository;

@Service
public class FleetService {
    
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    // Vehicle features mapping - you can extend this or move to database
    private final Map<String, List<String>> vehicleFeatures = new HashMap<>();
    
    public FleetService() {
        // Initialize vehicle features - you can move this to database later
        vehicleFeatures.put("Economy", Arrays.asList("Fuel efficient", "Easy parking", "Great for couples"));
        vehicleFeatures.put("Midsize", Arrays.asList("Spacious interior", "Smooth ride", "Advanced safety features"));
        vehicleFeatures.put("SUV", Arrays.asList("7+ seating capacity", "Large luggage space", "All-weather capable"));
        vehicleFeatures.put("Van", Arrays.asList("Large cargo capacity", "Moving equipment", "Commercial use"));
        vehicleFeatures.put("Luxury", Arrays.asList("Premium interior", "Advanced technology", "Comfortable ride"));
        vehicleFeatures.put("Sports", Arrays.asList("High performance", "Sporty design", "Advanced handling"));
    }
    
    public FleetResponse getFleet(int page, int size) {
        try {
            // Create pageable request
            Pageable pageable = PageRequest.of(page, size);
            
            // Get vehicles with pagination (show all vehicles regardless of status)
            Page<Vehicle> vehiclePage = vehicleRepository.findAll(pageable);
            
            // Convert to FleetResponse
            return createFleetResponse(vehiclePage);
            
        } catch (Exception e) {
            logger.error("Error retrieving fleet: {}", e.getMessage());
            return new FleetResponse(false, "Error retrieving fleet data", null);
        }
    }
    
    public FleetResponse getAvailableFleet(int page, int size) {
        try {
            // Create pageable request
            Pageable pageable = PageRequest.of(page, size);
            
            // Get only available vehicles - try different status values
            Page<Vehicle> vehiclePage = vehicleRepository.findByVehicleRentalStatus("Available", pageable);
            
            // If no results, try with "AVAILABLE"
            if (vehiclePage.isEmpty()) {
                vehiclePage = vehicleRepository.findByVehicleRentalStatus("AVAILABLE", pageable);
            }
            
            // If still no results, try with "available"
            if (vehiclePage.isEmpty()) {
                vehiclePage = vehicleRepository.findByVehicleRentalStatus("available", pageable);
            }
            
            // Convert to FleetResponse
            return createFleetResponse(vehiclePage);
            
        } catch (Exception e) {
            logger.error("Error retrieving available fleet: {}", e.getMessage());
            return new FleetResponse(false, "Error retrieving available fleet data", null);
        }
    }
    
    public FleetResponse getFleetByCategory(Integer categoryId, int page, int size) {
        try {
            // Create pageable request
            Pageable pageable = PageRequest.of(page, size);
            
            // Get vehicles by category
            Page<Vehicle> vehiclePage = vehicleRepository.findByCategoryCategoryId(categoryId, pageable);
            
            // Convert to FleetResponse
            return createFleetResponse(vehiclePage);
            
        } catch (Exception e) {
            logger.error("Error retrieving fleet by category: {}", e.getMessage());
            return new FleetResponse(false, "Error retrieving fleet data by category", null);
        }
    }
    
    private FleetResponse createFleetResponse(Page<Vehicle> vehiclePage) {
        List<FleetResponse.VehicleInfo> vehicleInfos = new ArrayList<>();
        
        for (Vehicle vehicle : vehiclePage.getContent()) {
            FleetResponse.VehicleInfo vehicleInfo = convertToVehicleInfo(vehicle);
            vehicleInfos.add(vehicleInfo);
        }
        
        // Create pagination info
        FleetResponse.PaginationInfo pagination = new FleetResponse.PaginationInfo(
            vehiclePage.getNumber(),
            vehiclePage.getTotalPages(),
            (int) vehiclePage.getTotalElements(),
            vehiclePage.getSize(),
            vehiclePage.hasNext(),
            vehiclePage.hasPrevious()
        );
        
        // Create fleet data
        FleetResponse.FleetData fleetData = new FleetResponse.FleetData(vehicleInfos, pagination);
        
        return new FleetResponse(true, "Fleet retrieved successfully", fleetData);
    }
    
    private FleetResponse.VehicleInfo convertToVehicleInfo(Vehicle vehicle) {
        // Get category name
        String categoryName = vehicle.getCategory() != null ? vehicle.getCategory().getName() : "Unknown";
        
        // Get features for this category
        List<String> features = vehicleFeatures.getOrDefault(categoryName, Arrays.asList("Standard features"));
        
        // Create vehicle name
        String vehicleName = vehicle.getMake() + " " + vehicle.getModel();
        
        // Format price
        String price = "$" + vehicle.getRentalCostPerDay() + "/day";
        
        // Create description based on category
        String description = createDescription(vehicle, categoryName);
        
        // Determine icon based on category
        String icon = determineIcon(categoryName);
        
        // Create image URL
        String imageUrl = vehicle.getVehicleImageUrl() != null ? 
            vehicle.getVehicleImageUrl() : 
            "/api/images/vehicles/default-vehicle.jpg";
        
        return new FleetResponse.VehicleInfo(
            vehicle.getVehicleId(),
            vehicleName,
            categoryName,
            price,
            description,
            features,
            imageUrl,
            icon,
            vehicle.getVehicleRentalStatus()
        );
    }
    
    private String createDescription(Vehicle vehicle, String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "economy":
                return "Perfect for city driving and daily commutes. Fuel efficient and easy to park.";
            case "midsize":
                return "Spacious sedan perfect for business trips and family outings.";
            case "suv":
                return "Versatile SUV with plenty of cargo space and all-wheel drive capability.";
            case "van":
                return "Large cargo van perfect for moving and commercial use.";
            case "luxury":
                return "Premium vehicle with advanced features and superior comfort.";
            case "sports":
                return "High-performance vehicle designed for speed and handling.";
            default:
                return vehicle.getMake() + " " + vehicle.getModel() + " - " + vehicle.getYear() + " " + vehicle.getColor() + " vehicle.";
        }
    }
    
    private String determineIcon(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "suv":
                return "Users";
            case "van":
                return "Truck";
            default:
                return "Car";
        }
    }
} 