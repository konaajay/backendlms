package com.lms.www.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.lms.www.model.SystemSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import com.lms.www.tenant.TenantRoutingDataSource;
import com.lms.www.model.User;
import com.lms.www.tenant.TenantContext;
import com.lms.www.repository.UserRepository;
import com.lms.www.repository.SystemSettingsRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final SystemSettingsRepository systemSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource tenantRoutingDataSource;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public AdminInitializer(JdbcTemplate jdbcTemplate, UserRepository userRepository,
            SystemSettingsRepository systemSettingsRepository, PasswordEncoder passwordEncoder,
            @Qualifier("tenantRoutingDataSource") DataSource tenantRoutingDataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.systemSettingsRepository = systemSettingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantRoutingDataSource = tenantRoutingDataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        String defaultTenant = "lms_tenant_1770701101086";
        String adminEmail = "admin@admin.com";
        String adminPassword = "password";

        try {
            // Check if tenant exists
            Integer dbCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?",
                    Integer.class, defaultTenant);

            if (dbCount == null || dbCount == 0) {
                System.out.println("Creating default development tenant database: " + defaultTenant);
                jdbcTemplate.execute("CREATE DATABASE " + defaultTenant
                        + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

                String masterUrl = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
                String tenantUrl = masterUrl.replace("/master_db", "/" + defaultTenant);

                try (Connection tenantConn = DriverManager.getConnection(tenantUrl, dbUser, dbPassword)) {
                    ScriptUtils.executeSqlScript(tenantConn, new ClassPathResource("db/tenant_template.sql"));
                }
                System.out.println("Tenant schema initialized.");
            }

            // Register tenant in master if not exists
            Integer registryCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM tenant_registry WHERE tenant_db_name = ?",
                    Integer.class, defaultTenant);

            if (registryCount == null || registryCount == 0) {
                jdbcTemplate.update(
                        "INSERT INTO tenant_registry (super_admin_email, tenant_db_name, tenant_domain, enabled) VALUES (?,?,?,?)",
                        adminEmail, defaultTenant, "localhost", true);
            }

            // Add tenant to routing data source
            if (tenantRoutingDataSource instanceof LazyConnectionDataSourceProxy proxy) {
                if (proxy.getTargetDataSource() instanceof TenantRoutingDataSource routing) {
                    routing.addTenant(defaultTenant);
                }
            } else if (tenantRoutingDataSource instanceof TenantRoutingDataSource routing) {
                routing.addTenant(defaultTenant);
            }

            // Switch to tenant context to create admin
            TenantContext.setTenant(defaultTenant);
            
            // Wait, we need to make sure the tenant routing data source knows about this tenant
            // If it's already running, it might not be registered in the map.
            // But for local dev, let's just insert it and it will try to resolve it.

            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setFirstName("Local");
                admin.setLastName("Admin");
                admin.setPhone("1234567890");
                admin.setEnabled(true);
                admin.setRoleName("ROLE_SUPER_ADMIN");
                admin = userRepository.save(admin);

                SystemSettings settings = new SystemSettings();
                settings.setUserId(admin.getUserId());
                settings.setMaxLoginAttempts(3L);
                settings.setAccLockDuration(30L);
                settings.setPassExpiryDays(60L);
                settings.setPassLength(10L);
                settings.setJwtExpiryMins(60L);
                settings.setSessionTimeout(360L);
                settings.setMultiSession(false);
                settings.setPasswordLastUpdatedAt(LocalDateTime.now());
                settings.setUpdatedTime(LocalDateTime.now());
                systemSettingsRepository.save(settings);

                System.out.println("=========================================================");
                System.out.println("Default Admin Credentials Created!");
                System.out.println("Email: " + adminEmail);
                System.out.println("Password: " + adminPassword);
                System.out.println("=========================================================");
            }
        } catch (Exception e) {
            System.err.println("Could not initialize default admin: " + e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }
}
