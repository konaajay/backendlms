package com.lms.www.management.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.lms.www.management.repository.InventoryStockRepository;
import com.lms.www.management.repository.ReturnDamageRepository;
import com.lms.www.management.repository.StockInwardRepository;
import com.lms.www.management.repository.StockOutwardRepository;
import com.lms.www.management.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final InventoryStockRepository inventoryStockRepository;
    private final StockInwardRepository stockInwardRepository;
    private final StockOutwardRepository stockOutwardRepository;
    private final ReturnDamageRepository returnDamageRepository;

    @Override
    public Map<String, Object> getDashboardSummary() {

        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("totalItems", inventoryStockRepository.count());
        dashboard.put("totalPurchases", stockInwardRepository.count());
        dashboard.put("totalIssued", stockOutwardRepository.count());
        dashboard.put("totalReturns", returnDamageRepository.count());

        return dashboard;
    }

    @Override
    public List<?> getStockSummary() {

        return inventoryStockRepository.findAll();
    }

    @Override
    public List<?> getPurchaseReport() {

        return stockInwardRepository.findAll();
    }

    @Override
    public List<?> getIssueReport() {

        return stockOutwardRepository.findAll();
    }

    @Override
    public List<?> getLossDamageReport() {

        return returnDamageRepository.findAll();
    }
}