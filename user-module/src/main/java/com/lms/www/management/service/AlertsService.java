package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.InventoryStock;

public interface AlertsService {

    List<InventoryStock> getLowStockItems();

}