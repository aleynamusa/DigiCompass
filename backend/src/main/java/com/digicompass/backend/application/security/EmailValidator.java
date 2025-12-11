package com.digicompass.backend.application.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@AllArgsConstructor
@NoArgsConstructor
public class EmailValidator {


    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    public static boolean isValid(String email) {
        if (email == null || email.isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static String getValidationError(String email) {
        if (email == null || email.isEmpty()) return "Email cannot be empty.";
        if (!EMAIL_PATTERN.matcher(email).matches()) return "Email is not valid.";
        return null;
    }
}
