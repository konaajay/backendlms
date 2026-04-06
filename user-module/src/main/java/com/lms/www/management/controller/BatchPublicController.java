package com.lms.www.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.Batch;
import com.lms.www.management.service.BatchService;

import lombok.RequiredArgsConstructor;

/**
 * Publicly accessible API for Batch information.
 * Used for Course Overview / Landing pages to display batch details.
 */
@RestController
@RequestMapping("/api/public/batches")
@RequiredArgsConstructor
@CrossOrigin
public class BatchPublicController {

    private final BatchService batchService;

    @GetMapping("/{id}")
    public ResponseEntity<Batch> getBatchDetails(@PathVariable Long id) {
        // No @PreAuthorize to allow public access
        Batch batch = batchService.getBatchById(id);
        return ResponseEntity.ok(batch);
    }
}
