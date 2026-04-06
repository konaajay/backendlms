package com.lms.www.fee.discount.controller;

import com.lms.www.fee.discount.service.DiscountService;
import com.lms.www.fee.dto.DiscountRequest;
import com.lms.www.fee.dto.DiscountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fee-management/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('DISCOUNT_CREATE')")
    public ResponseEntity<DiscountResponse> applyDiscount(@Valid @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(discountService.applyDiscount(request));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW')")
    public ResponseEntity<List<DiscountResponse>> getDiscountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(discountService.getDiscountsByUser(userId));
    }

    @GetMapping("/structure/{structureId}")
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW')")
    public ResponseEntity<List<DiscountResponse>> getDiscountsByStructure(@PathVariable Long structureId) {
        return ResponseEntity.ok(discountService.getDiscountsByStructure(structureId));
    }

    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('DISCOUNT_APPROVE')")
    public ResponseEntity<DiscountResponse> approveDiscount(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.approveDiscount(id));
    }

    @PostMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('DISCOUNT_DELETE')")
    public ResponseEntity<Void> deactivateDiscount(@PathVariable Long id) {
        discountService.deactivateDiscount(id);
        return ResponseEntity.noContent().build();
    }
}
