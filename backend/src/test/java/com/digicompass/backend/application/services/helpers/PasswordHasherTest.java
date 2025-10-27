package com.digicompass.backend.application.services.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {


    @Test
    void testHash_ShouldGenerateDifferentHashEachTime() {
        String password = "Strong#123";

        String hash1 = PasswordHasher.hash(password);
        String hash2 = PasswordHasher.hash(password);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2, "Argon2 should produce different hashes for the same password due to random salt");
    }

    @Test
    void testVerify_ShouldReturnTrue_ForCorrectPassword() {
        String password = "Strong#123";
        String hash = PasswordHasher.hash(password);

        assertTrue(PasswordHasher.verify(hash, password), "Verification should succeed for the correct password");
    }

    @Test
    void testVerify_ShouldReturnFalse_ForIncorrectPassword() {
        String hash = PasswordHasher.hash("Strong#123");
        String wrongPassword = "Wrong#123";

        assertFalse(PasswordHasher.verify(hash, wrongPassword), "Verification should fail for an incorrect password");
    }

    @Test
    void testHash_ShouldThrowException_ForNullPassword() {
        assertThrows(NullPointerException.class, () -> PasswordHasher.hash(null),
                "Hashing null should throw NullPointerException");
    }

    @Test
    void testVerify_ShouldHandleInvalidHashGracefully() {
        String fakeHash = "invalid-hash-format";
        String password = "Strong#123";

        // Argon2.verify() returns false if hash format is invalid — not an exception
        assertFalse(PasswordHasher.verify(fakeHash, password),
                "Invalid hash format should return false, not throw exception");
    }

    @Test
    void testVerify_ShouldThrowException_ForNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.verify("hashed", null),
                "Password and hash cannot be null");
    }

    @Test
    void testVerify_ShouldThrowException_ForNullHash() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.verify(null, "Password123@"),
                "Password and hash cannot be null");
    }


    @Test
    void testVerify_ShouldThrowException_ForNullPasswordAndHash() {
        assertThrows(IllegalArgumentException.class, () -> PasswordHasher.verify("hashed", null),
                "Password and hash cannot be null");
    }
}