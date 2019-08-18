package com.devpeer.calories.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Encapsulates mapping internal User object to external model.
 */
public class UserMapper {
    public static Map<Object, Object> from(UserDetails user) {
        Map<Object, Object> model = new HashMap<>();
        model.put("username", user.getUsername());
        model.put("roles", user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );
        return model;
    }
}
