package com.lms.www.marketing.controller;

import com.lms.www.marketing.model.ReferralCode;
import com.lms.www.marketing.model.ReferralUsage;
import com.lms.www.marketing.service.ReferralService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing/referral")
@CrossOrigin(origins = "*")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    // ✅ Get my referral code (NO userId from client)
    @GetMapping("/code")
    @PreAuthorize("hasAuthority('REFERRAL_SELF')")
    public ResponseEntity<ReferralCode> getMyCode(Principal principal) {
        Long userId = Long.parseLong(principal.getName()); // adapt based on your auth
        return ResponseEntity.ok(referralService.getOrCreateReferralCode(userId));
    }

    // ✅ Public register
    @PostMapping("/register")
    public ResponseEntity<?> registerWithCode(@RequestBody Map<String, Object> payload) {
        if (payload == null || payload.get("code") == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
        }

        Long refereeId = ((Number) payload.get("refereeId")).longValue();
        String code = (String) payload.get("code");
        Long courseId = ((Number) payload.get("courseId")).longValue();

        referralService.processReferral(refereeId, code, courseId);

        return ResponseEntity.ok(Map.of("message", "Referral registered"));
    }

    // ✅ Get my stats (secure)
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('REFERRAL_SELF')")
    public ResponseEntity<List<ReferralUsage>> getStats(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return ResponseEntity.ok(referralService.getReferralStats(userId));
    }

    // ✅ Internal trigger
    @PostMapping("/admin/trigger")
    @PreAuthorize("hasAuthority('REFERRAL_INTERNAL')")
    public ResponseEntity<?> triggerPurchase(@RequestParam Long refereeId,
            @RequestParam Long courseId) {
        referralService.processFirstPurchase(refereeId, courseId);
        return ResponseEntity.ok(Map.of("message", "Reward processed"));
    }
}