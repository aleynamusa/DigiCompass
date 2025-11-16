package com.digicompass.backend.unit.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        assertTrue(PasswordValidator.isValid("Strong#123"));
    }

    @Test
    void testShortPassword() {
        assertFalse(PasswordValidator.isValid("A#1b"));
    }

    @Test
    void testMissingUppercase() {
        assertFalse(PasswordValidator.isValid("weak#123"));
    }

    @Test
    void testMissingNumber() {
        assertFalse(PasswordValidator.isValid("Weak#pass"));
    }

    @Test
    void testMissingSpecial() {
        assertFalse(PasswordValidator.isValid("Weak1234"));
    }
}