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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class MealServiceTests {
    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

    @Captor
    ArgumentCaptor<Meal> mealArgumentCaptor;

    private static final LocalDateTime TEST_DATETIME = LocalDateTime.parse("2019-08-21 13:00:00",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private static final Meal TEST_MEAL =  Meal.builder()
            .id("id123")
            .userId("user123")
            .dateTime(TEST_DATETIME)
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
        assertEquals(TEST_DATETIME, mealArgumentCaptor.getValue().getDateTime());
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
    public void givenMeals_whenGetMeals_thenReturnMeals() {

    }

    @SuppressWarnings("unchecked")
    private void updateMeal(Authority authority) {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(authority));

        mealService.updateMeal(userDetails, TEST_MEAL);
        Mockito.verify(mealRepository).update(mealArgumentCaptor.capture());
        assertEquals("user123", mealArgumentCaptor.getValue().getUserId());
        assertEquals("id123", mealArgumentCaptor.getValue().getId());
        assertEquals(TEST_DATETIME, mealArgumentCaptor.getValue().getDateTime());
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

        mealService.getMeals(userDetails, null, 1, 1);

        Mockito.verify(mealRepository, Mockito.times(0))
                .findAll((QueryFilter) null, PageRequest.of(1, 1));

        Mockito.verify(mealRepository)
                .findAllByUserId("username", PageRequest.of(1, 1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenAdmin_whenGetMealsNoFilter_thenReturnAllMeals() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(Authority.ADMIN));

        mealService.getMeals(userDetails, null, 1, 1);

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
