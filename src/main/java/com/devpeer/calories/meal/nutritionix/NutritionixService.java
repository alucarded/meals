package com.devpeer.calories.meal.nutritionix;

import com.devpeer.calories.meal.nutritionix.model.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
public class NutritionixService {

    private final NutritionixClient nutritionixClient;

    @Autowired
    public NutritionixService(NutritionixClient nutritionixClient) {
        this.nutritionixClient = nutritionixClient;
    }

    public Integer getCaloriesForText(String text) {
        return nutritionixClient.getFoods(text).stream()
                .map(Food::getCalories)
                .reduce(0.f, Float::sum)
                .intValue();
    }
}
