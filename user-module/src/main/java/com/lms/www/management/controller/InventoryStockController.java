package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.InventoryStock;
import com.lms.www.management.service.InventoryStockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryStockController {

    private final InventoryStockService inventoryStockService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('INVENTORY_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<InventoryStock> createStock(@RequestBody InventoryStock stock) {

        return ResponseEntity.ok(inventoryStockService.createStock(stock));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('INVENTORY_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<InventoryStock>> getAllStocks() {

        return ResponseEntity.ok(inventoryStockService.getAllStocks());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('INVENTORY_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<InventoryStock> getStock(@PathVariable Long id) {

        return ResponseEntity.ok(inventoryStockService.getStockById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('INVENTORY_UPDATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<InventoryStock> updateStock(
            @PathVariable Long id,
            @RequestBody InventoryStock stock) {

        return ResponseEntity.ok(inventoryStockService.updateStock(id, stock));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('INVENTORY_DELETE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteStock(@PathVariable Long id) {

        inventoryStockService.deleteStock(id);

        return ResponseEntity.ok("Stock deleted successfully");
    }
}