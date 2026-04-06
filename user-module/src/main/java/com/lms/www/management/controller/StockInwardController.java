package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;
import com.lms.www.management.model.StockInward;
import com.lms.www.management.service.StockInwardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-inward")
@RequiredArgsConstructor
public class StockInwardController {

    private final StockInwardService stockInwardService;

    // ✅ CREATE (JSON ONLY)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('STOCK_INWARD_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<StockInward> createStockInward(
            @RequestBody StockInward stockInward) {

        return ResponseEntity.status(201)
                .body(stockInwardService.createStockInward(stockInward));
    }

    // ✅ UPLOAD FILE
    @PutMapping("/{id}/upload")
    @PreAuthorize("hasAnyAuthority('STOCK_INWARD_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<StockInward> uploadInvoice(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(stockInwardService.uploadInvoice(id, file));
    }

    // ✅ GET ALL
    @GetMapping
    @PreAuthorize("hasAnyAuthority('STOCK_INWARD_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<StockInward>> getAllStockInward() {
        return ResponseEntity.ok(stockInwardService.getAllStockInward());
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('STOCK_INWARD_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<StockInward> getStockInward(@PathVariable Long id) {
        return ResponseEntity.ok(stockInwardService.getStockInwardById(id));
    }
}