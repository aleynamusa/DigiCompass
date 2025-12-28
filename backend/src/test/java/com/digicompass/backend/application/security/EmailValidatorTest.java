package com.digicompass.backend.application.security;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    @Test
    void testValidEmail() {
        assertTrue(EmailValidator.isValid("john.doe@example.com"));
    }

    @Test
    void testInvalidEmailMissingAt() {
        assertFalse(EmailValidator.isValid("john.doeexample.com"));
    }

    @Test
    void testInvalidEmailMissingDomain() {
        assertFalse(EmailValidator.isValid("john.doe@"));
    }

    @Test
    void testInvalidEmailSpecialCharacters() {
        assertFalse(EmailValidator.isValid("john!doe@example.com"));
    }

    @Test
    void testEmptyEmail() {
        assertFalse(EmailValidator.isValid(""));
    }

    @Test
    void testNullEmail() {
        assertFalse(EmailValidator.isValid(null));
    }

    @Test
    void getVaValidationError_WhenNull(){
        String error = EmailValidator.getValidationError(null);
        assertEquals("Email cannot be empty.", error);
    }

    @Test
    void getVaValidationError_WhenBlank(){
        String error = EmailValidator.getValidationError("");
        assertEquals("Email cannot be empty.", error);
    }

}