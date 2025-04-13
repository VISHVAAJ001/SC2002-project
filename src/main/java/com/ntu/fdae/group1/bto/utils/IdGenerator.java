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
 * <p>
 * This class provides thread-safe generation of unique, sequential identifiers
 * for various entities in the BTO Management System. Each ID consists of a
 * entity-specific prefix followed by a zero-padded sequential number.
 * </p>
 * <p>
 * The class maintains separate counters for each entity type and ensures that
 * IDs remain unique across application restarts by examining existing IDs in
 * the repositories during initialization.
 * </p>
 * <p>
 * IMPORTANT: The initialize() method MUST be called once at application startup
 * AFTER data repositories have been loaded to ensure ID uniqueness across
 * restarts.
 * </p>
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

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class provides only static utility methods and should not be
     * instantiated.
     * </p>
     */
    private IdGenerator() {
    }

    /**
     * Initialises the ID counters by scanning existing IDs from loaded
     * repositories.
     * <p>
     * This method examines all existing IDs in the provided repositories and
     * sets each ID counter to one more than the highest existing ID of that type.
     * This ensures that new IDs will not collide with existing ones, even after
     * application restarts.
     * </p>
     * <p>
     * This MUST be called once at startup after repositories are loaded to ensure
     * uniqueness across application executions.
     * </p>
     *
     * @param projectRepo      Loaded IProjectRepository containing existing project
     *                         IDs
     * @param applicationRepo  Loaded IApplicationRepository containing existing
     *                         application IDs
     * @param bookingRepo      Loaded IBookingRepository containing existing booking
     *                         IDs
     * @param enquiryRepo      Loaded IEnquiryRepository containing existing enquiry
     *                         IDs
     * @param registrationRepo Loaded IOfficerRegistrationRepository containing
     *                         existing registration IDs
     */
    public static synchronized void initialise(
            IProjectRepository projectRepo,
            IApplicationRepository applicationRepo,
            IBookingRepository bookingRepo,
            IEnquiryRepository enquiryRepo,
            IOfficerRegistrationRepository registrationRepo) {

        nextProjectId = findNextId(projectRepo.findAll().keySet(), PROJECT_PATTERN);
        nextFlatInfoId = findNextId(projectRepo.findAllFlatInfoIds(), FLAT_INFO_PATTERN);
        nextApplicationId = findNextId(applicationRepo.findAll().keySet(), APPLICATION_PATTERN);
        nextBookingId = findNextId(bookingRepo.findAll().keySet(), BOOKING_PATTERN);
        nextEnquiryId = findNextId(enquiryRepo.findAll().keySet(), ENQUIRY_PATTERN);
        nextRegistrationId = findNextId(registrationRepo.findAll().keySet(), REGISTRATION_PATTERN);
    }

    /**
     * Helper method to find the next available ID number based on existing IDs.
     * <p>
     * This method examines a collection of existing IDs, extracts their numeric
     * parts using the provided pattern, and finds the maximum value. It then
     * returns one more than this maximum, ensuring the next generated ID will
     * be unique.
     * </p>
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

    /**
     * Generates a new unique application ID.
     * <p>
     * The ID consists of the prefix "APP" followed by a zero-padded sequential
     * number.
     * This method is thread-safe and atomically increments the counter.
     * </p>
     *
     * @return A new unique application ID string (e.g., "APP001", "APP002")
     */
    public static synchronized String generateApplicationId() {
        String numberPart = String.format(PADDING_FORMAT, nextApplicationId++);
        return APPLICATION_PREFIX + numberPart;
    }

    /**
     * Generates a new unique booking ID.
     * <p>
     * The ID consists of the prefix "BOOK" followed by a zero-padded sequential
     * number.
     * This method is thread-safe and atomically increments the counter.
     * </p>
     *
     * @return A new unique booking ID string (e.g., "BOOK001", "BOOK002")
     */
    public static synchronized String generateBookingId() {
        String numberPart = String.format(PADDING_FORMAT, nextBookingId++);
        return BOOKING_PREFIX + numberPart;
    }

    /**
     * Generates a new unique enquiry ID.
     * <p>
     * The ID consists of the prefix "ENQ" followed by a zero-padded sequential
     * number.
     * This method is thread-safe and atomically increments the counter.
     * </p>
     *
     * @return A new unique enquiry ID string (e.g., "ENQ001", "ENQ002")
     */
    public static synchronized String generateEnquiryId() {
        String numberPart = String.format(PADDING_FORMAT, nextEnquiryId++);
        return ENQUIRY_PREFIX + numberPart;
    }

    /**
     * Generates a new unique officer registration ID.
     * <p>
     * The ID consists of the prefix "REG" followed by a zero-padded sequential
     * number.
     * This method is thread-safe and atomically increments the counter.
     * </p>
     *
     * @return A new unique registration ID string (e.g., "REG001", "REG002")
     */
    public static synchronized String generateOfficerRegId() {
        String numberPart = String.format(PADDING_FORMAT, nextRegistrationId++);
        return REGISTRATION_PREFIX + numberPart;
    }

    /**
     * Generates a new unique project ID.
     * <p>
     * The ID consists of the prefix "PROJ" followed by a zero-padded sequential
     * number.
     * This method is thread-safe and atomically increments the counter.
     * </p>
     *
     * @return A new unique project ID string (e.g., "PROJ001", "PROJ002")
     */
    public static synchronized String generateProjectId() {
        String numberPart = String.format(PADDING_FORMAT, nextProjectId++);
        return PROJECT_PREFIX + numberPart;
    }

    /**
     * Generates a new unique flat information ID.
     * <p>
     * The ID consists of the prefix "FLAT" followed by a zero-padded sequential
     * number.
     * This method is thread-safe and atomically increments the counter.
     * </p>
     *
     * @return A new unique flat information ID string (e.g., "FLAT001", "FLAT002")
     */
    public static synchronized String generateFlatInfoId() {
        String numberPart = String.format(PADDING_FORMAT, nextFlatInfoId++);
        return FLAT_INFO_PREFIX + numberPart;
    }
}