package com.digicompass.backend.application.security;

import java.util.regex.Pattern;


public class PasswordValidator {
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[#?!@$%^&*-]");

    public static boolean isValid(String password) {
        if (password == null) return false;
        return password.length() >= 8 &&
                UPPERCASE.matcher(password).find() &&
                LOWERCASE.matcher(password).find() &&
                DIGIT.matcher(password).find() &&
                SPECIAL.matcher(password).find();
    }

    public static String getValidationError(String password) {
        if (password == null || password.isEmpty()) return "Password cannot be empty.";
        if (password.length() < 8) return "Password must be at least 8 characters long.";
        if (!UPPERCASE.matcher(password).find()) return "Password must contain an uppercase letter.";
        if (!LOWERCASE.matcher(password).find()) return "Password must contain a lowercase letter.";
        if (!DIGIT.matcher(password).find()) return "Password must contain a number.";
        if (!SPECIAL.matcher(password).find()) return "Password must contain a special character (#?!@$%^&*-).";
        return null;
    }
}
