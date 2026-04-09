package com.lms.www.marketing.controller;

import com.lms.www.marketing.dto.CouponApplyRequest;
import com.lms.www.marketing.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing/coupons/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicCouponController {

    private final CouponService couponService;

    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyCoupon(@RequestBody CouponApplyRequest request) {
        try {
            // First perform semantic validation (expiry, minimum bounds, etc)
            couponService.performValidation(request.getCode(), request.getCourseId(), request.getAmount(), request.getLearnerId());
            
            // Calculate the discount
            Double discountAmt = couponService.calculateDiscount(request.getCode(), request.getAmount());
            
            // Respond with the calculated discount amount to the frontend
            return ResponseEntity.ok(Collections.singletonMap("discountAmount", discountAmt));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
