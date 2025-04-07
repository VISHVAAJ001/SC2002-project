package com.ntu.fdae.group1.bto.utils;

import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.enquiry.IEnquiryRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IOfficerRegistrationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for generating sequential IDs with a fixed prefix and
 * zero-padding.
 * IMPORTANT: The initialize() method MUST be called once at application startup
 * AFTER data repositories have been loaded to ensure ID uniqueness across
 * restarts.
 */
public final class IdGenerator {

    // --- Configuration ---
    private static final int ID_NUMBER_LENGTH = 3;
    private static final String PADDING_FORMAT = "%0" + ID_NUMBER_LENGTH + "d";

    // --- Prefixes ---
    private static final String PROJECT_PREFIX = "PROJ";
    private static final String FLAT_INFO_PREFIX = "FLAT";
    private static final String APPLICATION_PREFIX = "APP";
    private static final String BOOKING_PREFIX = "BOOK";
    private static final String ENQUIRY_PREFIX = "ENQ";
    private static final String REGISTRATION_PREFIX = "REG";

    // --- Counters (initialize to 1, but will be updated by initialize()) ---
    private static int nextProjectId = 1;
    private static int nextFlatInfoId = 1;
    private static int nextApplicationId = 1;
    private static int nextBookingId = 1;
    private static int nextEnquiryId = 1;
    private static int nextRegistrationId = 1;

    // --- Patterns for extracting numbers from existing IDs ---
    // Adjust pattern if prefix characters can appear in the number part (unlikely)
    private static final Pattern PROJECT_PATTERN = Pattern.compile("^" + PROJECT_PREFIX + "(\\d+)$");
    private static final Pattern FLAT_INFO_PATTERN = Pattern.compile("^" + FLAT_INFO_PREFIX + "(\\d+)$");
    private static final Pattern APPLICATION_PATTERN = Pattern.compile("^" + APPLICATION_PREFIX + "(\\d+)$");
    private static final Pattern BOOKING_PATTERN = Pattern.compile("^" + BOOKING_PREFIX + "(\\d+)$");
    private static final Pattern ENQUIRY_PATTERN = Pattern.compile("^" + ENQUIRY_PREFIX + "(\\d+)$");
    private static final Pattern REGISTRATION_PATTERN = Pattern.compile("^" + REGISTRATION_PREFIX + "(\\d+)$");

    /** Private constructor to prevent instantiation. */
    private IdGenerator() {
    }

    /**
     * Initialises the ID counters by scanning existing IDs from loaded
     * repositories.
     * This MUST be called once at startup after repositories are loaded to ensure
     * uniqueness across application executions.
     *
     * @param projectRepo      Loaded IProjectRepository
     * @param applicationRepo  Loaded IApplicationRepository
     * @param bookingRepo      Loaded IBookingRepository
     * @param enquiryRepo      Loaded IEnquiryRepository
     * @param registrationRepo Loaded IOfficerRegistrationRepository
     */
    public static synchronized void initialise(
            IProjectRepository projectRepo,
            IApplicationRepository applicationRepo,
            IBookingRepository bookingRepo,
            IEnquiryRepository enquiryRepo,
            IOfficerRegistrationRepository registrationRepo) {

        // System.out.println("Initializing ID Generators...");

        nextProjectId = findNextId(projectRepo.findAll().keySet(), PROJECT_PATTERN);
        nextFlatInfoId = findNextId(projectRepo.findAllFlatInfoIds(), FLAT_INFO_PATTERN);
        nextApplicationId = findNextId(applicationRepo.findAll().keySet(), APPLICATION_PATTERN);
        nextBookingId = findNextId(bookingRepo.findAll().keySet(), BOOKING_PATTERN);
        nextEnquiryId = findNextId(enquiryRepo.findAll().keySet(), ENQUIRY_PATTERN);
        nextRegistrationId = findNextId(registrationRepo.findAll().keySet(), REGISTRATION_PATTERN);
    }

    /**
     * Helper method to find the next available ID number based on existing IDs.
     *
     * @param existingIds Collection of existing IDs (e.g., from
     *                    repository.findAll().keySet()).
     * @param pattern     The regex Pattern to extract the numeric part of the ID.
     * @return The next integer ID to use (max found + 1, or 1 if none found).
     */
    private static int findNextId(Collection<String> existingIds, Pattern pattern) {
        int maxId = 0;
        if (existingIds != null) {
            for (String id : existingIds) {
                if (id == null)
                    continue;
                Matcher matcher = pattern.matcher(id);
                if (matcher.matches()) {
                    try {
                        int currentIdNum = Integer.parseInt(matcher.group(1));
                        if (currentIdNum > maxId) {
                            maxId = currentIdNum;
                        }
                    } catch (NumberFormatException e) {
                        // Log or ignore malformed ID
                        System.err.println("Warning: Malformed ID detected during initialization: " + id);
                    }
                } else {
                    // This should never happen, but if it does, we just put a large maxId
                    maxId = 9999; // This will force the next ID to be 10000
                }
            }
        }
        return maxId + 1; // Start from max + 1, or 1 if maxId remained 0
    }

    // --- Public Generator Methods ---
    public static synchronized String generateApplicationId() {
        String numberPart = String.format(PADDING_FORMAT, nextApplicationId++);
        return APPLICATION_PREFIX + numberPart;
    }

    public static synchronized String generateBookingId() {
        String numberPart = String.format(PADDING_FORMAT, nextBookingId++);
        return BOOKING_PREFIX + numberPart;
    }

    public static synchronized String generateEnquiryId() {
        String numberPart = String.format(PADDING_FORMAT, nextEnquiryId++);
        return ENQUIRY_PREFIX + numberPart;
    }

    public static synchronized String generateOfficerRegId() {
        String numberPart = String.format(PADDING_FORMAT, nextRegistrationId++);
        return REGISTRATION_PREFIX + numberPart;
    }

    public static synchronized String generateProjectId() {
        String numberPart = String.format(PADDING_FORMAT, nextProjectId++);
        return PROJECT_PREFIX + numberPart;
    }

    public static synchronized String generateFlatInfoId() {
        String numberPart = String.format(PADDING_FORMAT, nextFlatInfoId++);
        return FLAT_INFO_PREFIX + numberPart;
    }
}