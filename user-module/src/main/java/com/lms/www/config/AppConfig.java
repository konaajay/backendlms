package com.lms.www.config;

import com.lms.www.tenant.TenantRoutingDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/themes/**")
                .addResourceLocations("file:uploads/themes/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
// @Configuration
// public class AppConfig implements WebMvcConfigurer {

// @Value("${spring.datasource.url}")
// private String url;

// @Value("${spring.datasource.username}")
// private String user;

// @Value("${spring.datasource.password}")
// private String pass;

// // =========================
// // DATA SOURCE
// // =========================

// // @Bean
// // public DataSource masterDataSource() {
// // HikariDataSource ds = new HikariDataSource();
// // ds.setJdbcUrl(url);
// // ds.setUsername(user);
// // ds.setPassword(pass);
// // return ds;
// // }

// @Bean
// public JdbcTemplate jdbcTemplate(DataSource masterDataSource) {
// return new JdbcTemplate(masterDataSource);
// }

// @Bean
// @Primary
// public DataSource tenantRoutingDataSource(DataSource masterDataSource) {
// TenantRoutingDataSource routing = new TenantRoutingDataSource();
// routing.init(masterDataSource, url, user, pass);
// return new LazyConnectionDataSourceProxy(routing);
// }

// // =========================
// // COMMON BEANS
// // =========================

// @Bean
// public RestTemplate restTemplate() {
// return new RestTemplate();
// }

// // =========================
// // STATIC FILES
// // =========================

// @Override
// public void addResourceHandlers(ResourceHandlerRegistry registry) {
// registry.addResourceHandler("/themes/**")
// .addResourceLocations("file:uploads/themes/");
// registry.addResourceHandler("/uploads/**")
// .addResourceLocations("file:uploads/");
// }
// }