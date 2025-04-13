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
 * PBKDF2WithHmacSHA256.
 * 
 * This class implements secure password handling following industry best
 * practices:
 * <ul>
 * <li>Uses PBKDF2 (Password-Based Key Derivation Function 2) with
 * HMAC-SHA256</li>
 * <li>Employs random salting to prevent rainbow table attacks</li>
 * <li>Applies multiple iterations to increase computational cost for
 * attackers</li>
 * <li>Implements timing-attack resistant comparison for verification</li>
 * <li>Stores password data in a standardized format for persistence</li>
 * </ul>
 * 
 * <p>
 * The password storage format is:
 * <code>base64(salt)$iterations$base64(hash)</code>
 * </p>
 * <p>
 * This implementation follows OWASP security recommendations for password
 * storage
 * and is intended to be used for all password handling in the BTO Management
 * System.
 * </p>
 */
public final class PasswordUtil {
    // Algorithm recommended by OWASP: PBKDF2 with HMAC-SHA256 or HMAC-SHA512
    /**
     * The cryptographic algorithm used for password hashing.
     * PBKDF2WithHmacSHA256 is recommended by OWASP for password hashing.
     */
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Length of the random salt in bytes.
     * 16 bytes (128 bits) provides sufficient randomness to prevent pre-computation
     * attacks.
     */
    private static final int SALT_LENGTH_BYTES = 16;

    /**
     * Number of iterations for the PBKDF2 algorithm.
     * Higher values increase security but also increase computation time.
     * 10000 iterations provides a good balance between security and performance.
     */
    private static final int ITERATION_COUNT = 10000;

    /**
     * Output key length in bits for the PBKDF2 algorithm.
     * 256 bits provides a strong security margin against brute force attacks.
     */
    private static final int KEY_LENGTH_BITS = 256;

    /**
     * Delimiter used to separate parts in the password storage format.
     * The format is: base64(salt)$iterations$base64(hash)
     */
    private static final String DELIMITER = "$";
    // --------------------

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class provides only static methods and should not be instantiated.
     */
    private PasswordUtil() {
        // Private constructor for utility class
    }

    /**
     * Generates a cryptographically secure random salt for password hashing.
     * <p>
     * A salt is a random value that is combined with a password before hashing
     * to prevent dictionary attacks and rainbow table attacks.
     * </p>
     * 
     * @return A byte array containing random salt values of length
     *         SALT_LENGTH_BYTES
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
     * This method:
     * <ol>
     * <li>Generates a cryptographically secure random salt</li>
     * <li>Applies the PBKDF2 algorithm to derive a hash</li>
     * <li>Encodes both salt and hash using Base64</li>
     * <li>Combines them with the iteration count into a single storage-ready
     * string</li>
     * </ol>
     *
     *
     * @param plainPassword The password to hash. Must not be null.
     * @return A string containing the Base64 encoded salt, iteration count,
     *         and Base64 encoded hash, separated by delimiters
     *         (e.g., "base64(salt)$iterations$base64(hash)").
     * @throws NullPointerException if plainPassword is null
     * @throws RuntimeException     if the PBKDF2 algorithm is not available
     *                              or if the key specification is invalid.
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
     * This method:
     * <ol>
     * <li>Parses the stored password string to extract salt, iterations, and
     * hash</li>
     * <li>Re-computes the hash using the provided plain-text password, extracted
     * salt, and iterations</li>
     * <li>Compares the computed hash with the stored hash using a constant-time
     * comparison
     * to prevent timing attacks</li>
     * </ol>
     *
     *
     * @param plainPassword  The plain-text password entered by the user.
     *                       Must not be null.
     * @param hashedPassword The combined string retrieved from storage
     *                       (format:
     *                       "base64(salt)$iterations$base64(hash)"). Must
     *                       not be null.
     * @return true if the password matches the stored hash, false otherwise.
     * @throws NullPointerException     if either parameter is null
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
     * <p>
     * This method applies the Password-Based Key Derivation Function 2 algorithm
     * to derive a cryptographic key (hash) from a password and salt with the
     * specified iteration count and key length.
     * </p>
     *
     * @param password   The password characters to hash
     * @param salt       The salt bytes to use
     * @param iterations The number of iterations to perform
     * @param keyLength  The desired key length in bits
     * @return The derived key (hash) as a byte array
     * @throws RuntimeException If the PBKDF2 algorithm is unavailable or the key
     *                          specification is invalid
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