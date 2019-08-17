package com.devpeer.calories.model.input;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationForm {
    private String username;
    private char[] password;
}
