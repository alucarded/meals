package com.devpeer.calories.meal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MealRepository extends MongoRepository<Meal, String>, CustomMealRepository {
    Page<Meal> findAllByUserId(String userId, Pageable pageable);
}
