package com.lms.www.tenant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TenantSubdomainResolver {

    private final JdbcTemplate jdbcTemplate;

    public TenantSubdomainResolver(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String resolveTenantDbFromRequest(HttpServletRequest request) {

        String host = request.getServerName(); // john.localhost

        // 🏠 LOCAL DEVELOPMENT OVERRIDE
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            return "lms_tenant_1770701101086";
        }

        if (host == null || !host.contains(".")) return null;

        String subdomain = host.split("\\.")[0].toLowerCase();

        try {
            return jdbcTemplate.queryForObject(
                "SELECT tenant_db_name FROM tenant_registry WHERE tenant_domain = ?",
                String.class,
                subdomain
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
