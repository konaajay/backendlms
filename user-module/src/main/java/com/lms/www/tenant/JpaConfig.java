package com.lms.www.tenant;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource tenantRoutingDataSource,
            org.springframework.core.env.Environment env
    ) {
        LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(tenantRoutingDataSource);
        emf.setPackagesToScan("com.lms.www");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        java.util.Map<String, Object> properties = new java.util.HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto", "update"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform", "org.hibernate.dialect.MySQLDialect"));
        properties.put("hibernate.physical_naming_strategy", env.getProperty("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"));
        properties.put("hibernate.show_sql", env.getProperty("spring.jpa.show-sql", "false"));
        properties.put("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql", "false"));
        emf.setJpaPropertyMap(properties);

        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(
            EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
