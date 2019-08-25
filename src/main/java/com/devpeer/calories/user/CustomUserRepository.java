package com.devpeer.calories.user;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.meal.model.Meal;
import com.devpeer.calories.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {
    Page<User> findAll(QueryFilter queryFilter, Pageable pageable);
}
