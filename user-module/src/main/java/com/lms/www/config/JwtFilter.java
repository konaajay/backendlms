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
import org.springframework.lang.NonNull;

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
    private static final java.util.Set<String> MIGRATED_TENANTS = java.util.Collections
            .synchronizedSet(new java.util.HashSet<>());

    @Autowired
    @Qualifier("tenantRoutingDataSource")
    private DataSource dataSource;

    public JwtFilter(
            JwtUtil jwtUtil,
            UserRepository userRepository,
            UserSessionRepository userSessionRepository,
            SystemSettingsRepository systemSettingsRepository,
            JdbcTemplate jdbcTemplate) {
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
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        return
        // 🔓 Login
        path.startsWith("/api/auth/login")
                || path.startsWith("/auth/login")

                // 🔓 Password reset (logged-out flow)
                || path.equals("/auth/password-reset")
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
                || path.startsWith("/uploads/")

                // 🔓 Webhooks
                || path.startsWith("/api/v1/fee-management/webhooks/")

                // 🔓 Static Assets & Dev Tools
                || path.contains(".") && (path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".png") 
                || path.endsWith(".jpg") || path.endsWith(".svg") || path.endsWith(".ico") || path.endsWith(".json")
                || path.contains("hot-update") || path.contains("socket.io"));
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
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
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

            // 🚀 LAZY MIGRATION (Only run once per lifecycle)
            if (MIGRATED_TENANTS.add(tenantDbFromDomain)) {
                try {
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDbFromDomain
                                + ".affiliates ADD COLUMN commission_type VARCHAR(50) DEFAULT 'PERCENTAGE' AFTER status;");
                    } catch (Exception e) {
                        /* ignore if exists */ }

                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDbFromDomain
                                + ".affiliates ADD COLUMN withdrawal_enabled BOOLEAN DEFAULT TRUE AFTER upi_id;");
                    } catch (Exception e) {
                        /* ignore if exists */ }

                    // 🚀 LAZY MIGRATION for Commission Rules Schema
                    try {
                        String[] cols = {
                                "affiliate_percent DECIMAL(19,4) NOT NULL DEFAULT 0.0",
                                "student_discount_percent DECIMAL(19,4) NOT NULL DEFAULT 0.0",
                                "course_id BIGINT",
                                "is_bonus BOOLEAN DEFAULT FALSE",
                                "created_at DATETIME"
                        };
                        for (String col : cols) {
                            try {
                                jdbcTemplate.execute(
                                        "ALTER TABLE " + tenantDbFromDomain + ".commission_rules ADD COLUMN " + col);
                            } catch (Exception e) {
                                /* ignore if exists */ }
                        }
                    } catch (Exception e) {
                        logger.warn("Migration for commission_rules failed: " + e.getMessage());
                    }

                    // 🚀 LAZY MIGRATION for Parent Student Mapping
                    try {
                        jdbcTemplate.execute(
                                "CREATE TABLE IF NOT EXISTS " + tenantDbFromDomain + ".parent_student_mapping (" +
                                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                        "parent_user_id BIGINT NOT NULL, " +
                                        "student_id BIGINT NOT NULL);");
                    } catch (Exception e2) {
                        /* ignore if mapping exists */ }

                    // 🚀 LAZY MIGRATION for Leads tracking
                    try {
                        String[] leadCols = {
                                "course_interest VARCHAR(255)",
                                "referral_code VARCHAR(255)",
                                "courseInterest VARCHAR(255)",
                                "referralCode VARCHAR(255)"
                        };
                        for (String col : leadCols) {
                            try {
                                jdbcTemplate.execute("ALTER TABLE " + tenantDbFromDomain + ".leads ADD COLUMN " + col);
                            } catch (Exception e) {
                                /* ignore if exists */ }
                        }
                    } catch (Exception e) {
                        logger.warn("Migration for leads failed: " + e.getMessage());
                    }

                    // 🚀 LAZY MIGRATION for Student Fee Payments (Cashfree)
                    try {
                        String[] paymentCols = {
                                "cashfree_order_id VARCHAR(255)",
                                "payment_session_id VARCHAR(255)",
                                "gateway_payment_status VARCHAR(255)",
                                "gateway_response TEXT",
                                "raw_gateway_response TEXT",
                                "gateway_amount DECIMAL(12,2)",
                                "gateway_payment_time DATETIME",
                                "signature_verified BOOLEAN DEFAULT FALSE",
                                "currency VARCHAR(10) DEFAULT 'INR'",
                                "installment_total DECIMAL(12,2)",
                                "discount_percentage DECIMAL(5,2)",
                                "penalty_amount DECIMAL(12,2)",
                                "overdue_remaining DECIMAL(12,2)",
                                "remarks TEXT",
                                "screenshot_url VARCHAR(255)",
                                "recorded_by BIGINT",
                                "student_installment_plan_id BIGINT"
                        };
                        for (String col : paymentCols) {
                            try {
                                jdbcTemplate.execute("ALTER TABLE " + tenantDbFromDomain
                                        + ".student_fee_payments ADD COLUMN " + col);
                            } catch (Exception e) {
                                /* ignore if exists */ }
                        }
                    } catch (Exception e) {
                        logger.warn("Migration for student_fee_payments failed: " + e.getMessage());
                    }
                    // 🚀 LAZY MIGRATION for installments
                    try {
                        String[] instCols = {
                                "installment_amount DECIMAL(12,2)",
                                "cashfree_order_id VARCHAR(255)",
                                "payment_session_id VARCHAR(255)",
                                "link_created_at DATETIME",
                                "link_expiry DATETIME",
                                "label VARCHAR(255)",
                                "user_id BIGINT"
                        };
                        for (String col : instCols) {
                            try {
                                jdbcTemplate.execute("ALTER TABLE " + tenantDbFromDomain
                                        + ".student_installment_plans ADD COLUMN " + col);
                            } catch (Exception e) {
                                /* ignore if exists */ }
                        }
                    } catch (Exception e) {
                        logger.warn("Migration for student_installment_plans failed: " + e.getMessage());
                    }

                    // 🚀 LAZY MIGRATION for Exam Response (Feedback)
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDbFromDomain
                                + ".exam_response ADD COLUMN feedback TEXT AFTER evaluation_type;");
                    } catch (Exception e) {
                        /* ignore if column already exists */ }

                } catch (Exception e) {
                    logger.error("Global migration fail: " + e.getMessage());
                }
            }
        } else if (subdomain != null) {
            try {
                tenantDbFromDomain = jdbcTemplate.queryForObject(
                        "SELECT tenant_db_name FROM tenant_registry WHERE tenant_domain = ?",
                        String.class,
                        subdomain);
            } catch (Exception ex) {
                // Not fatal but tenant registry lookup failed
            }
        }

        // 🏠 DEVELOPMENT OVERRIDE: If resolved tenant is still null but we are on
        // localhost
        // (Vite proxy often spoofs Host headers, so we trust lms_tenant_1770701101086
        // for dev)
        if (tenantDbFromDomain == null) {
            boolean appearsToBeLocalhost = domain.contains("localhost") ||
                    domain.contains("127.0.0.1") ||
                    (request.getHeader("Host") != null && request.getHeader("Host").contains("localhost")) ||
                    (request.getHeader("Origin") != null && request.getHeader("Origin").contains("localhost")) ||
                    (request.getHeader("Referer") != null && request.getHeader("Referer").contains("localhost"));

            // 🚀 AGGRESSIVE BYPASS: In dev mode, if we haven't found a tenant yet, use the
            // default testing one
            if (appearsToBeLocalhost || domain.contains("yourdomain.com")) {
                tenantDbFromDomain = "lms_tenant_1770701101086";
            }
        }

        // ================================
        // 2️⃣ Identify Request Type
        // ================================
        boolean isLoginRequest = path.startsWith("/auth/login");
        boolean isPublicMarketingRequest = path.equals("/website/marketing-community")
                || path.startsWith("/website/marketing-community/");

        boolean isPublicResourceRequest = ("GET".equalsIgnoreCase(request.getMethod())
                && (path.startsWith("/api/v1/settings") || path.startsWith("/api/courses")
                        || path.startsWith("/api/v1/courses")
                        || path.startsWith("/api/v1/fee-management/batches/course")
                        || path.startsWith("/inventory-api/") || path.startsWith("/api/webinars")
                        || path.startsWith("/api/certificates/public/")
                        || path.startsWith("/api/v1/fee/payments/public/")));

        boolean isPublicActionRequest = ("POST".equalsIgnoreCase(request.getMethod())
                && (path.startsWith("/api/webinar-registrations/external")
                        || path.startsWith("/api/external-participants")
                        || path.startsWith("/api/v1/fee-management/webhooks/")));

        boolean isPublicAffiliateRequest = path.startsWith("/api/v1/public/affiliates/lead");
        boolean isPublicPaymentRequest = path.startsWith("/api/v1/fee/payments/public/");

        // ================================
        // 3️⃣ PUBLIC BYPASS (NO JWT REQUIRED)
        // ================================
        if (isLoginRequest || isPublicMarketingRequest || isPublicResourceRequest || isPublicActionRequest
                || isPublicAffiliateRequest || isPublicPaymentRequest) {
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
                // If on localhost development, we skip the strict tenant validation since proxy
                // spoofs the Host
                boolean isLocalhost = domain.contains("localhost") || domain.contains("127.0.0.1") ||
                        request.getHeader("Origin") != null && request.getHeader("Origin").contains("localhost") ||
                        request.getHeader("Referer") != null && request.getHeader("Referer").contains("localhost");

                if (!isLocalhost && (tenantDbFromDomain == null || !tenantDb.equals(tenantDbFromDomain))) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            // ================================
            // 8️⃣ SWITCH TO TENANT DB
            // ================================
            routing().addTenant(tenantDb);
            TenantContext.setTenant(tenantDb);

            // 🚀 EMERGENCY MIGRATIONS (Consolidated to save connections)
            if (MIGRATED_TENANTS.add(tenantDb)) {
                try {
                    // 1. Commission Rules
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDb + ".commission_rules " +
                                "ADD COLUMN IF NOT EXISTS affiliate_percent DECIMAL(19,4) NOT NULL DEFAULT 0.0, " +
                                "ADD COLUMN IF NOT EXISTS student_discount_percent DECIMAL(19,4) NOT NULL DEFAULT 0.0, " +
                                "ADD COLUMN IF NOT EXISTS course_id BIGINT, " +
                                "ADD COLUMN IF NOT EXISTS is_bonus BOOLEAN DEFAULT FALSE, " +
                                "ADD COLUMN IF NOT EXISTS created_at DATETIME;");
                    } catch (Exception e) { /* fallback case-by-手 for older mysql versions if needed */ }

                    // 2. Affiliates
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDb + ".affiliates " +
                                "ADD COLUMN IF NOT EXISTS commission_type VARCHAR(50) DEFAULT 'PERCENTAGE' AFTER status, " +
                                "ADD COLUMN IF NOT EXISTS withdrawal_enabled BOOLEAN DEFAULT TRUE AFTER upi_id;");
                    } catch (Exception e) { }

                    // 3. Student Fee Payments
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDb + ".student_fee_payments " +
                                "ADD COLUMN IF NOT EXISTS cashfree_order_id VARCHAR(255), " +
                                "ADD COLUMN IF NOT EXISTS payment_session_id VARCHAR(255), " +
                                "ADD COLUMN IF NOT EXISTS gateway_payment_status VARCHAR(255), " +
                                "ADD COLUMN IF NOT EXISTS gateway_amount DECIMAL(12,2), " +
                                "ADD COLUMN IF NOT EXISTS signature_verified BOOLEAN DEFAULT FALSE, " +
                                "ADD COLUMN IF NOT EXISTS student_installment_plan_id BIGINT, " +
                                "ADD COLUMN IF NOT EXISTS discount_percentage DECIMAL(5,2), " +
                                "ADD COLUMN IF NOT EXISTS recorded_by BIGINT;");
                    } catch (Exception e) { }

                    // 4. Installment Plans
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDb + ".student_installment_plans " +
                                "ADD COLUMN IF NOT EXISTS installment_amount DECIMAL(12,2), " +
                                "ADD COLUMN IF NOT EXISTS cashfree_order_id VARCHAR(255), " +
                                "ADD COLUMN IF NOT EXISTS label VARCHAR(255), " +
                                "ADD COLUMN IF NOT EXISTS user_id BIGINT;");
                    } catch (Exception e) { }

                    // 5. Exam Response
                    try {
                        jdbcTemplate.execute("ALTER TABLE " + tenantDb + ".exam_response " +
                                "ADD COLUMN IF NOT EXISTS feedback TEXT AFTER evaluation_type;");
                    } catch (Exception e) { }

                } catch (Exception e) {
                    logger.warn("Consolidated migration failed for " + tenantDb + ": " + e.getMessage());
                }
            }

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
                LocalDateTime lastActivity = session.getLastActivityTime() != null
                        ? session.getLastActivityTime()
                        : session.getLoginTime();

                long idleMinutes = ChronoUnit.MINUTES.between(
                        lastActivity,
                        LocalDateTime.now());

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
            boolean isMarketingManager = false;
            boolean isInstructorOrAdmin = false;

            if (roles != null) {
                for (String role : roles) {
                    String cleanRole = role.toUpperCase();
                    String roleName = cleanRole.startsWith("ROLE_") ? cleanRole : "ROLE_" + cleanRole;
                    authorities.add(new SimpleGrantedAuthority(roleName));
                    
                    if ("ROLE_SUPER_ADMIN".equals(roleName)) {
                        isSuperAdmin = true;
                    }
                    if ("ROLE_MARKETING_MANAGER".equals(roleName)) {
                        isMarketingManager = true;
                    }
                    if (roleName.contains("INSTRUCTOR") || roleName.contains("ADMIN")) {
                        isInstructorOrAdmin = true;
                    }
                }
            }

            // 🚀 Marketing Manager "GOD MODE"
            if (isMarketingManager) {
                authorities.add(new SimpleGrantedAuthority("MARKETING_ANALYTICS_VIEW"));
                authorities.add(new SimpleGrantedAuthority("MARKETING_ANALYTICS_SUMMARY"));
                authorities.add(new SimpleGrantedAuthority("MARKETING_CAMPAIGN_VIEW"));
                authorities.add(new SimpleGrantedAuthority("MARKETING_TRACKED_LINK_VIEW"));
                authorities.add(new SimpleGrantedAuthority("MARKETING_TRACKED_LINK_CREATE"));
                authorities.add(new SimpleGrantedAuthority("MARKETING_TRACKED_LINK_DELETE"));
                authorities.add(new SimpleGrantedAuthority("MARKETING_MANAGER")); // Explicit for controllers checking
                                                                                  // without ROLE_ prefix
            }

            // 🚀 Affiliate "GOD MODE"
            if (roles != null && roles.stream().anyMatch(r -> {
                String R = r.toUpperCase();
                return R.contains("AFFILIATE");
            })) {
                authorities.add(new SimpleGrantedAuthority("STUDENT_AFFILIATE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("STUDENT_AFFILIATE_JOIN"));
                authorities.add(new SimpleGrantedAuthority("STUDENT_AFFILIATE_REGISTER"));
                authorities.add(new SimpleGrantedAuthority("AFFILIATE_PORTAL_VIEW"));
                authorities.add(new SimpleGrantedAuthority("AFFILIATE_BANK_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("AFFILIATE_SALE_VIEW_OWN"));
                authorities.add(new SimpleGrantedAuthority("AFFILIATE_LEAD_VIEW_OWN"));
            }

            // 🚀 Student Essential Authorities
            if (roles != null && roles.stream().anyMatch(r -> {
                String R = r.toUpperCase();
                return R.contains("STUDENT") || R.contains("USER");
            })) {
                // Also give students view access to exams and batches
                authorities.add(new SimpleGrantedAuthority("EXAM_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_ATTEMPT_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_ATTEMPT_START"));
                authorities.add(new SimpleGrantedAuthority("EXAM_ATTEMPT_SUBMIT"));
                authorities.add(new SimpleGrantedAuthority("EXAM_RESPONSE_SAVE"));
                authorities.add(new SimpleGrantedAuthority("STUDENT_BATCH_VIEW"));
                authorities.add(new SimpleGrantedAuthority("BATCH_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_SCHEDULE_VIEW"));

                // Also give students view access to affiliate portal if they want to join
                authorities.add(new SimpleGrantedAuthority("AFFILIATE_PORTAL_VIEW"));

                // Webinar participation for students
                authorities.add(new SimpleGrantedAuthority("WEBINAR_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_REGISTER"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_QUESTION_ASK"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_QUESTION_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CHAT_SEND"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CHAT_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_POLL_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_POLL_VOTE"));
            }

            // 🚀 Admin / Instructor Essential Authorities
            if (roles != null && roles.stream().anyMatch(r -> {
                String R = r.toUpperCase();
                return R.contains("ADMIN") || R.contains("INSTRUCTOR");
            })) {
                authorities.add(new SimpleGrantedAuthority("BATCH_VIEW"));
                authorities.add(new SimpleGrantedAuthority("BATCH_CREATE"));
                authorities.add(new SimpleGrantedAuthority("BATCH_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("BATCH_DELETE"));
                authorities.add(new SimpleGrantedAuthority("STUDENT_BATCH_VIEW"));

                authorities.add(new SimpleGrantedAuthority("SESSION_VIEW"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CREATE"));
                authorities.add(new SimpleGrantedAuthority("SESSION_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("SESSION_DELETE"));

                authorities.add(new SimpleGrantedAuthority("ATTENDANCE_RECORD_VIEW"));
                authorities.add(new SimpleGrantedAuthority("ATTENDANCE_RECORD_CREATE"));
                authorities.add(new SimpleGrantedAuthority("ATTENDANCE_RECORD_UPDATE"));

                authorities.add(new SimpleGrantedAuthority("EXAM_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_CREATE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_DELETE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_PUBLISH"));
                authorities.add(new SimpleGrantedAuthority("EXAM_CLOSE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_SCHEDULE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_SCHEDULE_CREATE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_RESPONSE_EVALUATE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_RESPONSE_EVALUATE"));

                authorities.add(new SimpleGrantedAuthority("QUESTION_BANK_VIEW"));
                authorities.add(new SimpleGrantedAuthority("QUESTION_BANK_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("CODING_TEST_CASE_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("CODING_TEST_CASE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_QUESTION_MANAGE"));

                authorities.add(new SimpleGrantedAuthority("WEBINAR_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CREATE"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CANCEL"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_REGISTER"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_REGISTRATION_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_REGISTRATION_CANCEL"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_RECORDING_UPLOAD"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_QUESTION_ASK"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_QUESTION_ANSWER"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_QUESTION_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CHAT_SEND"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CHAT_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_POLL_CREATE"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_POLL_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_POLL_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_POLL_DELETE"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_POLL_VOTE"));

                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_GENERATE"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_VIEW_ALL"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_VIEW_SELF"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_TEMPLATE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_TEMPLATE_CREATE"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_TEMPLATE_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_TEMPLATE_DELETE"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_REVOKE"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_UPDATE_EXPIRY"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_RENEW"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_STATS_VIEW"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_BULK_GENERATE"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_SEND_EMAIL"));

                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_VIEW"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_CREATE"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_DELETE"));

                // 🚀 Inject essential Topic/Content permissions for Instructors & Admins
                authorities.add(new SimpleGrantedAuthority("TOPIC_CREATE"));
                authorities.add(new SimpleGrantedAuthority("TOPIC_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("TOPIC_DELETE"));
                authorities.add(new SimpleGrantedAuthority("CONTENT_ADD"));
                authorities.add(new SimpleGrantedAuthority("CONTENT_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("CONTENT_DELETE"));
            }

            List<String> permissions = jwtUtil.extractPermissions(token);
            boolean hasAll = isSuperAdmin || isInstructorOrAdmin || (permissions != null && permissions.contains("*"));

            if (permissions != null) {
                for (String perm : permissions) {
                    authorities.add(new SimpleGrantedAuthority(perm));
                }
            }

            if (hasAll || isSuperAdmin || isInstructorOrAdmin) {
                // 🚀 GOD MODE AUTHORITY
                authorities.add(new SimpleGrantedAuthority("ALL_PERMISSIONS"));
                authorities.add(new SimpleGrantedAuthority("CERTIFICATE_GENERATE"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CREATE"));
                authorities.add(new SimpleGrantedAuthority("BATCH_CREATE"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CREATE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_SCHEDULE_CREATE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_SCHEDULE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_CREATE"));
                authorities.add(new SimpleGrantedAuthority("PAYMENT_RECEIPT_VIEW"));
                // logger.info("🔥 Granted ALL_PERMISSIONS and module authorities to
                // Admin/Instructor: " + email);

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
                authorities.add(new SimpleGrantedAuthority("STUDENT_BATCH_TRANSFER_CREATE"));
                authorities.add(new SimpleGrantedAuthority("STUDENT_BATCH_TRANSFER_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_CREATE"));
                authorities.add(new SimpleGrantedAuthority("WEBINAR_LIST"));
                authorities.add(new SimpleGrantedAuthority("WEBSITE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("WEBSITE_EDIT"));
                authorities.add(new SimpleGrantedAuthority("BATCH_VIEW"));
                authorities.add(new SimpleGrantedAuthority("BATCH_CREATE"));
                authorities.add(new SimpleGrantedAuthority("BATCH_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("BATCH_DELETE"));

                // 🚀 Inject essential EXAM permissions
                authorities.add(new SimpleGrantedAuthority("EXAM_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_CREATE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_DELETE"));
                authorities.add(new SimpleGrantedAuthority("QUESTION_BANK_VIEW"));
                authorities.add(new SimpleGrantedAuthority("QUESTION_BANK_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("CODING_TEST_CASE_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("CODING_TEST_CASE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_QUESTION_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("EXAM_SCHEDULE_VIEW"));
                authorities.add(new SimpleGrantedAuthority("EXAM_SCHEDULE_CREATE"));

                // 🚀 Inject session content permissions
                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_VIEW"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_CREATE"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_UPDATE"));
                authorities.add(new SimpleGrantedAuthority("SESSION_CONTENT_DELETE"));
            }

            CustomUserDetails userDetails = new CustomUserDetails(user, authorities);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities);

            authentication.setDetails(user);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute("authenticatedUser", user);
            
            if (path.contains("/admin/users/")) {
                System.out.println("🔐 AUTH DEBUG: User " + user.getEmail() + " | Path: " + path + " | Authorities: " + authorities);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("Broken pipe") || msg.contains("connection was aborted")
                    || msg.contains("ClientAbortException"))) {
                logger.warn("JWT Filter: Client connection closed prematurely while processing " + path);
            } else {
                logger.error("JWT Filter validation error: " + e.getMessage() + " at " + path, e);
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            TenantContext.clear();
        }
    }
}