package org.home.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * Configuration class for setting up Liquibase for database migrations.
 */
@Configuration
@PropertySource("classpath:application.yml")
public class LiquibaseConfig {

    @Value("${spring.liquibase.enabled}")
    private String isLiquibaseEnabledStr;

    @Value("${spring.liquibase.change-log}")
    private String changeLog;

    /**
     * Creates a SpringLiquibase bean configured with the provided DataSource.
     *
     * @param dataSource the DataSource to be used by Liquibase for database connections
     * @return a SpringLiquibase instance configured for database migrations
     */
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        boolean isLiquibaseEnabled = Boolean.parseBoolean(isLiquibaseEnabledStr);
        if (!isLiquibaseEnabled) {
            System.out.println("Liquibase is disabled. Skipping migrations.");
            return null;
        }

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        return liquibase;
    }
}
