package org.home.model;

import java.sql.Timestamp;

/**
 * The {@code UserAuditLog} class represents a log record of a user action.
 */
public class UserAuditLog {
    private Long id;
    private String email;
    private String action;
    private String result;
    private Timestamp timestamp;

    /**
     * Constructs a new {@code UserAuditLog} with the specified parameters.
     *
     * @param email         the email address of the user
     * @param action        the action that user has performed
     * @param result        the result of the action
     * @param timestamp     the timestamp of the action performed
     */
    public UserAuditLog(String email, String action, String result, Timestamp timestamp) {
        this.email = email;
        this.action = action;
        this.result = result;
        this.timestamp = timestamp;
    }

    public UserAuditLog() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UserAuditLog{" +
                "email='" + email + '\'' +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
