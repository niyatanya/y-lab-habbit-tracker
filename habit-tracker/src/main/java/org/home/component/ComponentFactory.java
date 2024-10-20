package org.home.component;

import org.home.config.DBConnectionProvider;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;

/**
 * The {@code ComponentFactory} interface is used to create important components
 * like database connections and repositories.
 * <p>
 * It provides methods to get a database connection and to create repositories for users, habits, and habit records.
 */
public interface ComponentFactory {
    /**
     * Creates a database connection provider that will help connect to the database.
     *
     * @return a {@link DBConnectionProvider} to manage the database connection
     */
    DBConnectionProvider prepareConnector();

    /**
     * Creates a {@link UserRepository} to manage user-related data in the database.
     *
     * @param connProvider the {@link DBConnectionProvider} used to connect to the database
     * @return a {@link UserRepository} to interact with user data
     */
    UserRepository createUserRepository(DBConnectionProvider connProvider);

    /**
     * Creates a {@link HabitRepository} to manage habit-related data in the database.
     *
     * @param connProvider the {@link DBConnectionProvider} used to connect to the database
     * @return a {@link HabitRepository} to interact with habit data
     */
    HabitRepository createHabitRepository(DBConnectionProvider connProvider);

    /**
     * Creates a {@link HabitRecordRepository} to manage habit record data in the database.
     *
     * @param connProvider the {@link DBConnectionProvider} used to connect to the database
     * @return a {@link HabitRecordRepository} to interact with habit record data
     */
    HabitRecordRepository createHabitRecordRepository(DBConnectionProvider connProvider);
}
