package com.app.b_and_t_lms.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])" + // at least one lowercase letter
            "(?=.*[A-Z])" + // at least one uppercase letter
            "(?=.*\\d)" + // at least one digit
            "(?=.*[^A-Za-z0-9])" + // at least one special character
            "(?=\\S+$)" + // no whitespace
            ".{8,}$"; // at least 8 characters

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValid(String password) {
        return pattern.matcher(password).matches();
    }
}
