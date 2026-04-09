package com.lms.www.fee.controller;

import com.lms.www.fee.dto.InstallmentPlanRequest;
import com.lms.www.fee.dto.InstallmentPlanResponse;
import com.lms.www.fee.dto.InstallmentUpdateRequest;
import com.lms.www.fee.service.StudentInstallmentPlanService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/installments")
@RequiredArgsConstructor
public class StudentInstallmentPlanController {

    private final StudentInstallmentPlanService service;

    // ================= ADMIN =================

    @PostMapping("/student")
    @PreAuthorize("hasAnyAuthority('INSTALLMENT_CREATE', 'ALLOCATION_CREATE', 'FEE_STRUCTURE_CREATE') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<InstallmentPlanResponse>> createForStudent(
            @RequestParam Long allocationId,
            @Valid @RequestBody List<InstallmentPlanRequest> plans) {
        return ResponseEntity.ok(service.createInstallments(allocationId, plans));
    }

    @RequestMapping(value = {"/override", "/override/{allocationId}"}, method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasAnyAuthority('INSTALLMENT_RESET', 'INSTALLMENT_UPDATE', 'INSTALLMENT_CREATE', 'FEE_STRUCTURE_CREATE', 'ALLOCATION_CREATE') or hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<InstallmentPlanResponse>> override(
            @PathVariable(required = false) Long allocationId,
            @RequestParam(value = "allocationId", required = false) Long allocationIdQuery,
            @Valid @RequestBody List<InstallmentPlanRequest> plans) {
        Long id = allocationId != null ? allocationId : allocationIdQuery;
        if (id == null) {
            throw new IllegalArgumentException("Allocation ID is required");
        }
        return ResponseEntity.ok(service.resetInstallments(id, plans));
    }

    @GetMapping({"/plan/{allocationId}", "/allocation/{allocationId}"})
    @PreAuthorize("hasAuthority('INSTALLMENT_VIEW') or hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'PARENT', 'STUDENT')")
    public ResponseEntity<List<InstallmentPlanResponse>> getByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(service.getByAllocationSecure(allocationId));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('INSTALLMENT_VIEW_OVERDUE')")
    public ResponseEntity<List<InstallmentPlanResponse>> getOverdue() {
        return ResponseEntity.ok(service.getOverdueInstallments());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteInstallment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/extend")
    @PreAuthorize("hasAuthority('INSTALLMENT_EXTEND')")
    public ResponseEntity<InstallmentPlanResponse> extend(
            @PathVariable Long id,
            @Valid @RequestBody InstallmentUpdateRequest request) {
        return ResponseEntity.ok(service.extendDueDate(id, request));
    }

    // ================= MERGED FROM InstallmentController =================

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_VIEW') or hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'PARENT', 'STUDENT')")
    public ResponseEntity<List<InstallmentPlanResponse>> getInstallmentsByAllocationLegacy(@PathVariable Long id) {
        return ResponseEntity.ok(service.getByAllocationSecure(id));
    }

    @GetMapping("/record/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_VIEW') or hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ACCOUNTANT', 'PARENT', 'STUDENT')")
    public ResponseEntity<InstallmentPlanResponse> getSingleInstallmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/status/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_UPDATE')")
    public ResponseEntity<Void> updateInstallmentStatus(@PathVariable Long id, @RequestParam String status) {
        service.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lock/{id}")
    @PreAuthorize("hasAuthority('INSTALLMENT_LOCK')")
    public ResponseEntity<Void> lockForEarlyPayment(@PathVariable Long id) {
        service.lockForEarlyPayment(id);
        return ResponseEntity.noContent().build();
    }
}