package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.management.model.Batch;
import com.lms.www.management.service.BatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    // ================= CREATE =================
    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('BATCH_CREATE')")
    public ResponseEntity<Batch> createBatch(
            @PathVariable Long courseId,
            @RequestBody Batch batch) {

        Batch createdBatch = batchService.createBatch(courseId, batch);
        return new ResponseEntity<>(createdBatch, HttpStatus.CREATED);
    }

    // ================= GET BY ID =================
    @GetMapping("/{batchId}")
    @PreAuthorize("hasAuthority('BATCH_VIEW')")
    public ResponseEntity<Batch> getBatchById(@PathVariable Long batchId) {

        Batch batch = batchService.getBatchById(batchId);
        return ResponseEntity.ok(batch);
    }

    // ================= GET BY COURSE =================
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('BATCH_VIEW')")
    public ResponseEntity<List<Batch>> getBatchesByCourseId(
            @PathVariable Long courseId) {

        List<Batch> batches =
                batchService.getBatchesByCourseId(courseId);

        return ResponseEntity.ok(batches);
    }

    // ================= GET ALL =================
    @GetMapping
    @PreAuthorize("hasAuthority('BATCH_VIEW')")
    public ResponseEntity<List<Batch>> getAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    // ================= UPDATE =================
    @PutMapping("/{batchId}")
    @PreAuthorize("hasAuthority('BATCH_UPDATE')")
    public ResponseEntity<Batch> updateBatch(
            @PathVariable Long batchId,
            @RequestBody Batch batch) {

        Batch updatedBatch =
                batchService.updateBatch(batchId, batch);

        return ResponseEntity.ok(updatedBatch);
    }

    // ================= DELETE =================
    @DeleteMapping("/{batchId}")
    @PreAuthorize("hasAuthority('BATCH_DELETE')")
    public ResponseEntity<Void> deleteBatch(
            @PathVariable Long batchId) {

        batchService.deleteBatch(batchId);
        return ResponseEntity.noContent().build();
    }
}