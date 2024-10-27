package org.home;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.home.component.DefaultComponentFactory;
import org.home.config.DBConnectionProvider;
import org.home.config.LiquibaseMigrator;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;

public class StartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dbUrl = sce.getServletContext().getInitParameter("dbUrl");
        String dbUser = sce.getServletContext().getInitParameter("dbUser");
        String dbPassword = sce.getServletContext().getInitParameter("dbPassword");

        DefaultComponentFactory componentFactory = new DefaultComponentFactory();
        DBConnectionProvider connProvider = new DBConnectionProvider(dbUrl, dbUser, dbPassword);

        sce.getServletContext().setAttribute("connProvider", connProvider);

        UserRepository userRepository = componentFactory.createUserRepository(connProvider);
        HabitRepository habitRepository = componentFactory.createHabitRepository(connProvider);
        HabitRecordRepository recordRepository = componentFactory.createHabitRecordRepository(connProvider);
        LiquibaseMigrator.updateMigrations();
    }
}
