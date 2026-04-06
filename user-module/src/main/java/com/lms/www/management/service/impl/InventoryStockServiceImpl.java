package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.InventoryStock;
import com.lms.www.management.model.Item;
import com.lms.www.management.repository.InventoryStockRepository;
import com.lms.www.management.repository.ItemRepository;
import com.lms.www.management.service.InventoryStockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryStockServiceImpl implements InventoryStockService {

    private final InventoryStockRepository inventoryStockRepository;
    private final ItemRepository itemRepository;   // ✅ added

    @Override
    public InventoryStock createStock(InventoryStock stock) {

        // ✅ fetch item from DB
        Item item = itemRepository.findById(stock.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        stock.setItem(item);

        stock.setUpdatedAt(LocalDateTime.now());

        if (stock.getReservedStock() == null) {
            stock.setReservedStock(0);
        }

        if (stock.getDamagedStock() == null) {
            stock.setDamagedStock(0);
        }

        return inventoryStockRepository.save(stock);
    }

    @Override
    public List<InventoryStock> getAllStocks() {
        return inventoryStockRepository.findAll();
    }

    @Override
    public InventoryStock getStockById(Long id) {

        return inventoryStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    @Override
    public InventoryStock updateStock(Long id, InventoryStock stock) {

        InventoryStock existing = getStockById(id);

        existing.setTotalStock(stock.getTotalStock());
        existing.setAvailableStock(stock.getAvailableStock());
        existing.setReservedStock(stock.getReservedStock());
        existing.setDamagedStock(stock.getDamagedStock());
        existing.setUpdatedAt(LocalDateTime.now());

        return inventoryStockRepository.save(existing);
    }

    @Override
    public void deleteStock(Long id) {

        inventoryStockRepository.deleteById(id);
    }
}