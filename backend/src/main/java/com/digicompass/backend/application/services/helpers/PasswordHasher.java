package com.digicompass.backend.application.services.helpers;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public final class PasswordHasher {

    private static final int ITERATIONS = 3;
    private static final int MEMORY = 1 << 12;
    private static final int PARALLELISM = 1;

    private static final Argon2 argon2 = Argon2Factory.create();

    private PasswordHasher() {}

    public static String hash(String password) {
        return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password.toCharArray());
    }

    public static boolean verify(String hash, String password) {
        return argon2.verify(hash, password.toCharArray());
    }
}
