package com.bptn.vehicle_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bptn.vehicle_project.jpa.LateFeePayment;

public interface LateFeePaymentRepository extends JpaRepository<LateFeePayment, Integer> {
	
	// Find all payments for a specific late fee
	List<LateFeePayment> findByLateFeeId(Integer lateFeeId);
	
	// Find all payments by a specific user
	List<LateFeePayment> findByUsername(String username);
	
	// Find all payments for a specific late fee by user
	List<LateFeePayment> findByLateFeeIdAndUsername(Integer lateFeeId, String username);
	
	// Calculate total amount paid for a specific late fee
	@Query("SELECT COALESCE(SUM(lfp.paymentAmount), 0) FROM LateFeePayment lfp WHERE lfp.lateFeeId = :lateFeeId")
	java.math.BigDecimal calculateTotalPaidForLateFee(@Param("lateFeeId") Integer lateFeeId);
	
	// Calculate total amount paid by a user for all late fees
	@Query("SELECT COALESCE(SUM(lfp.paymentAmount), 0) FROM LateFeePayment lfp WHERE lfp.username = :username")
	java.math.BigDecimal calculateTotalPaidByUser(@Param("username") String username);
} 