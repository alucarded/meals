package com.devpeer.calories.meal;

import com.devpeer.calories.CaloriesApplication;
import com.devpeer.calories.auth.CustomUserDetailsService;
import com.devpeer.calories.auth.jwt.JwtTokenProvider;
import com.devpeer.calories.auth.user.UserRepository;
import com.devpeer.calories.core.Jackson;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test.
 *
 * Currently controller + service layer.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = {MealsRestController.class},
        includeFilters = {@ComponentScan.Filter({Service.class})})
@ContextConfiguration(classes = {CaloriesApplication.class, JwtTokenProvider.class, CustomUserDetailsService.class})
public class MealsRestControllerTests {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MealService mealService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MealRepository mealRepository;

    @Test
    @WithMockUser(username = "tom123", authorities = {"USER"})
    public void givenUserAndMeal_whenAddMeal_thenReturnAddedMeal() throws Exception {
        Meal meal = Meal.builder()
                .text("Chicken breasts with rice")
                .calories(600)
                .build();

        given(mealRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg());

        mvc.perform(post("/v1/meals").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(meal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.userId", is("tom123")))
                .andDo(result -> {
                    Meal returnedMeal = Jackson.fromJsonUnsafe(result.getResponse().getContentAsString(), Meal.class);
                    assertTrue(LocalDateTime.now().compareTo(returnedMeal.getDateTime()) >= 0);
                })
                // TODO: The following can fail from time to time...
                //.andExpect(jsonPath("$.dateTime", is(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.text", is("Chicken breasts with rice")))
                .andExpect(jsonPath("$.calories", is(600)));
    }

    private ResultActions testAddMeal(Meal meal) throws Exception {
        given(mealRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg());

        return mvc.perform(post("/v1/meals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Jackson.toJsonUnsafe(meal)));
    }

    @Test
    @WithMockUser(username = "admin123", authorities = {"ADMIN"})
    public void givenAdminAndMeal_whenAddMealForDifferentUser_thenReturnAddedMeal() throws Exception {
        Meal meal = Meal.builder()
                .id("someid")
                .userId("jake")
                .dateTime(LocalDateTime.parse("2019-08-12 12:00:00", dateTimeFormatter))
                .text("Chicken breasts with rice")
                .calories(600)
                .build();

        testAddMeal(meal)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not("someid")))
                .andExpect(jsonPath("$.userId", is(meal.getUserId())))
                .andExpect(jsonPath("$.dateTime", is(meal.getDateTime().format(dateTimeFormatter))))
                .andExpect(jsonPath("$.text", is(meal.getText())))
                .andExpect(jsonPath("$.calories", is(meal.getCalories())));
    }

    @Test
    @WithMockUser(username = "tom123", authorities = {"USER"})
    public void givenUserAndMeal_whenAddMealForDifferentUser_thenReturnAccessDenied() throws Exception {
        Meal meal = Meal.builder()
                .id("someid")
                .userId("jake")
                .dateTime(LocalDateTime.parse("2019-08-12 12:00:00",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .text("Chicken breasts with rice")
                .calories(600)
                .build();

        testAddMeal(meal)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void givenAnonymousAndMeal_whenAddMeal_thenReturnAccessDenied() throws Exception {
        Meal meal = Meal.builder()
                .text("Chicken breasts with rice")
                .calories(600)
                .build();

        mvc.perform(post("/v1/meals").contentType(MediaType.APPLICATION_JSON).content(Jackson.toJsonUnsafe(meal)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }
}
