package com.lms.www.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.InventoryStock;
import com.lms.www.management.repository.InventoryStockRepository;
import com.lms.www.management.service.AlertsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertsServiceImpl implements AlertsService {

    private final InventoryStockRepository inventoryStockRepository;

    @Override
    public List<InventoryStock> getLowStockItems() {

        return inventoryStockRepository.findAll()
                .stream()
                .filter(stock -> stock.getAvailableStock() != null &&
                        stock.getAvailableStock() < 5) // Default low stock threshold
                .toList();
    }

}