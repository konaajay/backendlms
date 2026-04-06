package com.lms.www.service.Impl;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.model.User;
import com.lms.www.repository.UserRepository;
import com.lms.www.service.AccountUnlockService;
import com.lms.www.service.EmailService;
import com.lms.www.tenant.TenantContext;
import com.lms.www.tenant.TenantRoutingDataSource;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class AccountUnlockServiceImpl implements AccountUnlockService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final DataSource tenantRoutingDataSource;

    public AccountUnlockServiceImpl(
            JdbcTemplate jdbcTemplate,
            UserRepository userRepository,
            EmailService emailService,
            DataSource tenantRoutingDataSource
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.tenantRoutingDataSource = tenantRoutingDataSource;
    }

    @Override
    public void requestUnlock(String email, HttpServletRequest request) {

        // ================================
        // 1️⃣ RESOLVE TENANT (MASTER DB)
        // ================================
        String host = request.getServerName();
        String tenantDb = null;
        String superAdminEmail = null;
        String tenantDomain = null;

        // 🏠 LOCAL DEVELOPMENT OVERRIDE
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            tenantDb = "lms_tenant_1770701101086";
            tenantDomain = "localhost";
            try {
                superAdminEmail = jdbcTemplate.queryForObject(
                        "SELECT super_admin_email FROM tenant_registry WHERE tenant_db_name = ?",
                        String.class,
                        tenantDb
                );
            } catch (Exception e) {
                superAdminEmail = "lmssender4@gmail.com"; // Fallback to system sender for local dev
            }
        } else {
            if (host == null || !host.contains(".")) {
                throw new RuntimeException("Invalid tenant domain: " + host);
            }

            tenantDomain = host.split("\\.")[0].toLowerCase();

            try {
                tenantDb = jdbcTemplate.queryForObject(
                        "SELECT tenant_db_name FROM tenant_registry WHERE tenant_domain = ?",
                        String.class,
                        tenantDomain
                );

                superAdminEmail = jdbcTemplate.queryForObject(
                        "SELECT super_admin_email FROM tenant_registry WHERE tenant_domain = ?",
                        String.class,
                        tenantDomain
                );
            } catch (EmptyResultDataAccessException e) {
                throw new RuntimeException("Tenant domain '" + tenantDomain + "' not registered");
            }
        }

        // ================================
        // 2️⃣ REGISTER TENANT DATASOURCE
        // ================================
        TenantRoutingDataSource routing =
                (TenantRoutingDataSource)
                        ((LazyConnectionDataSourceProxy) tenantRoutingDataSource)
                                .getTargetDataSource();

        routing.addTenant(tenantDb);

        // ================================
        // 3️⃣ SWITCH TO TENANT DB
        // ================================
        TenantContext.setTenant(tenantDb);

        try {
            // ================================
            // 4️⃣ TENANT DB OPERATION (SAFE)
            // ================================
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ================================
            // 5️⃣ EMAIL SUPER ADMIN
            // ================================
            emailService.sendAccountUnlockRequestToSuperAdmin(
                    superAdminEmail,
                    user.getEmail(),
                    user.getRoleName()
            );

        } finally {
            // ================================
            // 6️⃣ ALWAYS CLEAR CONTEXT
            // ================================
            TenantContext.clear();
        }
    }
}
