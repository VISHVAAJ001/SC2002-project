package com.ntu.fdae.group1.bto.utils;

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
}
