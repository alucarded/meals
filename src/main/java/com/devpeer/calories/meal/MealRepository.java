package com.devpeer.calories.meal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MealRepository extends MongoRepository<Meal, String>, CustomMealRepository {
    Page<Meal> findAllByUserId(String userId, Pageable pageable);
    Optional<Meal> findByIdAndUserId(String id, String userId);
    void deleteByIdAndUserId(String id, String userId);
}
