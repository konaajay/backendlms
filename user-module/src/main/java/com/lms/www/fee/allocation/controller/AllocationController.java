package com.lms.www.fee.allocation.controller;

import com.lms.www.fee.dto.*;
import com.lms.www.fee.allocation.service.AllocationService;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService service;
    private final UserContext userContext;

    // ================= ADMIN =================

    @GetMapping
    @PreAuthorize("hasAuthority('ALLOCATION_VIEW')")
    public ResponseEntity<List<StudentFeeAllocationResponse>> getAll() {
        return ResponseEntity.ok(service.getAllAllocations());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ALLOCATION_CREATE')")
    public ResponseEntity<StudentFeeAllocationResponse> create(@Valid @RequestBody CreateAllocationRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ALLOCATION_CREATE')")
    public ResponseEntity<List<StudentFeeAllocationResponse>> createBulk(@Valid @RequestBody BulkAllocationRequest request) {
        return ResponseEntity.ok(service.createBulk(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ALLOCATION_VIEW')")
    public ResponseEntity<StudentFeeAllocationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ALLOCATION_VIEW')")
    public ResponseEntity<List<StudentFeeAllocationResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getByUser(userId));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('ALLOCATION_VIEW')")
    public ResponseEntity<List<StudentFeeAllocationResponse>> getByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(service.getByBatch(batchId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ALLOCATION_UPDATE')")
    public ResponseEntity<StudentFeeAllocationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAllocationRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ALLOCATION_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync-all")
    @PreAuthorize("hasAuthority('ALLOCATION_SYNC')")
    public ResponseEntity<Void> syncAll() {
        service.syncAllStudentInfo();
        return ResponseEntity.ok().build();
    }

    // ================= SECURE (STUDENT/PARENT) =================

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ALLOCATION_VIEW_SELF')")
    public ResponseEntity<List<StudentFeeAllocationResponse>> getMyAllocations() {
        return ResponseEntity.ok(service.getByUserSecure(userContext.getCurrentUserId()));
    }

    @GetMapping("/me/latest")
    @PreAuthorize("hasAuthority('ALLOCATION_VIEW_SELF')")
    public ResponseEntity<StudentFeeAllocationResponse> getMyLatest() {
        return ResponseEntity.ok(service.getLatestSecure(userContext.getCurrentUserId()));
    }

    @GetMapping("/parent/my-children")
    @PreAuthorize("hasAuthority('ALLOCATION_VIEW_CHILD')")
    public ResponseEntity<List<StudentFeeAllocationResponse>> getMyChildrenAllocations() {
        return ResponseEntity.ok(service.getByParentSecure(userContext.getCurrentUserId()));
    }
}
