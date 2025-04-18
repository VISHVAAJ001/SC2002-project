package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
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
 * Implementation of the IApplicationRepository interface that persists
 * Application entities
 * to a CSV file.
 * <p>
 * This repository manages application data, providing CRUD operations and
 * specialized
 * queries for application management in the BTO system. It uses a CSV file as
 * the
 * persistent storage mechanism, with in-memory caching for efficient access.
 * </p>
 * <p>
 * The repository delegates CSV file operations to a CsvRepositoryHelper and
 * handles
 * the conversion between Application objects and their CSV representation.
 * </p>
 */
public class ApplicationRepository implements IApplicationRepository {
    /**
     * Path to the CSV file where application data is stored.
     */
    private static final String APPLICATION_FILE_PATH = "data/applications.csv";

    /**
     * Header row defining the columns in the application CSV file.
     * The order of elements must match the order used in serialization methods.
     */
    private static final String[] APPLICATION_CSV_HEADER = new String[] {
            "applicationId", "applicantNric", "projectId", "submissionDate",
            "status", "requestedWithdrawalDate", "preferredFlatType"
    };

    /**
     * In-memory cache of applications, keyed by application ID.
     * This improves performance by reducing the need for repeated file I/O.
     */
    private Map<String, Application> applications;

    /**
     * Helper that handles CSV file operations for application data.
     */
    private final CsvRepositoryHelper<String, Application> csvHelper;

    /**
     * Constructs a new ApplicationRepository.
     * <p>
     * Initializes the repository with a CsvRepositoryHelper configured for
     * Application entities and attempts to load existing application data
     * from the CSV file. If the initial data load fails, an empty application
     * collection is created.
     * </p>
     */
    public ApplicationRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                APPLICATION_FILE_PATH,
                APPLICATION_CSV_HEADER,
                this::deserializeApplications,
                this::serializeApplications);
        try {
            this.applications = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial load failed: " + e.getMessage());
            this.applications = new HashMap<>();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves an application by its unique identifier from the in-memory cache.
     * Returns null if no application exists with the specified ID.
     * </p>
     */
    @Override
    public Application findById(String applicationId) {
        return applications.get(applicationId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a defensive copy of the applications map to prevent external
     * modification of the repository's internal state.
     * </p>
     */
    @Override
    public Map<String, Application> findAll() {
        return new HashMap<>(applications);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves an application to both the in-memory cache and the CSV file.
     * If the application or its ID is null, the method logs an error and returns
     * without saving.
     * </p>
     * <p>
     * The method updates the in-memory cache first, then delegates the persistence
     * to the CSV helper. If saving fails, the exception is logged and rethrown.
     * </p>
     */
    @Override
    public void save(Application application) {
        if (application == null || application.getApplicationId() == null) {
            System.err.println("Attempted to save null application or application with null ID");
            return;
        }
        // Modify in-memory map first
        applications.put(application.getApplicationId(), application);
        // Delegate saving the entire map to the helper
        try {
            csvHelper.saveData(applications);
        } catch (DataAccessException e) {
            System.err.println("Failed to save application " + application.getApplicationId() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the in-memory application collection with the provided map and
     * persists all applications to the CSV file. Creates a defensive copy of the
     * provided map to maintain repository encapsulation.
     * </p>
     * <p>
     * If saving fails, the exception is logged and rethrown, but the in-memory
     * state will already reflect the new entities.
     * </p>
     */
    @Override
    public void saveAll(Map<String, Application> entities) {
        // Replace in-memory map
        this.applications = new HashMap<>(entities);
        // Delegate saving to the helper
        try {
            csvHelper.saveData(applications);
        } catch (DataAccessException e) {
            System.err.println("Failed to save all applications: " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reloads all application data from the CSV file into the in-memory cache,
     * replacing any existing data. Returns a defensive copy of the loaded
     * applications.
     * </p>
     */
    @Override
    public Map<String, Application> loadAll() throws DataAccessException {
        this.applications = csvHelper.loadData();
        return new HashMap<>(applications); // Return a copy
    }

    /**
     * {@inheritDoc}
     * <p>
     * Searches for an application submitted by the specified applicant.
     * This method assumes an applicant can have only one active application
     * in the system at a time, returning the first match found or null if
     * no application exists for the applicant.
     * </p>
     * <p>
     * The search is performed on the in-memory cache for optimal performance.
     * </p>
     * 
     * @param nric The NRIC of the applicant to search for
     * @return The application associated with the specified applicant, or null if
     *         not found
     */
    @Override
    public Application findByApplicantNric(String nric) {
        for (Application application : applications.values()) {
            if (application.getApplicantNric().equals(nric)) {
                return application;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves all applications for a specific project.
     * This method filters the in-memory application collection to find all
     * applications associated with the given project ID, which is useful for
     * project management and selection processes.
     * </p>
     * <p>
     * The method uses Java 8 Stream API for efficient filtering.
     * </p>
     * 
     * @param projectId The ID of the project to filter by
     * @return A list of applications for the specified project, or an empty list if
     *         none exist
     */
    @Override
    public List<Application> findByProjectId(String projectId) {
        return applications.values().stream()
                .filter(app -> app.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves all applications with a specific status.
     * This method filters the in-memory application collection to find all
     * applications with the given status (e.g., PENDING, APPROVED, REJECTED),
     * which is useful for batch processing and status reporting.
     * </p>
     * <p>
     * The method uses Java 8 Stream API for efficient filtering.
     * </p>
     * 
     * @param status The application status to filter by
     * @return A list of applications with the specified status, or an empty list if
     *         none exist
     */
    @Override
    public List<Application> findByStatus(ApplicationStatus status) {
        return applications.values().stream()
                .filter(app -> app.getStatus() == status)
                .collect(Collectors.toList());
    }

    // --- Serialization/Deserialization Logic (Specific to Application) ---
    // These methods are now private and used by the helper via method references.

    /**
     * Deserializes CSV data into Application objects.
     * <p>
     * This method converts each CSV row into an Application object, handling
     * validation and parsing of various field types including dates and enums.
     * It provides extensive error checking and gracefully handles invalid data.
     * </p>
     * 
     * @param applicationData List of string arrays representing application data
     *                        from CSV
     * @return A map of deserialized Application objects, keyed by their IDs
     */
    private Map<String, Application> deserializeApplications(List<String[]> applicationData) {
        Map<String, Application> applicationMap = new HashMap<>();
        if (applicationData == null)
            return applicationMap; // Handle null data

        // Skip header row if present (FileUtil.readCsvLines usually handles this
        // depending on implementation)
        // Assuming FileUtil doesn't return the header
        for (String[] row : applicationData) {
            if (row.length < 5) {
                System.err.println("Skipping invalid application row: " + String.join(",", row));
                continue;
            }

            try {
                String applicationId = row[0];
                String applicantNric = row[1];
                String projectId = row[2];
                LocalDate submissionDate = FileUtil.parseLocalDate(row[3]);
                if (submissionDate == null) { // Handle parsing failure
                    System.err.println("Skipping application row due to invalid submission date: " + row[3]);
                    continue;
                }
                ApplicationStatus status = FileUtil.parseEnum(ApplicationStatus.class, row[4],
                        ApplicationStatus.PENDING);

                Application application = new Application(applicationId, applicantNric, projectId, submissionDate);
                application.setStatus(status);

                // Optional fields
                if (row.length > 5 && row[5] != null && !row[5].trim().isEmpty()) {
                    application.setRequestedWithdrawalDate(FileUtil.parseLocalDate(row[5]));
                }

                if (row.length > 6 && row[6] != null && !row[6].trim().isEmpty()) {
                    application.setPreferredFlatType(FileUtil.parseEnum(FlatType.class, row[6], null));
                }

                applicationMap.put(applicationId, application);
            } catch (Exception e) { // Catch broader exceptions during parsing/creation
                System.err.println("Error parsing application row: " + String.join(",", row) + " - " + e.getMessage());
            }
        }
        return applicationMap;
    }

    /**
     * Serializes Application objects into CSV format for persistence.
     * <p>
     * This method converts each Application object into a string array suitable for
     * writing to a CSV file. It handles null values safely and ensures all required
     * fields are properly formatted.
     * </p>
     * 
     * @param appsToSerialize Map of Application objects to serialize, keyed by ID
     * @return List of string arrays representing applications in CSV format
     */
    private List<String[]> serializeApplications(Map<String, Application> appsToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
        if (appsToSerialize == null)
            return serializedData;

        for (Application application : appsToSerialize.values()) {
            serializedData.add(new String[] {
                    application.getApplicationId(),
                    application.getApplicantNric(),
                    application.getProjectId(),
                    FileUtil.formatLocalDate(application.getSubmissionDate()),
                    application.getStatus().toString(),
                    FileUtil.formatLocalDate(application.getRequestedWithdrawalDate()), // Util handles null
                    application.getPreferredFlatType() != null ? application.getPreferredFlatType().toString() : "" // Handle
                                                                                                                    // null
                                                                                                                    // enum
            });
        }
        return serializedData;
    }
}