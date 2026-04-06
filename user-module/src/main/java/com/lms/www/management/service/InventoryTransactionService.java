package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.InventoryTransaction;

public interface InventoryTransactionService {

    InventoryTransaction saveTransaction(InventoryTransaction transaction);

    List<InventoryTransaction> getAllTransactions();

    InventoryTransaction getTransactionById(Long id);

}