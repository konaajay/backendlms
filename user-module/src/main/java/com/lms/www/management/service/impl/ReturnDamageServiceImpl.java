package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.InventoryStock;
import com.lms.www.management.model.Item;
import com.lms.www.management.model.ReturnDamage;
import com.lms.www.management.repository.InventoryStockRepository;
import com.lms.www.management.repository.ItemRepository;
import com.lms.www.management.repository.ReturnDamageRepository;
import com.lms.www.management.service.ReturnDamageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReturnDamageServiceImpl implements ReturnDamageService {

    private final ReturnDamageRepository returnRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final ItemRepository itemRepository;

    @Override
    public ReturnDamage processReturn(ReturnDamage returnDamage) {

        // fetch item from DB so response contains full item details
        Item item = itemRepository.findById(returnDamage.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        returnDamage.setItem(item);

        // fetch inventory for the item
        InventoryStock inventory = inventoryStockRepository
                .findByItem(item)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        int qty = 1;

        if ("GOOD".equalsIgnoreCase(returnDamage.getItemCondition())) {

            inventory.setAvailableStock(
                    inventory.getAvailableStock() + qty);

            inventory.setReservedStock(
                    Math.max(0, inventory.getReservedStock() - qty));
        }

        if ("DAMAGED".equalsIgnoreCase(returnDamage.getItemCondition())) {

            inventory.setDamagedStock(
                    inventory.getDamagedStock() + qty);

            inventory.setReservedStock(
                    Math.max(0, inventory.getReservedStock() - qty));
        }

        inventoryStockRepository.save(inventory);

        returnDamage.setCreatedAt(LocalDateTime.now());

        return returnRepository.save(returnDamage);
    }

    @Override
    public List<ReturnDamage> getAllReturns() {
        return returnRepository.findAll();
    }

    @Override
    public ReturnDamage getReturnById(Long id) {

        return returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return record not found"));
    }
}