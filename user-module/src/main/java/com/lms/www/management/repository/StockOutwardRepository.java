package com.lms.www.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.StockOutward;

public interface StockOutwardRepository extends JpaRepository<StockOutward, Long> {
}