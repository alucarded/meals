package com.devpeer.calories.meal;

import com.devpeer.calories.meal.model.Meal;
import com.devpeer.calories.meal.repository.MealRepository;
import com.devpeer.calories.settings.UserSettingsRepository;
import com.devpeer.calories.settings.model.UserSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.devpeer.calories.core.Common.DATE_FORMATTER;
import static com.devpeer.calories.core.Common.TIME_FORMATTER;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class MealRepositoryTests {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    private static final Meal[] TEST_MEALS = {
            Meal.builder()
                    .id("1")
                    .userId("admin")
                    .date(LocalDate.parse("2019-06-22", DATE_FORMATTER))
                    .time(LocalTime.parse("22:21:00", TIME_FORMATTER))
                    .text("some text")
                    .calories(120)
                    .build(),
            Meal.builder()
                    .id("2")
                    .userId("admin")
                    .date(LocalDate.parse("2019-07-11", DATE_FORMATTER))
                    .time(LocalTime.parse("14:21:00", TIME_FORMATTER))
                    .text("some text2")
                    .calories(456)
                    .build(),
            Meal.builder()
                    .id("3")
                    .userId("admin")
                    .date(LocalDate.parse("2019-08-21", DATE_FORMATTER))
                    .time(LocalTime.parse("09:11:00", TIME_FORMATTER))
                    .text("some text3")
                    .calories(333)
                    .build(),
            Meal.builder()
                    .id("4")
                    .userId("admin")
                    .date(LocalDate.parse("2019-08-24", DATE_FORMATTER))
                    .time(LocalTime.parse("09:11:00", TIME_FORMATTER))
                    .text("some text4")
                    .calories(122)
                    .build(),
            Meal.builder()
                    .id("5")
                    .userId("admin")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("12:25:12", TIME_FORMATTER))
                    .text("some text5")
                    .calories(1200)
                    .build(),
            Meal.builder()
                    .id("6")
                    .userId("admin")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("15:25:12", TIME_FORMATTER))
                    .text("some text6")
                    .calories(100)
                    .build(),
            Meal.builder()
                    .id("7")
                    .userId("admin")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("17:20:15", TIME_FORMATTER))
                    .text("some text7")
                    .calories(300)
                    .build(),
            Meal.builder()
                    .id("8")
                    .userId("user123")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("18:56:00", TIME_FORMATTER))
                    .text("some text8")
                    .calories(487)
                    .build(),
            Meal.builder()
                    .id("9")
                    .userId("user123")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("11:34:23", TIME_FORMATTER))
                    .text("some text9")
                    .calories(614)
                    .build()
    };

    @Before
    public void setUpData() {
        Arrays.stream(TEST_MEALS).forEach(mealRepository::save);
        userSettingsRepository.save(new UserSettings("admin", 1000));
    }

    @Test
    public void testFindAllWithAggregations() {
        PageRequest pageRequest = PageRequest.of(0, 8);
        Page<Meal> pagedMeals = mealRepository.findAllWithAggregations(null, pageRequest);
        pagedMeals.forEach(
                meal -> System.out.println(meal.toString())
        );
        assertEquals(8, pagedMeals.getTotalElements());
        assertEquals(1, pagedMeals.getTotalPages());

        List<Meal> meals = pagedMeals.getContent();

        Meal firstMeal = meals.get(0);
        assertEquals("admin", firstMeal.getUserId());
        assertEquals(LocalDate.parse("2019-06-22", DATE_FORMATTER), firstMeal.getDate());
        assertEquals(LocalTime.parse("22:21:00", TIME_FORMATTER), firstMeal.getTime());
        assertEquals(120, (long) firstMeal.getCalories());
        assertEquals(true, firstMeal.getIsTotalForTheDayOk());

        Meal sixthMeal = meals.get(4);
        assertEquals("admin", sixthMeal.getUserId());
        assertEquals(LocalDate.parse("2019-08-25", DATE_FORMATTER), sixthMeal.getDate());
        assertEquals(LocalTime.parse("12:25:12", TIME_FORMATTER), sixthMeal.getTime());
        assertEquals(1200, (long) sixthMeal.getCalories());
        assertEquals(false, sixthMeal.getIsTotalForTheDayOk());

        Meal ninthMeal = meals.get(7);
        assertEquals("user123", ninthMeal.getUserId());
        assertEquals(LocalDate.parse("2019-08-25", DATE_FORMATTER), ninthMeal.getDate());
        assertEquals(LocalTime.parse("18:56:00", TIME_FORMATTER), ninthMeal.getTime());
        assertEquals(487, (long) ninthMeal.getCalories());
        // No expected calories for a day in settings, isTotalForTheDayOk defaults to true
        assertEquals(true, ninthMeal.getIsTotalForTheDayOk());
    }

    @Test
    public void testReplaceMeal() {
        Meal replacedMeal = Meal.builder()
                .id("1")
                .userId("admin")
                .date(LocalDate.parse("2019-06-23", DATE_FORMATTER))
                .text("some replaced text")
                .calories(120)
                .build();
        assertEquals(replacedMeal, mealRepository.replaceByIdAndUserId(replacedMeal));
    }
}
