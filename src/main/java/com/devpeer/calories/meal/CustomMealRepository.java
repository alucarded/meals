package com.devpeer.calories.meal;

import com.devpeer.calories.core.QueryFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMealRepository {
    Meal update(Meal meal);

    Page<Meal> findAll(QueryFilter queryFilter, Pageable pageable);
}
