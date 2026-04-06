package com.lms.www.fee.controller;

import com.lms.www.fee.dto.FeeStructureRequest;
import com.lms.www.fee.dto.FeeStructureResponse;
import com.lms.www.fee.structure.service.StructureService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/fee-structures")
@RequiredArgsConstructor
public class FeeStructureController {

    private final StructureService service;

    @PostMapping
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_CREATE')")
    public ResponseEntity<FeeStructureResponse> create(@Valid @RequestBody FeeStructureRequest request) {
        return ResponseEntity.ok(service.createStructure(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_VIEW')")
    public ResponseEntity<FeeStructureResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStructureById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_VIEW')")
    public ResponseEntity<List<FeeStructureResponse>> getAll() {
        return ResponseEntity.ok(service.getAllStructures());
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_VIEW')")
    public ResponseEntity<List<FeeStructureResponse>> getByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(service.getStructuresByBatch(batchId));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_VIEW')")
    public ResponseEntity<List<FeeStructureResponse>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(service.getStructuresByCourse(courseId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_UPDATE')")
    public ResponseEntity<FeeStructureResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FeeStructureRequest request) {
        return ResponseEntity.ok(service.updateStructure(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_DELETE')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deleteStructure(id);
        return ResponseEntity.noContent().build();
    }
}