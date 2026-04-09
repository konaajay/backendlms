package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.InventoryStock;


public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {

    Optional<InventoryStock> findByItemId(String itemId);

}