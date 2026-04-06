package com.lms.www.marketing.controller;

import com.lms.www.marketing.model.Coupon;
import com.lms.www.marketing.service.AdminCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/marketing/admin/coupons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminCouponController {

    private final AdminCouponService couponService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @Valid @RequestBody Coupon coupon) {
        if (id == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(couponService.updateCoupon(id, coupon));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MARKETING_MANAGER') or hasAuthority('ALL_PERMISSIONS')")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        if (id == null) return ResponseEntity.badRequest().build();
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
