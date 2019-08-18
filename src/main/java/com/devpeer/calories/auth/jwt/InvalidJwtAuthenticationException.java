package com.devpeer.calories.auth.jwt;

import org.springframework.security.core.AuthenticationException;

class InvalidJwtAuthenticationException extends AuthenticationException {
    InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
