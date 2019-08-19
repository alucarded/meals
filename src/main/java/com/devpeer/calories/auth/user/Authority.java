package com.devpeer.calories.auth.user;

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
