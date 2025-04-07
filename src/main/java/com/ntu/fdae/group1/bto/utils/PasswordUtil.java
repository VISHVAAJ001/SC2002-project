package com.ntu.fdae.group1.bto.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest; // Still needed for isEqual
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * Utility class for handling password hashing and verification using
 * PBKDF2WithHmacSHA256. This is a standard key derivation function
 * designed to be slow, increasing resistance to brute-force attacks.
 *
 * Stores the salt, iteration count, and hash together, Base64 encoded,
 * separated by delimiters.
 * Format: base64(salt)$iterations$base64(hash)
 *
 * This is the recommended approach for password hashing within standard Java
 * libraries.
 */
public final class PasswordUtil {
    // Algorithm recommended by OWASP: PBKDF2 with HMAC-SHA256 or HMAC-SHA512
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int ITERATION_COUNT = 10000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final String DELIMITER = "$";
    // --------------------

    private PasswordUtil() {
        // Private constructor for utility class
    }

    /**
     * Generates a random salt.
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes the given plain-text password using PBKDF2WithHmacSHA256
     * with a newly generated salt and configured iteration count.
     *
     * @param plainPassword The password to hash. Must not be null.
     * @return A string containing the Base64 encoded salt, iteration count,
     *         and Base64 encoded hash, separated by delimiters
     *         (e.g., "base64(salt)$iterations$base64(hash)").
     * @throws RuntimeException if the PBKDF2 algorithm is not available
     *                          or if the key specification is invalid.
     */
    public static String hashPassword(String plainPassword) {
        Objects.requireNonNull(plainPassword, "Password cannot be null");

        byte[] salt = generateSalt();
        byte[] hash = pbkdf2Hash(plainPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH_BITS);

        // Encode salt and hash to Base64 strings
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHash = Base64.getEncoder().encodeToString(hash);

        // Combine salt, iteration count, and hash with delimiters
        return new StringBuilder()
                .append(encodedSalt)
                .append(DELIMITER)
                .append(ITERATION_COUNT) // Store iterations used
                .append(DELIMITER)
                .append(encodedHash)
                .toString();
    }

    /**
     * Verifies a plain-text password against a stored combined salt, iterations,
     * and hash string.
     *
     * @param plainPassword  The plain-text password entered by the user.
     *                       Must not be null.
     * @param hashedPassword The combined string retrieved from storage
     *                       (format:
     *                       "base64(salt)$iterations$base64(hash)"). Must
     *                       not be null.
     * @return true if the password matches the stored hash, false otherwise.
     * @throws IllegalArgumentException if the storedCombinedPassword format is
     *                                  invalid.
     * @throws RuntimeException         if the PBKDF2 algorithm is not available
     *                                  or if the key specification is invalid
     *                                  during verification.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        Objects.requireNonNull(plainPassword, "Password cannot be null");
        Objects.requireNonNull(hashedPassword, "Stored password data cannot be null");

        // Split the stored string into parts
        String[] parts = hashedPassword.split("\\" + DELIMITER); // Need to escape regex special char $
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid stored password format. Expected 'salt" + DELIMITER + "iterations" + DELIMITER + "hash'.");
        }

        String encodedSalt = parts[0];
        int iterations;
        String encodedStoredHash = parts[2];

        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid iteration count in stored password data.", e);
        }

        // Decode Base64 salt and hash back to bytes
        byte[] salt;
        byte[] storedHashBytes;
        try {
            salt = Base64.getDecoder().decode(encodedSalt);
            storedHashBytes = Base64.getDecoder().decode(encodedStoredHash);
        } catch (IllegalArgumentException e) {
            System.err.println("Failed to decode stored salt or hash: " + e.getMessage());
            return false; // Treat decode failure as verification failure
        }

        // Hash the input password using the *extracted* salt and iterations
        byte[] calculatedHashBytes = pbkdf2Hash(plainPassword.toCharArray(), salt, iterations, KEY_LENGTH_BITS);

        // Compare the calculated hash with the stored hash (timing-attack resistant)
        return MessageDigest.isEqual(calculatedHashBytes, storedHashBytes);
    }

    /**
     * Helper method to perform the actual PBKDF2 hashing.
     */
    private static byte[] pbkdf2Hash(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: PBKDF2 algorithm '" + PBKDF2_ALGORITHM + "' not found.", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Error: Invalid PBEKeySpec for PBKDF2.", e);
        }
    }
}