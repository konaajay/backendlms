package com.lms.www.marketing.controller;

import com.lms.www.marketing.service.MarketingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class PublicTrackingController {

    private final MarketingService marketingService;

    @GetMapping("/ref")
    public RedirectView trackAndRedirect(
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "direct") String source,
            HttpServletRequest request) {
        
        String ip = request.getRemoteAddr();
        marketingService.trackClick(code, source, ip);
        
        return new RedirectView("/");
    }
}