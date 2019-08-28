package com.devpeer.calories.meal;

import com.devpeer.calories.core.query.QueryFilter;
import com.devpeer.calories.core.query.QueryFilterParser;
import com.devpeer.calories.meal.model.Meal;
import com.devpeer.calories.meal.nutritionix.NutritionixService;
import com.devpeer.calories.meal.repository.MealRepository;
import com.devpeer.calories.user.model.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Optional<Meal> getMealById(UserDetails requestingUser, String id) {
        return mealRepository.findByIdAndUserId(id, requestingUser.getUsername());
    }

    public Page<Meal> getMeals(UserDetails requestingUser, String filter, Pageable pageable) {
        if (null == filter) {
            return getMeals(requestingUser, pageable);
        }
        // TODO: use filter as SPEL expression in mongo projection...
        QueryFilter queryFilter = QueryFilterParser.parse(filter, Meal.class);
        if (!requestingUser.getAuthorities().contains(Authority.ADMIN)) {
            // Make sure regular user can get only his entries
            verifyQueryFilter(queryFilter);
            QueryFilter currentUserFilter = QueryFilter.builder()
                    .key(Meal.USER_ID_FIELD_NAME)
                    .value(requestingUser.getUsername())
                    .operator(QueryFilter.Operator.EQ)
                    .build();
            queryFilter = QueryFilter.builder()
                    .operator(QueryFilter.Operator.AND)
                    .chainOperations(Arrays.asList(queryFilter, currentUserFilter))
                    .build();
        }
        return mealRepository.findAllWithAggregations(queryFilter, pageable);
    }

    private Page<Meal> getMeals(UserDetails requestingUser, Pageable pageable) {
        if (requestingUser.getAuthorities().contains(Authority.ADMIN)) {
            return mealRepository.findAll(pageable);
        } else if (requestingUser.getAuthorities().contains(Authority.USER)) {
            return mealRepository.findAllByUserId(requestingUser.getUsername(), pageable);
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

    private void verifyQueryFilter(QueryFilter queryFilter) {
        if (Meal.USER_ID_FIELD_NAME.equals(queryFilter.getKey())) {
            throw new AccessDeniedException("No permission to filter meals by userId");
        }
        if (!queryFilter.getChainOperations().isEmpty()) {
            queryFilter.getChainOperations().forEach(this::verifyQueryFilter);
        }
    }

    private AccessDeniedException noAuthority() {
        return new AccessDeniedException("No permissions to CRUD meals");
    }
}
