package com.ntu.fdae.group1.bto.enums;

/**
 * Defines the possible marital statuses for users in the BTO system.
 * <p>
 * Marital status is a key eligibility factor for BTO housing applications, as
 * different flat types and schemes have different marital status requirements.
 * This enum provides standardized values for tracking a user's marital status.
 * </p>
 * <p>
 * Each enum value stores a user-friendly display string that can be used in the
 * UI.
 * </p>
 */
public enum MaritalStatus {
    /**
     * Represents a user who is not married.
     * Single applicants may have restrictions on certain flat types or schemes.
     */
    SINGLE("Single"),

    /**
     * Represents a user who is legally married.
     * Married applicants may qualify for additional schemes or flat types.
     */
    MARRIED("Married");

    /**
     * The user-friendly display string representing this marital status.
     */
    private final String status;

    /**
     * Constructs a new MaritalStatus enum value with the specified display string.
     *
     * @param status The user-friendly display string for this status
     */
    MaritalStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the user-friendly display string for this marital status.
     *
     * @return The display string representation of this status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the user-friendly display string for this marital status.
     *
     * @return The display string representation of this status
     */
    @Override
    public String toString() {
        return status;
    }

    // public static MaritalStatus valueOfIgnoreCase(String status) {
    // return valueOf(status.toUpperCase());
    // }
}
