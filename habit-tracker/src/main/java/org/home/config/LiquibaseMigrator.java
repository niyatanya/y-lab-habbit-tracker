package org.home.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class LiquibaseMigrator {

    private static ConfigLoader configLoader = new ConfigLoader();

    public static void updateMigrations() {
        String dbUrl = configLoader.getDbUrl();
        String username = configLoader.getDbUsername();
        String password = configLoader.getDbPassword();
        String changeLogFile = configLoader.getLiquibaseChangeLog();

        try {
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase =
                    new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
            System.out.println("Migration is completed successfully");
        } catch (SQLException | LiquibaseException e) {
            System.out.println("SQL Exception in migration " + e.getMessage());
        }
    }
}
