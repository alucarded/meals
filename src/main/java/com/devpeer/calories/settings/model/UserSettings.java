package com.devpeer.calories.settings.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


// TODO: put settings directly on user document, remove this
// Fun fact: if user deletes his account and another user creates account with same username,
// then he will have same settings as deleted user, because settings are not removed with user document ;)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {
    @Id
    private String userId;
    private Integer expectedCaloriesPerDay;
}
