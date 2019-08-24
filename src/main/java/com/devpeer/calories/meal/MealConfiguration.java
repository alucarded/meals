package com.devpeer.calories.meal;

import com.devpeer.calories.meal.nutritionix.NutritionixClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MealConfiguration {

    @Bean
    public NutritionixClient nutritionixClient() {
        return new NutritionixClient();
    }

}
