package com.lms.www.affiliate.controller;

import com.lms.www.affiliate.dto.PricingResponseDTO;
import com.lms.www.affiliate.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping
    public ResponseEntity<PricingResponseDTO> getPrice(
            @RequestParam Long courseId,
            @RequestParam(required = false) String referralCode,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                pricingService.calculatePrice(courseId, referralCode, amount)
        );
    }
}
