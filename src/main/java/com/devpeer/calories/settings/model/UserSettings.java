package com.devpeer.calories.settings.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {
    @Id
    private String userId;
    private Integer expectedCaloriesPerDay;
}
