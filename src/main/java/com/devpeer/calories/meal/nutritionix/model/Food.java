package com.devpeer.calories.meal.nutritionix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Food {
    @JsonProperty("food_name")
    private String foodName;
    @JsonProperty("nf_calories")
    private Float calories;
}
