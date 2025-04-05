package com.ntu.fdae.group1.bto.utils;

public class ValidationUtil {
    static final String NRIC_REGEX = "^[STFG]\\d{7}[A-Z]$";

    // Validates NRIC format
    public static boolean isValidNric(String nric) {
        return nric != null && nric.matches(NRIC_REGEX);
    }
}
