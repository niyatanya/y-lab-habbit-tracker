package org.home.model;

import java.sql.Timestamp;

public class UserAuditLog {
    private Long id;
    private String email;
    private String action;
    private String result;
    private Timestamp timestamp;

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
