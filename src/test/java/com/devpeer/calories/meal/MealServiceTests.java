package com.devpeer.calories.meal;

import com.devpeer.calories.meal.model.Meal;
import com.devpeer.calories.meal.repository.MealRepository;
import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.core.query.QueryFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.MockitoAnnotations.initMocks;

public class MealServiceTests {
    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

    @Captor
    ArgumentCaptor<Meal> mealArgumentCaptor;

    private static final LocalDate TEST_DATE = LocalDate.parse("2019-08-21",
            DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private static final Meal TEST_MEAL =  Meal.builder()
            .id("id123")
            .userId("user123")
            .date(TEST_DATE)
            .text("This was a good meal")
            .calories(1000).build();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @SuppressWarnings("unchecked")
    private void addMeal(Authority authority) {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(authority));
        mealService.addMeal(userDetails, TEST_MEAL);
        Mockito.verify(mealRepository).save(mealArgumentCaptor.capture());
        assertEquals("user123", mealArgumentCaptor.getValue().getUserId());
        assertNotEquals("id123", mealArgumentCaptor.getValue().getId());
        assertEquals(TEST_DATE, mealArgumentCaptor.getValue().getDate());
        assertEquals("This was a good meal", mealArgumentCaptor.getValue().getText());
        assertEquals(Integer.valueOf(1000), mealArgumentCaptor.getValue().getCalories());
    }

    @Test
    public void givenAdminAndMeal_whenAddMeal_thenSaveMeal() {
        addMeal(Authority.ADMIN);
    }

    @Test(expected = AccessDeniedException.class)
    public void givenUserAndMeal_whenAddMeal_thenThrowAccessDenied() {
        addMeal(Authority.USER);
    }

    @Test
    public void givenRegularUser_whenGetMeal_thenReturnMealByIdAndUserId() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(Authority.USER));

        mealService.getMealById(userDetails, "someid");

        Mockito.verify(mealRepository, Mockito.times(0))
                .findById(any());

        Mockito.verify(mealRepository)
                .findByIdAndUserId("someid","username");
    }

    @SuppressWarnings("unchecked")
    private void updateMeal(Authority authority) {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(authority));

        mealService.replaceMeal(userDetails, TEST_MEAL);
        Mockito.verify(mealRepository).replaceByIdAndUserId(mealArgumentCaptor.capture());
        assertEquals("user123", mealArgumentCaptor.getValue().getUserId());
        assertEquals("id123", mealArgumentCaptor.getValue().getId());
        assertEquals(TEST_DATE, mealArgumentCaptor.getValue().getDate());
        assertEquals("This was a good meal", mealArgumentCaptor.getValue().getText());
        assertEquals(Integer.valueOf(1000), mealArgumentCaptor.getValue().getCalories());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenUser_whenGetMealsNoFilter_thenReturnMealsForUser() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(Authority.USER));

        mealService.getMeals(userDetails, null, PageRequest.of(1, 1));

        Mockito.verify(mealRepository, Mockito.times(0))
                .findAll((QueryFilter) null, PageRequest.of(1, 1));

        Mockito.verify(mealRepository)
                .findAllByUserId("username", PageRequest.of(1, 1));
    }

    @Test(expected = AccessDeniedException.class)
    @SuppressWarnings("unchecked")
    public void givenUser_whenGetMealsWithUserIdFilter_thenThrow() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(Authority.USER));

        mealService.getMeals(userDetails, "(time gt 13:00:00) AND (userId eq someuser1)", PageRequest.of(1, 1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenAdmin_whenGetMealsNoFilter_thenReturnAllMeals() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(Authority.ADMIN));

        mealService.getMeals(userDetails, null, PageRequest.of(1, 1));

        Mockito.verify(mealRepository, Mockito.times(0))
                .findAll((QueryFilter) null, PageRequest.of(1, 1));

        Mockito.verify(mealRepository)
                .findAll(PageRequest.of(1, 1));
    }

    @Test(expected = AccessDeniedException.class)
    public void givenUserAndMeal_whenUpdateMeal_thenReturnAccessDenied() {
        updateMeal(Authority.USER);
    }

    @Test
    public void givenAdminAndMeal_whenUpdateMeal_thenReturnMeals() {
        updateMeal(Authority.ADMIN);
    }
}
