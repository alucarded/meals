package com.devpeer.calories.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/v1/meals")
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
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
            return ResponseEntity.created(URI.create("/vi/meals/" + addedMeal.getId())).body(addedMeal);
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
     * @param userDetails
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity getMeals(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam("filter") String filter,
                                   @RequestParam("page") int page,
                                   @RequestParam("size") int size) {
        try {
            return ResponseEntity.ok(mealService.getMeals(userDetails, filter, page, size));
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
            return ResponseEntity.ok(mealService.updateMeal(userDetails, meal));
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