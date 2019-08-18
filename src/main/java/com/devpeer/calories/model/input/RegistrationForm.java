package com.devpeer.calories.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    private String username;
    // HTTP servlet container returns HTTP parameters as String,
    // so there is no point in having password in char[].
    private String password;
}
