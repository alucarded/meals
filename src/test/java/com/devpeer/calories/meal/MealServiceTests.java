package com.devpeer.calories.meal;

import com.devpeer.calories.auth.user.Authority;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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

    @Before
    public void setUp() {
        initMocks(this);
    }

    @SuppressWarnings("unchecked")
    private void addMeal(Authority authority) {
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        LocalDateTime now = LocalDateTime.now();
        Meal meal = Meal.builder()
                .id("id123")
                .userId("user123")
                .dateTime(now)
                .text("This was a good meal")
                .calories(1000).build();
        Mockito.when(userDetails.getUsername()).thenReturn("username");
        Mockito.when(userDetails.getAuthorities())
                .thenReturn((Collection) Collections.singleton(authority));
        mealService.addMeal(userDetails, meal);
        Mockito.verify(mealRepository).save(mealArgumentCaptor.capture());
        assertEquals("user123", mealArgumentCaptor.getValue().getUserId());
        assertNotEquals("id123", mealArgumentCaptor.getValue().getId());
        assertEquals(now, mealArgumentCaptor.getValue().getDateTime());
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

    @Test
    public void givenMeals_whenUpdateMeals_thenReturnMeals() {

    }

    @Test
    public void givenMeal_whenDeleteMeal_thenReturnOk() {

    }
}
