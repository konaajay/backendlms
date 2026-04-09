package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.InventoryStock;
import com.lms.www.management.model.StockOutward;
import com.lms.www.management.repository.InventoryStockRepository;
import com.lms.www.management.repository.StockOutwardRepository;
import com.lms.www.management.service.StockOutwardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockOutwardServiceImpl implements StockOutwardService {

    private final StockOutwardRepository stockOutwardRepository;
    private final InventoryStockRepository inventoryStockRepository;

    @Override
    public StockOutward issueStock(StockOutward stockOutward) {

        String itemId = stockOutward.getItemId();

        InventoryStock inventory = inventoryStockRepository
                .findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for item: " + itemId));

        if (inventory.getAvailableStock() < stockOutward.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        inventory.setAvailableStock(
                inventory.getAvailableStock() - stockOutward.getQuantity());

        if (Boolean.TRUE.equals(stockOutward.getReturnable())) {

            inventory.setReservedStock(
                    inventory.getReservedStock() + stockOutward.getQuantity());

        } else {

            inventory.setTotalStock(
                    inventory.getTotalStock() - stockOutward.getQuantity());
        }

        inventoryStockRepository.save(inventory);

        stockOutward.setCreatedAt(LocalDateTime.now());
        stockOutward.setIssueDate(LocalDate.now());

        return stockOutwardRepository.save(stockOutward);
    }

    @Override
    public List<StockOutward> getAllIssuedStocks() {
        return stockOutwardRepository.findAll();
    }

    @Override
    public StockOutward getIssuedStockById(Long id) {

        return stockOutwardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issued stock not found"));
    }
}