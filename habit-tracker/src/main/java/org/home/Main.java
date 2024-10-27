package org.home;

import lombok.AllArgsConstructor;
import org.home.component.ComponentFactory;
import org.home.component.DefaultComponentFactory;
import org.home.config.DBConnectionProvider;
import org.home.config.LiquibaseMigrator;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;

/**
 * The {@code Main} class is the entry point for the application.
 * It initializes the necessary components and performs database migrations.
 */
@AllArgsConstructor
public class Main {
    private ComponentFactory componentFactory;

    /**
     * Initializes the application by preparing the database connection and creating
     * the required repositories, and executes database migrations.
     */
    public void initialize() {
        DBConnectionProvider connProvider = componentFactory.prepareConnector();
        UserRepository userRepository = componentFactory.createUserRepository(connProvider);
        HabitRepository habitRepository = componentFactory.createHabitRepository(connProvider);
        HabitRecordRepository recordRepository = componentFactory.createHabitRecordRepository(connProvider);
        LiquibaseMigrator.updateMigrations();
    }

    /**
     * The main method serves as the entry point for the application.
     */
    public static void main(String[] args) {
        ComponentFactory factory = new DefaultComponentFactory();
        Main app = new Main(factory);
        app.initialize();
    }
}
