package com.lms.www.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.InventoryTransaction;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
}