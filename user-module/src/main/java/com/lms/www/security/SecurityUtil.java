package com.lms.www.security;

import com.lms.www.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            System.err.println("[SecurityUtil] No authenticated user found in context");
            return null;
        }
        try {
            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return ((CustomUserDetails) principal).getId();
            }
            if (principal instanceof Long) {
                return (Long) principal;
            }
            
            // Fallback for DevTools classloader issues
            try {
                java.lang.reflect.Method getIdMethod = principal.getClass().getMethod("getId");
                Object id = getIdMethod.invoke(principal);
                if (id instanceof Long) return (Long) id;
                if (id instanceof Integer) return ((Integer) id).longValue();
            } catch (Exception ignored) {}
            
            System.err.println("[SecurityUtil] Principal is not an instance of CustomUserDetails. Actual type: " + (principal != null ? principal.getClass().getName() : "null"));
            return null;
        } catch (Exception e) {
            System.err.println("[SecurityUtil] Error extracting user ID: " + e.getMessage());
            return null;
        }
    }

    public Long getInstructorId() {
        return getUserId();
    }
}
