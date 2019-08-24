package com.devpeer.calories.meal.nutritionix.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NutritionsResponse {
    private List<Food> foods;
}
