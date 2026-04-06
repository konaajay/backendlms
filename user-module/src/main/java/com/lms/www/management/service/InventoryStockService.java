package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.InventoryStock;

public interface InventoryStockService {

    InventoryStock createStock(InventoryStock stock);

    List<InventoryStock> getAllStocks();

    InventoryStock getStockById(Long id);

    InventoryStock updateStock(Long id, InventoryStock stock);

    void deleteStock(Long id);
}