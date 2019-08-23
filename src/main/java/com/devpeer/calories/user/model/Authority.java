package com.devpeer.calories.user.model;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    ADMIN,
    MANAGER,
    USER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
