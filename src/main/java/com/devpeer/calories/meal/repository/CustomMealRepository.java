package com.devpeer.calories.meal.repository;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.meal.model.CaloriesForDay;
import com.devpeer.calories.meal.model.Meal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomMealRepository {
    Meal update(Meal meal);

    List<Meal> findAllWithTotalCalories();

    Page<Meal> findAll(QueryFilter queryFilter, Pageable pageable);
}
