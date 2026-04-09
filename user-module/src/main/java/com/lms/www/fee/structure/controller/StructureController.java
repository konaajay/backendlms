package com.lms.www.fee.structure.controller;

import com.lms.www.fee.dto.FeeStructureRequest;
import com.lms.www.fee.dto.FeeStructureResponse;
import com.lms.www.fee.dto.FeeTypeRequest;
import com.lms.www.fee.dto.FeeTypeResponse;
import com.lms.www.fee.structure.service.StructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/structures")
@RequiredArgsConstructor
public class StructureController {

    private final StructureService structureService;

    @PostMapping
    @PreAuthorize("hasAuthority('STRUCTURE_CREATE') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<FeeStructureResponse> createStructure(@Valid @RequestBody FeeStructureRequest request) {
        return ResponseEntity.ok(structureService.createStructure(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STRUCTURE_VIEW') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<FeeStructureResponse> getStructureById(@PathVariable Long id) {
        return ResponseEntity.ok(structureService.getStructureById(id));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('STRUCTURE_VIEW') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<FeeStructureResponse>> getStructuresByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(structureService.getStructuresByCourse(courseId));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('STRUCTURE_VIEW') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<FeeStructureResponse>> getStructuresByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(structureService.getStructuresByBatch(batchId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STRUCTURE_UPDATE') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<FeeStructureResponse> updateStructure(@PathVariable Long id, @Valid @RequestBody FeeStructureRequest request) {
        return ResponseEntity.ok(structureService.updateStructure(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STRUCTURE_DELETE') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteStructure(@PathVariable Long id) {
        structureService.deleteStructure(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/type")
    @PreAuthorize("hasAuthority('FEE_TYPE_CREATE') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<FeeTypeResponse> createFeeType(@Valid @RequestBody FeeTypeRequest request) {
        return ResponseEntity.ok(structureService.createFeeType(request));
    }

    @GetMapping("/type/all")
    @PreAuthorize("hasAuthority('FEE_TYPE_VIEW') or hasAuthority('ALL_PERMISSIONS') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<FeeTypeResponse>> getAllFeeTypes() {
        return ResponseEntity.ok(structureService.getAllFeeTypes());
    }
}
