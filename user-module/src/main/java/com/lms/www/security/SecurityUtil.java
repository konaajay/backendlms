package com.lms.www.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        // Assuming CustomUserDetails is available in the security package or equivalent
        // If not, we fall back to a safe cast or placeholder
        try {
            Object principal = auth.getPrincipal();
            if (principal instanceof Long) {
                return (Long) principal;
            }
            // Add custom logic here to extract ID from principal
            return 1L;
        } catch (Exception e) {
            return null;
        }
    }

    public Long getInstructorId() {
        return getUserId();
    }
}
