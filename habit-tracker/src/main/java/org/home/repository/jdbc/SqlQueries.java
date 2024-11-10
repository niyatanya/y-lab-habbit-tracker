package org.home.repository.jdbc;

public final class SqlQueries {

    public static final String SELECT_ALL_USERS = "SELECT * FROM ylab_schema.users";
    public static final String INSERT_USER =
            "INSERT INTO ylab_schema.users (name, email, password, role) VALUES (?, ?, ?, ?::ROLE)";
    public static final String SELECT_USER = "SELECT * FROM ylab_schema.users WHERE email = ?";
    public static final String UPDATE_USER =
            "UPDATE ylab_schema.users SET name = ?, email = ?, password = ?, is_blocked = ? WHERE id = ?";
    public static final String DELETE_USER = "DELETE FROM ylab_schema.users WHERE email = ?";

    public static final String SELECT_ALL_HABITS = "SELECT * FROM ylab_schema.habits WHERE user_id = ?";
    public static final String INSERT_HABIT = "INSERT INTO ylab_schema.habits (title, description, frequency, user_id)"
            + " VALUES (?, ?, ?::FREQUENCY, ?)";
    public static final String SELECT_HABIT = "SELECT * FROM ylab_schema.habits WHERE user_id = ? AND title = ?";
    public static final String UPDATE_HABIT = "UPDATE ylab_schema.habits SET title = ?, description = ?, "
        + "frequency = ?::FREQUENCY WHERE id = ?";
    public static final String DELETE_HABIT = "DELETE FROM ylab_schema.habits WHERE id = ?";

    public static final String SELECT_ALL_RECORDS = "SELECT * FROM ylab_schema.records WHERE habit_id = ?";
    public static final String INSERT_RECORD = "INSERT INTO ylab_schema.records (date, completed, habit_id) VALUES"
            + " (?, ?, ?)";
    public static final String SELECT_RECORD = "SELECT * FROM ylab_schema.records WHERE habit_id = ? AND date = ?";
    public static final String UPDATE_RECORD = "UPDATE ylab_schema.records SET completed = ? WHERE id = ?";
    public static final String DELETE_RECORD = "DELETE FROM ylab_schema.records WHERE id = ?";

    public static final String INSERT_AUDIT_LOG =
            "INSERT INTO ylab_schema.audit_log (email, action, result, timestamp) VALUES (?, ?, ?, ?)";
    public static final String SELECT_ALL_AUDIT_LOGS =
            "SELECT email, action, result, timestamp FROM ylab_schema.audit_log ORDER BY timestamp DESC";

    private SqlQueries() {
    }
}
