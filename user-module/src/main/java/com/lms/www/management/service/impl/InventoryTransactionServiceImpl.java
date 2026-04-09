package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.InventoryTransaction;
import com.lms.www.management.repository.InventoryTransactionRepository;
import com.lms.www.management.service.InventoryTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;

    @Override
    public InventoryTransaction saveTransaction(InventoryTransaction transaction) {

        // Item ID/Name is already handled via string itemId in the model
        transaction.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Override
    public List<InventoryTransaction> getAllTransactions() {

        return transactionRepository.findAll();
    }

    @Override
    public InventoryTransaction getTransactionById(Long id) {

        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}