package org.home;

import lombok.AllArgsConstructor;
import org.home.component.ComponentFactory;
import org.home.component.DefaultComponentFactory;
import org.home.config.DBConnectionProvider;
import org.home.config.LiquibaseMigrator;
import org.home.console.ConsoleApp;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;

/**
 * The {@code Main} class is the entry point for the application.
 * It initializes the necessary components and starts the console application.
 */
@AllArgsConstructor
public class Main {
    private ComponentFactory componentFactory;

    /**
     * Runs the application, initializing the necessary components.
     */
    public void run() {
        DBConnectionProvider connProvider = componentFactory.prepareConnector();
        UserRepository userRepository = componentFactory.createUserRepository(connProvider);
        HabitRepository habitRepository = componentFactory.createHabitRepository(connProvider);
        HabitRecordRepository recordRepository = componentFactory.createHabitRecordRepository(connProvider);
        LiquibaseMigrator.updateMigrations();
        ConsoleApp.run();
    }

    /**
     * The main method serves as the entry point for the application.
     */
    public static void main(String[] args) {
        ComponentFactory factory = new DefaultComponentFactory();
        Main app = new Main(factory);
        app.run();
    }
}
