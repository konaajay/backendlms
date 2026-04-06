package com.lms.www.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lms.www.model.SystemSettings;
import com.lms.www.model.User;
import com.lms.www.model.UserSession;
import com.lms.www.repository.SystemSettingsRepository;
import com.lms.www.repository.UserRepository;
import com.lms.www.repository.UserSessionRepository;
import com.lms.www.tenant.TenantContext;
import com.lms.www.tenant.TenantRoutingDataSource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final SystemSettingsRepository systemSettingsRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("tenantRoutingDataSource")
    private DataSource dataSource;

    public JwtFilter(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            SystemSettingsRepository systemSettingsRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.systemSettingsRepository = systemSettingsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    private TenantRoutingDataSource routing() {
        if (dataSource instanceof LazyConnectionDataSourceProxy proxy) {
            return (TenantRoutingDataSource) proxy.getTargetDataSource();
        }
        throw new IllegalStateException("TenantRoutingDataSource not found");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        return
                // 🔓 Password reset (logged-out flow)
                path.equals("/auth/password-reset")
                || path.startsWith("/auth/password-reset/")

                // 🔓 Account unlock (logged-out flow)
                || path.equals("/auth/account-unlock")
                || path.startsWith("/auth/account-unlock/")

                // 🔓 Super admin signup (pre-tenant)
                || path.equals("/super-admin/signup")
                || path.startsWith("/super-admin/signup/")

                // 🔓 Super admin disable request
                || path.equals("/super-admin/request-disable")
                || path.startsWith("/super-admin/request-disable/")

                // 🔓 Platform-level APIs
                || path.startsWith("/platform/")

                // 🔓 Public Uploads
                || path.startsWith("/uploads/");
    }

    private String extractSubdomain(HttpServletRequest request) {
        String host = request.getServerName();
        if (host == null || !host.contains(".")) {
            return null;
        }
        return host.split("\\.")[0].toLowerCase();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String domain = request.getServerName();

        // ================================
        // 1️⃣ Extract subdomain & Resolve Tenant Registry
        // ================================
        String subdomain = extractSubdomain(request);
        String tenantDbFromDomain = null;

        if (domain.equals("localhost") || domain.equals("127.0.0.1")) {
            tenantDbFromDomain = "lms_tenant_1770701101086";
        } else if (subdomain != null) {
            try {
                tenantDbFromDomain = jdbcTemplate.queryForObject(
                        "SELECT tenant_db_name FROM tenant_registry WHERE tenant_domain = ?",
                        String.class,
                        subdomain
                );
            } catch (Exception ex) {
                // Not necessarily fatal for all requests, but tenant registry is down
            }
        }

        // ================================
        // 2️⃣ Identify Request Type
        // ================================
        boolean isLoginRequest = path.startsWith("/auth/login");
        boolean isPublicMarketingRequest = path.equals("/website/marketing-community")
                || path.startsWith("/website/marketing-community/");

        boolean isPublicResourceRequest = "GET".equalsIgnoreCase(request.getMethod())
                && (path.startsWith("/api/v1/settings") || path.startsWith("/api/courses") || path.startsWith("/api/v1/courses") || path.startsWith("/api/v1/fee-management/batches/course") || path.startsWith("/inventory-api/"))
                && request.getHeader("Authorization") == null;

        boolean isPublicAffiliateRequest = path.startsWith("/api/v1/public/affiliates/lead");

        // ================================
        // 3️⃣ PUBLIC BYPASS (NO JWT REQUIRED)
        // ================================
        if (isLoginRequest || isPublicMarketingRequest || isPublicResourceRequest || isPublicAffiliateRequest) {
            if (!isLoginRequest) {
                // Set context for public requests
                if (tenantDbFromDomain != null) {
                    routing().addTenant(tenantDbFromDomain);
                    TenantContext.setTenant(tenantDbFromDomain);
                }
            }
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ================================
            // 4️⃣ Extract Token
            // ================================
            String token = null;
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ================================
            // 5️⃣ JWT VALIDATION
            // ================================
            try {
                jwtUtil.validateToken(token);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ================================
            // 6️⃣ EXTRACT TENANT DB FROM JWT
            // ================================
            String tenantDb = jwtUtil.extractTenantDb(token);
            if (tenantDb == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // ================================
            // 7️⃣ DOMAIN ↔ TENANT VALIDATION
            // ================================
            if (subdomain != null && !path.startsWith("/platform/")) {
                if (tenantDbFromDomain == null || !tenantDb.equals(tenantDbFromDomain)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            // ================================
            // 8️⃣ SWITCH TO TENANT DB
            // ================================
            routing().addTenant(tenantDb);
            TenantContext.setTenant(tenantDb);

            // ================================
            // 9️⃣ LOAD USER
            // ================================
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // ================================
            // 🔟 VALIDATE SESSION
            // ================================
            UserSession session = userSessionRepository
                    .findByTokenAndLogoutTimeIsNull(token)
                    .orElse(null);

            if (session == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ================================
            // 1️⃣1️⃣ SESSION TIMEOUT CHECK
            // ================================
            SystemSettings settings = systemSettingsRepository
                    .findByUserId(user.getUserId())
                    .orElse(null);

            if (settings != null && settings.getSessionTimeout() != null) {
                LocalDateTime lastActivity =
                        session.getLastActivityTime() != null
                                ? session.getLastActivityTime()
                                : session.getLoginTime();

                long idleMinutes = ChronoUnit.MINUTES.between(
                        lastActivity,
                        LocalDateTime.now()
                );

                if (idleMinutes >= settings.getSessionTimeout()) {
                    session.setLogoutTime(LocalDateTime.now());
                    userSessionRepository.save(session);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            // ================================
            // 1️⃣2️⃣ UPDATE ACTIVITY
            // ================================
            session.setLastActivityTime(LocalDateTime.now());
            userSessionRepository.save(session);

            // ================================
            // 1️⃣3️⃣ BUILD AUTHORITIES
            // ================================
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            List<String> roles = jwtUtil.extractRoles(token);
            boolean isSuperAdmin = false;
            if (roles != null) {
                for (String role : roles) {
                    String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    authorities.add(new SimpleGrantedAuthority(roleName));
                    if ("ROLE_SUPER_ADMIN".equals(roleName)) {
                        isSuperAdmin = true;
                    }
                }
            }

            List<String> permissions = jwtUtil.extractPermissions(token);
            if (permissions != null) {
                boolean hasAll = permissions.contains("*") || isSuperAdmin;
                for (String perm : permissions) {
                    authorities.add(new SimpleGrantedAuthority(perm));
                }
                if (hasAll) {
                    // 🚀 GOD MODE AUTHORITY
                    authorities.add(new SimpleGrantedAuthority("ALL_PERMISSIONS"));

                    // Inject common permissions if wildcard is present
                    authorities.add(new SimpleGrantedAuthority("COURSE_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("COURSE_CREATE"));
                    authorities.add(new SimpleGrantedAuthority("COURSE_UPDATE"));
                    authorities.add(new SimpleGrantedAuthority("COURSE_DELETE"));
                    authorities.add(new SimpleGrantedAuthority("SETTING_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("SETTING_UPDATE"));
                    authorities.add(new SimpleGrantedAuthority("USER_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("USER_CREATE"));
                    authorities.add(new SimpleGrantedAuthority("USER_UPDATE"));
                    authorities.add(new SimpleGrantedAuthority("USER_DELETE"));
                    authorities.add(new SimpleGrantedAuthority("ADMIN_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("ADMIN_CREATE"));
                    authorities.add(new SimpleGrantedAuthority("ADMIN_UPDATE"));
                    authorities.add(new SimpleGrantedAuthority("FEE_STRUCTURE_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("FEE_STRUCTURE_CREATE"));
                    authorities.add(new SimpleGrantedAuthority("FEE_STRUCTURE_UPDATE"));
                    authorities.add(new SimpleGrantedAuthority("FEE_TYPE_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("FEE_TYPE_VIEW_SELF"));
                    authorities.add(new SimpleGrantedAuthority("PAYMENT_VIEW_ALL"));
                    authorities.add(new SimpleGrantedAuthority("PAYMENT_ADD"));
                    authorities.add(new SimpleGrantedAuthority("STUDENT_BATCH_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("STUDENT_BATCH_CREATE"));
                    authorities.add(new SimpleGrantedAuthority("WEBINAR_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("WEBINAR_CREATE"));
                    authorities.add(new SimpleGrantedAuthority("WEBINAR_LIST"));
                    authorities.add(new SimpleGrantedAuthority("WEBSITE_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("WEBSITE_EDIT"));
                    authorities.add(new SimpleGrantedAuthority("BATCH_VIEW"));
                    authorities.add(new SimpleGrantedAuthority("BATCH_CREATE"));
                    authorities.add(new SimpleGrantedAuthority("BATCH_UPDATE"));
                    authorities.add(new SimpleGrantedAuthority("BATCH_DELETE"));
                }
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            authorities
                    );

            authentication.setDetails(user);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("authenticatedUser", user);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            TenantContext.clear();
        }
    }
}