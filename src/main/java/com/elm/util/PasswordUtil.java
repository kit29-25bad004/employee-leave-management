package com.elm.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Reusable utility for hashing and verifying passwords using SHA-256.
 */
public class PasswordUtil {

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes a plain-text password using SHA-256 and returns a 64-character lowercase hex string.
     *
     * @param plainPassword The plain-text password to hash
     * @return Hexadecimal representation of SHA-256 hash, or null if input is null
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available in environment", e);
        }
    }

    /**
     * Verifies whether a plain-text password matches an existing SHA-256 hash.
     *
     * @param plainPassword  The plain-text candidate password
     * @param hashedPassword The stored SHA-256 hexadecimal hash
     * @return true if matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String computedHash = hashPassword(plainPassword);
        return computedHash.equalsIgnoreCase(hashedPassword);
    }
}
