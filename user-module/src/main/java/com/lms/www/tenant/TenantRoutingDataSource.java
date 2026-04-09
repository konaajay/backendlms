package com.lms.www.tenant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.zaxxer.hikari.HikariDataSource;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log =
            LoggerFactory.getLogger(TenantRoutingDataSource.class);

    // 🔥 DYNAMIC TENANT DATASOURCES
    private final Map<Object, Object> tenantDataSources =
            new ConcurrentHashMap<>();

    private DataSource masterDataSource;
    private String jdbcUrl;
    private String username;
    private String password;

    public void init(
            DataSource master,
            String jdbcUrl,
            String username,
            String password
    ) {
        this.masterDataSource = master;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;

        tenantDataSources.put("MASTER", master);
        super.setTargetDataSources(tenantDataSources);
        super.setDefaultTargetDataSource(master);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getTenant();
        return (tenant == null) ? "MASTER" : tenant;
    }

    // 🔥 CALLED ON DEMAND
    public void addTenant(String tenantDb) {
        if (tenantDataSources.containsKey(tenantDb)) {
            return;
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl.replace("/master_db", "/" + tenantDb));
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaximumPoolSize(2); // 🔥 LIMIT CONCURRENT CONNECTIONS PER TENANT
        ds.setPoolName("HikariPool-" + tenantDb); // 🔥 Better debugging
        ds.setIdleTimeout(30000); // 30 seconds
        ds.setMinimumIdle(0); // Allow scale down to 0
        ds.setConnectionTimeout(20000); // 20s timeout

        tenantDataSources.put(tenantDb, ds);
        super.afterPropertiesSet();

        log.info("Tenant datasource registered: {}", tenantDb);
    }
}
