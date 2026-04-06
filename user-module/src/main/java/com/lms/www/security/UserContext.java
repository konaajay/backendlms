package com.lms.www.security;

import com.lms.www.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class UserContext {

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() != null) {
            Object details = auth.getDetails();
            if (details instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) details;
                Object id = map.get("userId");
                if (id instanceof Number) {
                    return ((Number) id).longValue();
                }
            } else if (details instanceof User) {
                return ((User) details).getUserId();
            }
        }
        return null; 
    }

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName(); // Usually email is the principal name in JWT-based systems
        }
        return null;
    }

    public String getCurrentUsername() {
        return getCurrentUserEmail(); // Assuming they are the same in this system
    }

    public boolean isStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    public boolean isParent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARENT"));
    }
}
