package org.home.component;

import org.home.config.ConfigLoader;
import org.home.config.DBConnectionProvider;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;

public class DefaultComponentFactory implements ComponentFactory {

    private ConfigLoader configLoader;

    public DefaultComponentFactory() {
        this.configLoader = new ConfigLoader(); // Загрузка конфигураций
    }

    @Override
    public DBConnectionProvider prepareConnector() {
        String dbUrl = configLoader.getDbUrl();
        String username = configLoader.getDbUsername();
        String password = configLoader.getDbPassword();
        return new DBConnectionProvider(dbUrl, username, password);
    }

    @Override
    public UserRepository createUserRepository(DBConnectionProvider connProvider) {
        return new UserRepository(connProvider);
    }

    @Override
    public HabitRepository createHabitRepository(DBConnectionProvider connProvider) {
        return new HabitRepository(connProvider);
    }

    @Override
    public HabitRecordRepository createHabitRecordRepository(DBConnectionProvider connProvider) {
        return new HabitRecordRepository(connProvider);
    }
}
