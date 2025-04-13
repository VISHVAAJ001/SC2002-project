package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;
import com.ntu.fdae.group1.bto.repository.util.CsvRepositoryHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository implementation for managing OfficerRegistration entities in the
 * BTO Management System.
 * <p>
 * This class handles the persistence and retrieval of OfficerRegistration
 * records using
 * a CSV file as the backing store. It provides methods for finding
 * registrations by various
 * criteria and implements the standard repository operations defined in the
 * IOfficerRegistrationRepository interface.
 * </p>
 * <p>
 * The repository maintains an in-memory cache of registrations for quick access
 * while
 * ensuring that changes are persisted to the CSV file. It uses the
 * CsvRepositoryHelper
 * to handle the low-level file operations and serialization/deserialization.
 * </p>
 */
public class OfficerRegistrationRepository implements IOfficerRegistrationRepository {
    /**
     * Path to the CSV file where officer registration data is stored.
     */
    private static final String OFFICER_REGISTRATION_FILE_PATH = "resources/officer_registrations.csv";

    /**
     * CSV header columns for the officer registrations file.
     */
    private static final String[] REGISTRATION_CSV_HEADER = new String[] {
            "registrationId", "officerNric", "projectId", "requestDate", "status"
    };

    /**
     * In-memory cache of all officer registrations, indexed by registration ID.
     */
    private Map<String, OfficerRegistration> registrations;

    /**
     * Helper for CSV file operations, handling serialization and deserialization.
     */
    private final CsvRepositoryHelper<String, OfficerRegistration> csvHelper;

    /**
     * Constructs a new OfficerRegistrationRepository.
     * <p>
     * Initializes the CSV helper with appropriate serializers/deserializers and
     * loads the initial data from the CSV file. If loading fails, it starts with
     * an empty registration collection.
     * </p>
     */
    public OfficerRegistrationRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                OFFICER_REGISTRATION_FILE_PATH,
                REGISTRATION_CSV_HEADER,
                this::deserializeRegistrations, // Method reference
                this::serializeRegistrations // Method reference
        );
        // Load initial data
        try {
            this.registrations = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial officer registration load failed: " + e.getMessage());
            this.registrations = new HashMap<>(); // Start with empty map on failure
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the registration with the specified ID from the in-memory cache,
     * or null if no registration with that ID exists.
     * </p>
     */
    @Override
    public OfficerRegistration findById(String registrationId) {
        return registrations.get(registrationId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a defensive copy of the in-memory registration map to prevent
     * external modification of the repository's internal state.
     * </p>
     */
    @Override
    public Map<String, OfficerRegistration> findAll() {
        return new HashMap<>(registrations);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves a single registration to both the in-memory cache and the CSV file.
     * Validates that neither the registration nor its ID is null before saving.
     * </p>
     * 
     * @throws DataAccessException if there is an error writing to the CSV file
     */
    @Override
    public void save(OfficerRegistration registration) {
        if (registration == null || registration.getRegistrationId() == null) {
            System.err.println("Attempted to save null registration or registration with null ID");
            return;
        }
        registrations.put(registration.getRegistrationId(), registration);
        try {
            csvHelper.saveData(registrations);
        } catch (DataAccessException e) {
            System.err.println(
                    "Failed to save officer registration " + registration.getRegistrationId() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Replaces the entire in-memory registration collection with the provided map
     * and persists all registrations to the CSV file.
     * </p>
     * 
     * @throws DataAccessException if there is an error writing to the CSV file
     */
    @Override
    public void saveAll(Map<String, OfficerRegistration> entities) {
        this.registrations = new HashMap<>(entities);
        try {
            csvHelper.saveData(registrations);
        } catch (DataAccessException e) {
            System.err.println("Failed to save all officer registrations: " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reloads all registrations from the CSV file, refreshing the in-memory cache.
     * This is useful when external processes might have modified the CSV file.
     * </p>
     * 
     * @throws DataAccessException if there is an error reading from the CSV file
     */
    @Override
    public Map<String, OfficerRegistration> loadAll() throws DataAccessException {
        this.registrations = csvHelper.loadData();
        return new HashMap<>(registrations);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filters the in-memory registrations to find those associated with the
     * specified officer.
     * Uses Java 8 Stream API for efficient filtering.
     * </p>
     */
    @Override
    public List<OfficerRegistration> findByOfficerNric(String nric) {
        return registrations.values().stream()
                .filter(registration -> registration.getOfficerNric().equals(nric))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Filters the in-memory registrations to find those associated with the
     * specified project.
     * Uses Java 8 Stream API for efficient filtering.
     * </p>
     */
    @Override
    public List<OfficerRegistration> findByProjectId(String projectId) {
        return registrations.values().stream()
                .filter(registration -> registration.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    /**
     * Deserializes officer registration data from CSV rows into OfficerRegistration
     * objects.
     * <p>
     * This method handles potential data format issues and logs errors for
     * problematic rows
     * without throwing exceptions that would disrupt the entire loading process.
     * </p>
     * 
     * @param registrationData List of CSV row data arrays
     * @return Map of deserialized OfficerRegistration objects indexed by
     *         registration ID
     */
    private Map<String, OfficerRegistration> deserializeRegistrations(List<String[]> registrationData) {
        Map<String, OfficerRegistration> registrationMap = new HashMap<>();
        if (registrationData == null)
            return registrationMap;

        for (String[] row : registrationData) {
            if (row.length < 5) {
                System.err.println("Skipping invalid officer registration row: " + String.join(",", row));
                continue;
            }
            try {
                String registrationId = row[0];
                String officerNric = row[1];
                String projectId = row[2];
                LocalDate requestDate = FileUtil.parseLocalDate(row[3]);
                if (requestDate == null) {
                    System.err.println("Skipping registration row due to invalid request date: " + row[3]);
                    continue;
                }
                // Use parseEnum with a default value for robustness
                OfficerRegStatus status = FileUtil.parseEnum(OfficerRegStatus.class, row[4], OfficerRegStatus.PENDING);

                // Constructor creates with PENDING status by default if model is designed that
                // way,
                // otherwise, set it explicitly if the constructor doesn't handle status.
                OfficerRegistration registration = new OfficerRegistration(
                        registrationId,
                        officerNric,
                        projectId,
                        requestDate);
                registration.setStatus(status); // Set parsed status

                registrationMap.put(registrationId, registration);
            } catch (Exception e) {
                System.err.println(
                        "Error parsing officer registration row: " + String.join(",", row) + " - " + e.getMessage());
            }
        }
        return registrationMap;
    }

    /**
     * Serializes OfficerRegistration objects into CSV row format for storage.
     * <p>
     * This method handles potential null values defensively to ensure robust
     * serialization even with incomplete data.
     * </p>
     * 
     * @param regsToSerialize Map of OfficerRegistration objects to serialize
     * @return List of CSV row data arrays
     */
    private List<String[]> serializeRegistrations(Map<String, OfficerRegistration> regsToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
        if (regsToSerialize == null)
            return serializedData;

        for (OfficerRegistration registration : regsToSerialize.values()) {
            serializedData.add(new String[] {
                    registration.getRegistrationId(),
                    registration.getOfficerNric(),
                    registration.getProjectId(),
                    FileUtil.formatLocalDate(registration.getRequestDate()), // Util handles null
                    registration.getStatus() != null ? registration.getStatus().toString()
                            : OfficerRegStatus.PENDING.toString() // Handle null status defensively
            });
        }
        return serializedData;
    }
}