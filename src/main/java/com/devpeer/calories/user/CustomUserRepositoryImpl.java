package com.devpeer.calories.user;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class CustomUserRepositoryImpl implements CustomUserRepository {
    @Override
    public Page<User> findAll(QueryFilter queryFilter, Pageable pageable) {
        return null;
    }
}
