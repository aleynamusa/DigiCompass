package com.digicompass.backend.application.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testCalculateAge() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        User user = new User(1L, "test", "test@example.com", birthDate, "password123", 2L, null, null, true);

        int age = user.CalculateAge();

        int expectedAge = Period.between(birthDate, LocalDate.now()).getYears();
        assertEquals(expectedAge, age);
    }
}