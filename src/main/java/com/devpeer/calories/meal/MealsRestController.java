package com.devpeer.calories.meal;

import com.devpeer.calories.meal.model.Meal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/v1/meals")
public class MealsRestController {

    private final MealService mealService;

    @Autowired
    public MealsRestController(MealService mealService) {
        this.mealService = mealService;
    }

    /**
     * Add meal for current user.
     *
     * @param userDetails
     * @param meal
     * @return
     */
    @PostMapping
    public ResponseEntity addMeal(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody Meal meal) {
        try {
            Meal addedMeal = mealService.addMeal(userDetails, meal);
            // TODO: we could respond with full URI in location header?
            return ResponseEntity.created(URI.create("/v1/meals/" + addedMeal.getId())).body(addedMeal);
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
            // TODO: better specialize exception (specific to Meal service domain logic), instead of using the generic client one
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find number of calories for given text," +
                    "please provide number of calories or change text", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getMealById(@AuthenticationPrincipal UserDetails userDetails,
                                      @PathVariable("id") String id) {
        try {
            return ResponseEntity.of(mealService.getMealById(userDetails, id));
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    /**
     * Get meals. Regular user can only get his meals.
     * // TODO: add filtering, filtering by userId only allowed for admin, for regular user - always set his ID...
     * // TODO: ...or return bad request if he tries to filter by user ID
     *
     * @param userDetails
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity getMeals(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(value = "filter", required = false) String filter,
                                   @PageableDefault(value = Integer.MAX_VALUE) Pageable pageable) {
        try {
            return ResponseEntity.ok(mealService.getMeals(userDetails, filter, pageable));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    /**
     * Update a meal by given ID. Regular user can only update his meals.
     *
     * @param userDetails
     * @param meal
     * @return
     */
    @PutMapping
    public ResponseEntity updateMeal(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestBody Meal meal) {
        try {
            return ResponseEntity.ok(mealService.replaceMeal(userDetails, meal));
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    /**
     * Delete meal by ID. Regular user can only delete his meals.
     *
     * @param userDetails
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteMealById(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable("id") String id) {
        try {
            mealService.deleteMeal(userDetails, id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }
}
