package org.home.model;

import lombok.Getter;

@Getter
public class Role {
    private final String roleName;

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.roleName);
    }
}
