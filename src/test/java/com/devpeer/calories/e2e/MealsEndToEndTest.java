package com.devpeer.calories.e2e;

import com.devpeer.calories.auth.model.AuthenticationRequest;
import com.devpeer.calories.auth.model.AuthenticationResult;
import com.devpeer.calories.core.jackson.Jackson;
import com.devpeer.calories.meal.model.Meal;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(LocalApplicationRunner.class)
public class MealsEndToEndTest {

    private WebTestClient webTestClient;

    private String token;

    @Before
    public void setUp() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
        // TODO: could be in BeforeClass
        token = Objects.requireNonNull(webTestClient.post()
                .uri("/v1/auth/signin")
                .syncBody(new AuthenticationRequest("admin", "password"))
                .exchange()
                .returnResult(AuthenticationResult.class)
                .getResponseBody()
                .blockFirst())
                .getToken();
    }

    @Test
    public void testAddMealWithNoCalories() {
        Meal mealResponse = webTestClient.post()
                .uri("/v1/meals")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .syncBody(Meal.builder().text("one egg").build())
                .exchange()
                .expectBody(Meal.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(mealResponse);
        assertTrue(mealResponse.getCalories() > 0);
        System.out.println("Calories: " + mealResponse.getCalories());
    }

    @Test
    public void testGetMealsWithFilter() {
        String mealsResponse = webTestClient.get()
                .uri("/v1/meals?filter=calories gt 100&page=0&size=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        Page<Meal> mealsPage = Jackson.fromJsonUnsafe(mealsResponse, new TypeReference<Page<Meal>>() {});
        System.out.println(mealsPage.getContent());
    }
}
