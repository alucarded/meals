package com.devpeer.calories.meal.nutritionix;

import com.devpeer.calories.meal.nutritionix.model.Food;
import com.devpeer.calories.meal.nutritionix.model.NutritionsRequest;
import com.devpeer.calories.meal.nutritionix.model.NutritionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Component
public class NutritionixClient {

    private static final String API_VERSION = "v2";
    private static final String APP_ID_HEADER_NAME = "x-app-id";
    private static final String APP_KEY_HEADER_NAME = "x-app-key";

    private final WebClient webClient;

    public NutritionixClient() {
        webClient = WebClient.builder()
                .baseUrl("https://trackapi.nutritionix.com/" + API_VERSION)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // TODO: load keys from some config
                .defaultHeader(APP_ID_HEADER_NAME, "72c10c37")
                .defaultHeader(APP_KEY_HEADER_NAME, "e1163d397693d3eac59b92dd2afba9e0")
                .build();
    }

    public List<Food> getFoods(String text) {
        return webClient
                .post()
                .uri("/natural/nutrients")
                .body(BodyInserters.fromObject(new NutritionsRequest(text)))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(NutritionsResponse.class)
                // TODO: not good, should be reactive
                .blockOptional()
                .map(NutritionsResponse::getFoods)
                .orElse(Collections.emptyList());
    }
}
