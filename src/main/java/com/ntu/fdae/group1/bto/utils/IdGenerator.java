package com.ntu.fdae.group1.bto.utils;

import java.util.UUID;

/**
 * Utility class for generating unique identifiers for various entities.
 * Uses Java's built-in UUID generator for stateless and highly unique IDs.
 */
public final class IdGenerator {

    // Private constructor to prevent instantiation of utility class
    private IdGenerator() {
    }

    private static final String APPLICATION_PREFIX = "APP-";
    private static final String BOOKING_PREFIX = "BOOK-";
    private static final String ENQUIRY_PREFIX = "ENQ-";
    private static final String PROJECT_FLAT_INFO_PREFIX = "FLAT-";
    private static final String PROJECT_PREFIX = "PROJ-";
    private static final String OFFICER_REG_PREFIX = "REG-";
    // No prefix for User NRIC as it's predefined

    /**
     * Generates a unique ID for a Project.
     * 
     * @return A unique project ID string (e.g.,
     *         "PROJ-f47ac10b-58cc-4372-a567-0e02b2c3d479").
     */
    public static String generateProjectId() {
        return PROJECT_PREFIX + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique ID for a Project Flat Info.
     * 
     * @return A unique project flat info ID string (e.g.,
     *         "FLAT-f47ac10b-58cc-4372-a567-0e02b2c3d479").
     */
    public static String generateProjectFlatInfoId() {
        return PROJECT_FLAT_INFO_PREFIX + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique ID for an Application.
     * 
     * @return A unique application ID string (e.g.,
     *         "APP-f47ac10b-58cc-4372-a567-0e02b2c3d479").
     */
    public static String generateApplicationId() {
        return APPLICATION_PREFIX + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique ID for a Booking.
     * 
     * @return A unique booking ID string (e.g.,
     *         "BOOK-f47ac10b-58cc-4372-a567-0e02b2c3d479").
     */
    public static String generateBookingId() {
        return BOOKING_PREFIX + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique ID for an Enquiry.
     * 
     * @return A unique enquiry ID string (e.g.,
     *         "ENQ-f47ac10b-58cc-4372-a567-0e02b2c3d479").
     */
    public static String generateEnquiryId() {
        return ENQUIRY_PREFIX + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique ID for an Officer Registration request.
     * 
     * @return A unique officer registration ID string (e.g.,
     *         "OREG-f47ac10b-58cc-4372-a567-0e02b2c3d479").
     */
    public static String generateOfficerRegId() {
        return OFFICER_REG_PREFIX + UUID.randomUUID().toString();
    }
}