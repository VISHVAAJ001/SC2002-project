package com.ntu.fdae.group1.bto.utils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for common data types and formats
 * in the BTO Management System.
 * <p>
 * This class contains static methods for validating various inputs against
 * specific format requirements, such as NRIC validation. It provides a
 * centralized
 * location for all validation logic to ensure consistency throughout the
 * application.
 * </p>
 * <p>
 * All methods in this class are designed to be null-safe and follow consistent
 * validation patterns.
 * </p>
 */
public class ValidationUtil {

    /**
     * Regular expression pattern for validating Singapore NRIC numbers.
     * 
     * The pattern validates that the NRIC:
     * <ul>
     * <li>Starts with S, T, F, or G (S/T for citizens, F/G for foreigners)</li>
     * <li>Followed by exactly 7 digits</li>
     * <li>Ends with a capital letter checksum</li>
     * </ul>
     * 
     * <p>
     * Note: This pattern only validates the format, not the checksum algorithm.
     * </p>
     */
    static final String NRIC_REGEX = "^[STFG]\\d{7}[A-Z]$";

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 16;

    // Regex Explanation:
    // (?=.*[a-z])  : Positive lookahead ensuring at least one lowercase letter
    // (?=.*[A-Z])  : Positive lookahead ensuring at least one uppercase letter
    // (?=.*\d)     : Positive lookahead ensuring at least one digit
    // (?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]) : Positive lookahead ensuring at least one special character
    //                                               (Adjust the character set [] as needed)
    // [A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~] : Defines the allowed characters in the password
    // {8,16}       : Enforces the length constraint (redundant with separate length check, but good practice)
    // Note: The character set `[...]` should match the special characters in the lookahead `(?=.*[...])`
    // Important: This regex implicitly disallows whitespace because \s is not included in the allowed character set.

    // Simpler regex for just checking for whitespace
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    // For error messages - list the allowed special characters
    public static final String ALLOWED_SPECIAL_CHARS = "!@#$%^&*()_+-=[]{};':\"\\|,.<>/?~";

    /**
     * Validates if a string conforms to the Singapore NRIC format.
     * 
     * This method checks if the provided string:
     * <ul>
     * <li>Is not null</li>
     * <li>Matches the NRIC format pattern: starting with S, T, F, or G,
     * followed by exactly 7 digits, and ending with a capital letter</li>
     * </ul>
     * 
     * <p>
     * The validation only checks the format and not whether the NRIC
     * is actually valid according to the checksum algorithm.
     * </p>
     * 
     * @param nric The NRIC string to validate
     * @return true if the string is in valid NRIC format, false otherwise
     *         (including for null input)
     */
    public static boolean isValidNric(String nric) {
        return nric != null && nric.matches(NRIC_REGEX);
    }

    /**
     * Validates a password against the defined strength criteria.
     *
     * Criteria:
     * - Between MIN_LENGTH and MAX_LENGTH characters (inclusive)
     * - At least 1 uppercase letter
     * - At least 1 lowercase letter
     * - At least 1 digit
     * - At least 1 special symbol (from ALLOWED_SPECIAL_CHARS)
     * - No whitespace characters
     *
     * @param password The password string to validate.
     * @return A String containing an error message if validation fails, or null if the password is valid.
     */
    public static String validatePasswordStrength(String password) {
        Objects.requireNonNull(password, "Password cannot be null");

        // 1. Check Length
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return "Password must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters long.";
        }

        // 2. Check for Whitespace
        if (WHITESPACE_PATTERN.matcher(password).find()) {
            return "Password cannot contain whitespace characters.";
        }

        // 3. Check Complexity (Using individual checks for clearer error messages)
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit.";
        }
        // Use Pattern.quote to safely include special characters in the regex character class
        if (!password.matches(".*[" + Pattern.quote(ALLOWED_SPECIAL_CHARS) + "].*")) {
             return "Password must contain at least one special symbol (" + ALLOWED_SPECIAL_CHARS + ").";
        }

        // 4. If all checks pass: valid password
        return null;
    }
}
