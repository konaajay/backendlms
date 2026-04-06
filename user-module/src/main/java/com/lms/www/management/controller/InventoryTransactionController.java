package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.InventoryTransaction;
import com.lms.www.management.service.InventoryTransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('INVENTORY_TRANSACTION_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<InventoryTransaction> createTransaction(
            @RequestBody InventoryTransaction transaction) {

        return ResponseEntity.ok(transactionService.saveTransaction(transaction));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('INVENTORY_TRANSACTION_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<InventoryTransaction>> getAllTransactions() {

        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('INVENTORY_TRANSACTION_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<InventoryTransaction> getTransaction(@PathVariable Long id) {

        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}