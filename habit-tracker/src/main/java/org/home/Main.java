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

@AllArgsConstructor
public class Main {
    private ComponentFactory componentFactory;

    public void run() {
        DBConnectionProvider connProvider = componentFactory.prepareConnector();
        UserRepository userRepository = componentFactory.createUserRepository(connProvider);
        HabitRepository habitRepository = componentFactory.createHabitRepository(connProvider);
        HabitRecordRepository recordRepository = componentFactory.createHabitRecordRepository(connProvider);
        LiquibaseMigrator.updateMigrations();
        ConsoleApp.run();
    }

    public static void main(String[] args) {
        ComponentFactory factory = new DefaultComponentFactory();
        Main app = new Main(factory);
        app.run();
    }
}
