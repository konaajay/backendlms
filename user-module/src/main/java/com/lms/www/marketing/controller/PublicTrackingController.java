package com.lms.www.marketing.controller;

import com.lms.www.marketing.service.MarketingService;
import com.lms.www.marketing.service.TrackingService;
import com.lms.www.marketing.dto.TrackingRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketing/public")
public class PublicTrackingController {

    private final MarketingService marketingService;
    private final TrackingService trackingService;

    @GetMapping("/ref")
    public RedirectView trackAndRedirect(
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "direct") String source,
            HttpServletRequest request) {
        
        String ip = request.getRemoteAddr();
        marketingService.trackClick(code, source, ip);
        
        return new RedirectView("/");
    }

    @PostMapping("/track")
    public void trackEvent(@RequestBody TrackingRequest trackingRequest) {
        trackingService.track(trackingRequest);
    }
}