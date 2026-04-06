package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.StockOutward;

public interface StockOutwardService {

    StockOutward issueStock(StockOutward stockOutward);

    List<StockOutward> getAllIssuedStocks();

    StockOutward getIssuedStockById(Long id);

}