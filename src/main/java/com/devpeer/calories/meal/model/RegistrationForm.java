package com.devpeer.calories.meal.model;

import com.devpeer.calories.core.RegularExpressions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    @Pattern(regexp = RegularExpressions.USERNAME_REGEXP,
            message = RegularExpressions.USERNAME_REGEXP_DESCRIPTION)
    private String username;

    // HTTP servlet container returns HTTP parameters as String,
    // so there is no point in having password in char[].
    @Pattern(regexp = RegularExpressions.PASSWORD_REGEXP,
            message = RegularExpressions.PASSWORD_REGEXP_DESCRIPTION)
    private String password;
}
