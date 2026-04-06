package com.lms.www.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.StockInward;

@Repository
public interface StockInwardRepository extends JpaRepository<StockInward, Long> {
}