package com.lastcalleats.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Loads demo data on dev startup. Skips when data already exists so normal
 * restarts don't wipe local state; set app.seed.force=true to re-seed.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.seed.force:false}")
    private boolean force;

    @Override
    public void run(String... args) {
        Long userCount = jdbcTemplate.queryForObject("SELECT count(*) FROM \"user\"", Long.class);
        if (userCount != null && userCount > 0 && !force) {
            log.info("Seed skipped: {} users already present (app.seed.force=true to re-seed)", userCount);
            return;
        }
        new ResourceDatabasePopulator(new ClassPathResource("db/seed/dev-seed.sql")).execute(dataSource);
        log.info("Dev seed data loaded");
    }
}
