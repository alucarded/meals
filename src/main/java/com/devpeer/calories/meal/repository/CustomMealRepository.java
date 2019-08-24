package com.devpeer.calories.meal.repository;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.meal.model.Meal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMealRepository {
    Meal update(Meal meal);

    Page<Meal> findAll(QueryFilter queryFilter, Pageable pageable);
}
