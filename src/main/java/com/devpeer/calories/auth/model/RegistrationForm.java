package com.devpeer.calories.auth.model;

import com.devpeer.calories.core.RegularExpressions;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    @NonNull
    @Pattern(regexp = RegularExpressions.USERNAME_REGEXP,
            message = RegularExpressions.USERNAME_REGEXP_DESCRIPTION)
    private String username;

    // HTTP servlet container returns HTTP parameters as String,
    // so there is no point in having password in char[].
    @Pattern(regexp = RegularExpressions.PASSWORD_REGEXP,
            message = RegularExpressions.PASSWORD_REGEXP_DESCRIPTION)
    private String password;
}
