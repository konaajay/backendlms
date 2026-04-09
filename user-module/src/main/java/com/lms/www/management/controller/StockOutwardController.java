package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.StockOutward;
import com.lms.www.management.service.StockOutwardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-outward")
@RequiredArgsConstructor
public class StockOutwardController {

    private final StockOutwardService stockOutwardService;

    // ✅ ISSUE STOCK
    @PostMapping
    @PreAuthorize("hasAnyAuthority('STOCK_INWARD_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<StockOutward> issueStock(@RequestBody StockOutward stockOutward) {

        return ResponseEntity.status(201)
                .body(stockOutwardService.issueStock(stockOutward));
    }

    // ✅ GET ALL
    @GetMapping
    @PreAuthorize("hasAnyAuthority('STOCK_INWARD_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS')")
    public ResponseEntity<List<StockOutward>> getAllIssuedStocks() {

        return ResponseEntity.ok(stockOutwardService.getAllIssuedStocks());
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('STOCK_INWARD_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<StockOutward> getIssuedStock(@PathVariable Long id) {

        return ResponseEntity.ok(stockOutwardService.getIssuedStockById(id));
    }
}