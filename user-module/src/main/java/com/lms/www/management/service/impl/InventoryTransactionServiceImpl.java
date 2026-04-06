package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.InventoryTransaction;
import com.lms.www.management.model.Item;
import com.lms.www.management.repository.InventoryTransactionRepository;
import com.lms.www.management.repository.ItemRepository;
import com.lms.www.management.service.InventoryTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final ItemRepository itemRepository;

    @Override
    public InventoryTransaction saveTransaction(InventoryTransaction transaction) {

        // fetch item from DB so response contains full item details
        if (transaction.getItem() != null && transaction.getItem().getId() != null) {

            Item item = itemRepository.findById(transaction.getItem().getId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            transaction.setItem(item);
        }

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