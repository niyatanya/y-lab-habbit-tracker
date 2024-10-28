package org.home;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
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
        String changeLogFile = sce.getServletContext().getInitParameter("changeLogFile");

        DBConnectionProvider connProvider = new DBConnectionProvider(dbUrl, dbUser, dbPassword);
        sce.getServletContext().setAttribute(changeLogFile, connProvider);

        UserRepository userRepository = new UserRepository(connProvider);
        HabitRepository habitRepository = new HabitRepository(connProvider);
        HabitRecordRepository recordRepository = new HabitRecordRepository(connProvider);
        LiquibaseMigrator.updateMigrations(dbUrl, dbUser, dbPassword, changeLogFile);
    }
}
