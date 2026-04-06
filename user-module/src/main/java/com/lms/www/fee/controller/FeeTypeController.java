package com.lms.www.fee.controller;

import com.lms.www.fee.dto.FeeTypeRequest;
import com.lms.www.fee.dto.FeeTypeResponse;
import com.lms.www.fee.service.FeeTypeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/fee-types")
@RequiredArgsConstructor
public class FeeTypeController {

    private final FeeTypeService feeTypeService;

    @PostMapping
    @PreAuthorize("hasAuthority('FEE_TYPE_CREATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<FeeTypeResponse> create(@Valid @RequestBody FeeTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feeTypeService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FEE_TYPE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<FeeTypeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feeTypeService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FEE_TYPE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<FeeTypeResponse>> getAll() {
        return ResponseEntity.ok(feeTypeService.getAll());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('FEE_TYPE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<FeeTypeResponse>> getActive() {
        return ResponseEntity.ok(feeTypeService.getActive());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FEE_TYPE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<FeeTypeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FeeTypeRequest request) {
        return ResponseEntity.ok(feeTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FEE_TYPE_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feeTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}