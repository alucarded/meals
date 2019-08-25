package com.devpeer.calories.meal;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.core.query.QueryFilterParser;
import com.devpeer.calories.meal.model.Meal;
import com.devpeer.calories.meal.nutritionix.NutritionixService;
import com.devpeer.calories.meal.repository.MealRepository;
import com.devpeer.calories.user.model.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final NutritionixService nutritionixService;

    @Autowired
    public MealService(MealRepository mealRepository,
                       NutritionixService nutritionixService) {
        this.mealRepository = mealRepository;
        this.nutritionixService = nutritionixService;
    }

    public Meal addMeal(UserDetails requestingUser, Meal meal) {
        String mealUserId = meal.getUserId();
        if (null == mealUserId) {
            meal.setUserId(requestingUser.getUsername());
        } else {
            verifyPermissions(requestingUser, mealUserId);
        }
        meal.setId(UUID.randomUUID().toString());
        if (null == meal.getDate()) {
            // Set current date
            meal.setDate(LocalDate.now());
        }
        if (null == meal.getTime()) {
            // Set current time
            meal.setTime(LocalTime.now());
        }
        if (null == meal.getCalories()) {
            meal.setCalories(nutritionixService.getCaloriesForText(meal.getText()));
        }
        return mealRepository.save(meal);
    }

    public Page<Meal> getMealsForCurrentUser(UserDetails requestingUser, Pageable pageable) {
        // TODO: filtering
        return mealRepository.findAllByUserId(requestingUser.getUsername(), pageable);
    }

    public Page<Meal> getMealsForUser(UserDetails requestingUser, String userId, Pageable pageable) {
        verifyPermissions(requestingUser, userId);
        // TODO: filtering
        return mealRepository.findAllByUserId(userId, pageable);
    }

    public Optional<Meal> getMealById(UserDetails requestingUser, String id) {
        return mealRepository.findByIdAndUserId(id, requestingUser.getUsername());
    }

    public Page<Meal> getMeals(UserDetails requestingUser, String filter, int page, int size) {
        if (null == filter) {
            return getMeals(requestingUser, page, size);
        }
        QueryFilter queryFilter = QueryFilterParser.parse(filter, Meal.class);
        verifyQueryFilter(requestingUser, queryFilter);
        return mealRepository.findAll(queryFilter, PageRequest.of(page, size));
    }

    private Page<Meal> getMeals(UserDetails requestingUser, int page, int size) {
        if (requestingUser.getAuthorities().contains(Authority.ADMIN)) {
            return mealRepository.findAll(PageRequest.of(page, size));
        } else if (requestingUser.getAuthorities().contains(Authority.USER)) {
            return mealRepository.findAllByUserId(requestingUser.getUsername(), PageRequest.of(page, size));
        } else {
            throw noAuthority();
        }
    }

    public Meal updateMeal(UserDetails requestingUser, Meal meal) {
        verifyPermissions(requestingUser, meal);
        return mealRepository.update(meal);
    }

    // TODO: does it throw if object does not exist?
    public void deleteMeal(UserDetails requestingUser, String id) {
        mealRepository.deleteByIdAndUserId(id, requestingUser.getUsername());
    }

    private void verifyPermissions(UserDetails requestingUser, String userId) {
        if (requestingUser.getAuthorities().contains(Authority.ADMIN)) {
            // noop
        } else if (requestingUser.getAuthorities().contains(Authority.USER)) {
            if (!requestingUser.getUsername().equals(userId)) {
                throw new AccessDeniedException(
                        String.format("Only allowed to CRUD owned records. Please set userId to %s or do not send it",
                                requestingUser.getUsername()));
            }
        } else {
            throw noAuthority();
        }
    }

    private void verifyPermissions(UserDetails requestingUser, Meal meal) {
        verifyPermissions(requestingUser, meal.getUserId());
    }

    private void verifyQueryFilter(UserDetails requestingUser, QueryFilter queryFilter) {
        // TODO
    }

    private AccessDeniedException noAuthority() {
        return new AccessDeniedException("No permissions to CRUD meals");
    }
}
