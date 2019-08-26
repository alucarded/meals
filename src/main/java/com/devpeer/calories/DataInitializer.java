package com.devpeer.calories;

import com.devpeer.calories.meal.model.Meal;
import com.devpeer.calories.meal.repository.MealRepository;
import com.devpeer.calories.user.UserRepository;
import com.devpeer.calories.user.model.Authority;
import com.devpeer.calories.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static com.devpeer.calories.core.Common.DATE_FORMATTER;
import static com.devpeer.calories.core.Common.TIME_FORMATTER;

@Component
@Slf4j
@Profile("development")
public class DataInitializer implements CommandLineRunner {

    private static Meal[] TEST_MEALS = {
            Meal.builder()
                    .id("1")
                    .date(LocalDate.parse("2019-06-22", DATE_FORMATTER))
                    .time(LocalTime.parse("22:21:00", TIME_FORMATTER))
                    .text("some text")
                    .calories(120)
                    .build(),
            Meal.builder()
                    .id("2")
                    .date(LocalDate.parse("2019-07-11", DATE_FORMATTER))
                    .time(LocalTime.parse("14:21:00", TIME_FORMATTER))
                    .text("some text2")
                    .calories(456)
                    .build(),
            Meal.builder()
                    .id("3")
                    .date(LocalDate.parse("2019-08-21", DATE_FORMATTER))
                    .time(LocalTime.parse("09:11:00", TIME_FORMATTER))
                    .text("some text3")
                    .calories(333)
                    .build(),
            Meal.builder()
                    .id("4")
                    .date(LocalDate.parse("2019-08-24", DATE_FORMATTER))
                    .time(LocalTime.parse("09:11:00", TIME_FORMATTER))
                    .text("some text4")
                    .calories(122)
                    .build(),
            Meal.builder()
                    .id("5")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("12:25:12", TIME_FORMATTER))
                    .text("some text5")
                    .calories(1200)
                    .build(),
            Meal.builder()
                    .id("6")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("15:25:12", TIME_FORMATTER))
                    .text("some text6")
                    .calories(100)
                    .build(),
            Meal.builder()
                    .id("7")
                    .date(LocalDate.parse("2019-08-25", DATE_FORMATTER))
                    .time(LocalTime.parse("17:20:15", TIME_FORMATTER))
                    .text("some text7")
                    .calories(300)
                    .build()
    };

    @Autowired
    UserRepository users;

    @Autowired
    MealRepository mealRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        try {
            this.users.save(User.builder()
                    .username("user")
                    .password(this.passwordEncoder.encode("password"))
                    .authorities(Arrays.asList(Authority.USER))
                    .build()
            );
        } catch (DuplicateKeyException e) {
            log.info("Test user already in DB", e);
        }

        try {
            this.users.save(User.builder()
                    .username("admin")
                    .password(this.passwordEncoder.encode("password"))
                    .authorities(Arrays.asList(Authority.USER, Authority.ADMIN))
                    .build()
            );
        } catch (DuplicateKeyException e) {
            log.info("Test admin already in DB", e);
        }

        // Not good to try-catch i loop, but that is only for testing
        for (Meal testMeal : TEST_MEALS) {
            try {
                this.mealRepository.save(testMeal);
            } catch (DuplicateKeyException e) {
                log.info("Test meal" + testMeal.toString() + " already in DB", e);
            }
        }

        log.debug("Printing test users...");
        this.users.findAll().forEach(v -> log.debug(" User :" + v.toString()));
    }
}