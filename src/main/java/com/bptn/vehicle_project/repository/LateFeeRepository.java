package com.bptn.vehicle_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bptn.vehicle_project.jpa.LateFee;

public interface LateFeeRepository extends JpaRepository<LateFee, Integer> {
	
	// Find all late fees for a specific user
	List<LateFee> findByUsername(String username);
	
	// Find late fees for a specific rental
	List<LateFee> findByRentalId(Integer rentalId);
	
	// Find unpaid late fees for a user (where amount_paid < total_cost)
	@Query("SELECT lf FROM LateFee lf WHERE lf.username = :username AND lf.amountPaid < lf.totalCost")
	List<LateFee> findUnpaidLateFeesByUsername(@Param("username") String username);
	
	// Calculate total outstanding late fees for a user
	@Query("SELECT COALESCE(SUM(lf.totalCost - lf.amountPaid), 0) FROM LateFee lf WHERE lf.username = :username")
	Optional<java.math.BigDecimal> calculateTotalOutstandingLateFees(@Param("username") String username);
	
	// Check if user has any outstanding late fees
	@Query("SELECT COUNT(lf) > 0 FROM LateFee lf WHERE lf.username = :username AND lf.amountPaid < lf.totalCost")
	boolean hasOutstandingLateFees(@Param("username") String username);
} 