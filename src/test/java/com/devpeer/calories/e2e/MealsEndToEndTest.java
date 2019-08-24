package com.devpeer.calories.e2e;

import com.devpeer.calories.auth.model.AuthenticationRequest;
import com.devpeer.calories.auth.model.AuthenticationResult;
import com.devpeer.calories.meal.model.Meal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(LocalApplicationRunner.class)
public class MealsEndToEndTest {

    private WebTestClient webTestClient;

    private String token;

    // TODO: could be BeforeClass
    @Before
    public void setUp() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
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
}
