package com.lms.www.marketing.controller;

import com.lms.www.marketing.service.MarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/leads")
@RequiredArgsConstructor
public class PublicLeadController {

    private final MarketingService marketingService;

    @PostMapping
    public ResponseEntity<Void> captureLead(@RequestBody LeadRequest request) {
        marketingService.createLead(
                request.email(),
                request.mobile(),
                request.batchId()
        );
        return ResponseEntity.ok().build();
    }

    public record LeadRequest(String email, String mobile, Long batchId) {}
}
