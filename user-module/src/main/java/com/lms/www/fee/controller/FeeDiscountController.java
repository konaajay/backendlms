package com.lms.www.fee.controller;

import com.lms.www.fee.dto.FeeDiscountRequest;
import com.lms.www.fee.dto.FeeDiscountResponse;
import com.lms.www.fee.service.FeeDiscountService;
import com.lms.www.security.UserContext;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/fee-discounts")
@RequiredArgsConstructor
public class FeeDiscountController {

    private final FeeDiscountService feeDiscountService;
    private final UserContext userContext;

    @PostMapping
    @PreAuthorize("hasAuthority('DISCOUNT_CREATE')")
    public ResponseEntity<FeeDiscountResponse> create(@Valid @RequestBody FeeDiscountRequest request) {
        return new ResponseEntity<>(feeDiscountService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW')")
    public ResponseEntity<FeeDiscountResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feeDiscountService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW')")
    public ResponseEntity<List<FeeDiscountResponse>> getAll() {
        return ResponseEntity.ok(feeDiscountService.getAll());
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW_SELF')")
    public ResponseEntity<List<FeeDiscountResponse>> getMyDiscounts() {
        return ResponseEntity.ok(feeDiscountService.getByUserIdSecure(userContext.getCurrentUserId()));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW_ADMIN')")
    public ResponseEntity<List<FeeDiscountResponse>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feeDiscountService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DISCOUNT_UPDATE')")
    public ResponseEntity<FeeDiscountResponse> update(
            @PathVariable Long id, 
            @Valid @RequestBody FeeDiscountRequest request) {
        return ResponseEntity.ok(feeDiscountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DISCOUNT_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feeDiscountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}