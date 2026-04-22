package com.GoldenOpportunity.dbLogin;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Password hashing helper based on PBKDF2-HMAC-SHA256.
 *
 * Format: {@code pbkdf2$<iterations>$<saltBase64>$<hashBase64>}.
 * - Salt is random per password (16 bytes).
 * - Iterations increase work factor against brute force.
 * - Hash is 32 bytes (256 bits).
 */
public final class PasswordHasher {
    private static final String ALGO = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65_536;      // Work factor; tune as needed
    private static final int KEY_LENGTH_BITS = 256;    // 32 bytes
    private static final int SALT_BYTES = 16;          // 128-bit salt

    private PasswordHasher() {}

    /** Create a new salted PBKDF2 hash string for storage. */
    public static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        /** Generate the salt */
        new SecureRandom().nextBytes(salt);
        /** Hash users password */
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        /** return string of hashed password */
        /** Why: storing salt, iterations, and hash together lets verify() split on
         $, read the parameters, recompute, and compare without hardcoding settings
         or separate columns. */
        return String.format("pbkdf2$%d$%s$%s",
                ITERATIONS,
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash));
    }

    /** Validate a plaintext password against a stored PBKDF2 string. */
    public static boolean verify(String password, String stored) {
        try {
            /** As explained above (Line: 34) we split at $ */
            String[] parts = stored.split("\\$");
            /** If the parts of the hash does not meet the format, it is not valid */
            if (parts.length != 4 || !"pbkdf2".equals(parts[0])) return false;
            /** Grab the parts of the password hash */
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            /** Add all parts together */
            byte[] actual = pbkdf2(password.toCharArray(), salt, iterations, expected.length * 8);
            /** Return the hash of the attempted password used to login. */
            return slowEquals(expected, actual);
        } catch (Exception e) {
            /** Any parse/crypto error results in a failure without leaking detail. */
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("PBKDF2 failure", e);
        }
    }

    /** Constant-time comparison to avoid timing issues */
    private static boolean slowEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
