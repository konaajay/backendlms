package com.lms.www.management.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.StockInward;

public interface StockInwardService {

    // ✅ Create (JSON only)
    StockInward createStockInward(StockInward stockInward);

    // ✅ Upload file separately
    StockInward uploadInvoice(Long id, MultipartFile file);

    // ✅ Get methods
    List<StockInward> getAllStockInward();

    StockInward getStockInwardById(Long id);
}