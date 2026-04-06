package com.lms.www.marketing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
public class SchemaFixTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void fixSchema() {
        System.out.println("--- STARTING SCHEMA FIX ---");
        
        try {
            System.out.println("Adding adBudget to tracked_links...");
            jdbcTemplate.execute("ALTER TABLE tracked_links ADD COLUMN adBudget DECIMAL(19, 2)");
            System.out.println("Success!");
        } catch (Exception e) {
            System.out.println("tracked_links update skipped: " + e.getMessage());
        }

        try {
            System.out.println("Adding adBudget to landing_pages...");
            jdbcTemplate.execute("ALTER TABLE landing_pages ADD COLUMN adBudget DECIMAL(19, 2)");
            System.out.println("Success!");
        } catch (Exception e) {
            System.out.println("landing_pages update skipped: " + e.getMessage());
        }

        System.out.println("--- SCHEMA FIX FINISHED ---");
    }
}
