package org.home.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private final Properties properties = new Properties();

    public ConfigLoader() {
        try (FileInputStream input = new FileInputStream("src/main/resources/application.yml")) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Failed to load properties from config file." + e.getMessage());
        }
    }

    public String getDbUrl() {
        String dbUrl = properties.getProperty("url");
        return dbUrl.substring(1, dbUrl.length() - 1);
    }

    public String getDbUsername() {
        return properties.getProperty("username");
    }

    public String getDbPassword() {
        return properties.getProperty("password");
    }

    public String getLiquibaseChangeLog() {
        return properties.getProperty("change-log").substring(10);
    }
}
