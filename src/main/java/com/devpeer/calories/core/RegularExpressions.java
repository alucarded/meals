package com.devpeer.calories.core;

public class RegularExpressions {
    public static final String PASSWORD_REGEXP = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,100}$";
    public static final String PASSWORD_REGEXP_DESCRIPTION = "Password must include a digit, lowercase letter, uppercase letter, special character and must be at least 8 characters long.";

    public static final String USERNAME_REGEXP = "^[a-zA-Z0-9]{5,100}";
    public static final String USERNAME_REGEXP_DESCRIPTION = "Length must be at least 5";
}
