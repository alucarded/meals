package com.devpeer.calories.meal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CaloriesForDay {

    @Data
    @NoArgsConstructor
    @ToString
    public static class AggId {
        private String userId;

        private LocalDate date;
    }

    private AggId _id;

    private String userId;

    private LocalDate date;

    private Integer totalCalories;
}
