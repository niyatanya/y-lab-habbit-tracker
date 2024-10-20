package org.home.component;

import org.home.config.DBConnectionProvider;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;

public interface ComponentFactory {
    DBConnectionProvider prepareConnector();
    UserRepository createUserRepository(DBConnectionProvider connProvider);
    HabitRepository createHabitRepository(DBConnectionProvider connProvider);
    HabitRecordRepository createHabitRecordRepository(DBConnectionProvider connProvider);
}
