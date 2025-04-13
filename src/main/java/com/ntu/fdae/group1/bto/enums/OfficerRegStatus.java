package com.ntu.fdae.group1.bto.enums;

/**
 * Defines the possible registration statuses for HDB officers in the BTO
 * system.
 * <p>
 * This enum tracks the lifecycle of an officer's registration from submission
 * to approval or rejection.
 * Each status represents a stage in the registration process and determines
 * what system functions
 * the officer can access.
 * </p>
 *
 * The typical flow is:
 * <ol>
 * <li>PENDING - Initial state when an officer submits registration</li>
 * <li>APPROVED or REJECTED - Final state after admin review</li>
 * </ol>
 *
 * <p>
 * Each enum value stores a user-friendly display string that can be used in the
 * UI.
 * </p>
 */
public enum OfficerRegStatus {
    /**
     * Represents a registration request that is awaiting review.
     * Officers with pending status have limited system access.
     */
    PENDING("Pending"),

    /**
     * Represents a registration request that has been approved by an administrator.
     * Officers with approved status have full access to officer functions.
     */
    APPROVED("Approved"),

    /**
     * Represents a registration request that has been rejected by an administrator.
     * Officers with rejected status cannot access officer functions.
     */
    REJECTED("Rejected");

    /**
     * The user-friendly display string representing this registration status.
     */
    private final String status;

    /**
     * Constructs a new OfficerRegStatus enum value with the specified display
     * string.
     *
     * @param status The user-friendly display string for this status
     */
    OfficerRegStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the user-friendly display string for this registration status.
     *
     * @return The display string representation of this status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the user-friendly display string for this registration status.
     *
     * @return The display string representation of this status
     */
    @Override
    public String toString() {
        return status;
    }
}