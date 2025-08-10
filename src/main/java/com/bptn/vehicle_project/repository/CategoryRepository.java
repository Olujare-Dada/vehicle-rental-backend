package com.bptn.vehicle_project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bptn.vehicle_project.jpa.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findByCategoryId(Integer categoryId);
    Optional<Category> findByName(String name);
} 