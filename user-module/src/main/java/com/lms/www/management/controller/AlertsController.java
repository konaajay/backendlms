package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.InventoryStock;
import com.lms.www.management.service.AlertsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertsController {

    private final AlertsService alertsService;

    // LOW STOCK ALERTS
    @GetMapping("/low-stock")
    @PreAuthorize("hasAuthority('ALERT_VIEW')")
    public ResponseEntity<List<InventoryStock>> getLowStockAlerts() {

        return ResponseEntity.ok(alertsService.getLowStockItems());
    }

}