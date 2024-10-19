package org.home;

import org.home.console.ConsoleApp;
import org.home.config.LiquibaseMigrator;

public class Main {

    public static void main(String[] args) {
        LiquibaseMigrator.updateMigrations();
        ConsoleApp.run();
    }
}
