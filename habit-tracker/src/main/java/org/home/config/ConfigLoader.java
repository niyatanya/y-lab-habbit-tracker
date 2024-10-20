package org.home.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The {@code ConfigLoader} class is responsible for loading configuration properties
 * from a file and providing access to the database connection details and other settings.
 */
public class ConfigLoader {

    private final Properties properties = new Properties();

    /**
     * Loads the configuration file and stores the properties in memory.
     */
    public ConfigLoader() {
        try (FileInputStream input = new FileInputStream("src/main/resources/application.yml")) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Failed to load properties from config file." + e.getMessage());
        }
    }

    /**
     * Retrieves the database URL from the properties file.
     *
     * @return the database URL
     */
    public String getDbUrl() {
        String dbUrl = properties.getProperty("url");
        return dbUrl.substring(1, dbUrl.length() - 1);
    }

    /**
     * Retrieves the database username from the properties file.
     *
     * @return the database username
     */
    public String getDbUsername() {
        return properties.getProperty("username");
    }

    /**
     * Retrieves the database password from the properties file.
     *
     * @return the database password
     */
    public String getDbPassword() {
        return properties.getProperty("password");
    }

    /**
     * Retrieves the Liquibase changelog file location from the properties file.
     *
     * @return the changelog file location
     */
    public String getLiquibaseChangeLog() {
        return properties.getProperty("change-log").substring(10);
    }
}
