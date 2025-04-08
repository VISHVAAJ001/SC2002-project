package com.ntu.fdae.group1.bto.repository.project; 

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil; 

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap; 
import java.util.stream.Collectors;

public class ApplicationRepository implements IApplicationRepository {

    private static final String APPLICATION_FILE_PATH = "data/applications.csv"; 
    private static final String[] HEADERS = {
            "applicationId", "applicantNric", "projectId", "submissionDate",
            "status", "requestedWithdrawalDate", "preferredFlatType"
    };

    private final Map<String, Application> applications;

    public ApplicationRepository() {
        // Use ConcurrentHashMap if multiple threads might access the repository
        this.applications = new ConcurrentHashMap<>();
        try {
            loadAll(); // Load data from file on startup
        } catch (DataAccessException e) {
            // Log the error but allow application to start with an empty repository
            System.err.println("WARN: Failed to load applications on startup: " + e.getMessage());
        }
    }

    @Override
    public Application findById(String applicationId) {
        return applications.get(applicationId);
    }

    @Override
    public Map<String, Application> findAll() {
        return new ConcurrentHashMap<>(applications);
    }

    @Override
    public void save(Application application) {
        Objects.requireNonNull(application, "Application cannot be null for saving");
        Objects.requireNonNull(application.getApplicationId(), "Application ID cannot be null for saving");
        applications.put(application.getApplicationId(), application);
        saveAllInternal(); // Persist changes immediately (or batch if preferred)
    }

    @Override
    public void saveAll(Map<String, Application> entities) {
        Objects.requireNonNull(entities, "Cannot save null map of applications");
        // Replace internal map content and persist
        this.applications.clear();
        this.applications.putAll(entities);
        saveAllInternal(); // Persist changes
    }

    @Override
    public Map<String, Application> loadAll() throws DataAccessException {
        applications.clear(); // Clear current data before loading
        try {
            List<String[]> lines = FileUtil.readCsvLines(APPLICATION_FILE_PATH); // Assumes FileUtil handles file-not-found
            // Skip header handled by FileUtil or check here if needed
            deserializeApplications(lines); 
            System.out.println("INFO: Loaded " + applications.size() + " applications from " + APPLICATION_FILE_PATH);
        } catch (IOException e) {
            System.err.println("INFO: Applications file not found or error reading: " + APPLICATION_FILE_PATH + " (" + e.getMessage() + "). Starting empty.");
            // Don't re-throw if file not found is acceptable on first run
            // Optional: throw new DataAccessException("Failed to load applications from file", e);
        } catch (Exception e) {
            // Catch potential parsing errors during deserialization
            System.err.println("ERROR: Failed to parse application data: " + e.getMessage());
            // Decide whether to clear potentially partially loaded data
             applications.clear();
            // Optionally re-throw or throw new DataAccessException
            // throw new DataAccessException("Error parsing application data", e);
        }
        return new ConcurrentHashMap<>(applications); // Return a defensive copy
    }

    @Override
    public void deleteById(String id) {
        Objects.requireNonNull(id, "Application ID cannot be null for deletion");
        Application removedApplication = applications.remove(id);

        if (removedApplication != null) {
            System.out.println("INFO: Removing Application with ID: " + id);
            saveAllInternal(); // Persist changes after removal
        } else {
            System.err.println("WARN: Attempted to delete non-existent Application with ID: " + id);
        }
    }

    @Override
    public Application findByApplicantNric(String nric) {
        if (nric == null || nric.isBlank()) {
            return null;
        }
        // Find the first application matching the NRIC
        return applications.values().stream()
                .filter(app -> nric.equals(app.getApplicantNric()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Application> findByProjectId(String projectId) {
        if (projectId == null || projectId.isBlank()) {
            return List.of(); // Return empty list for invalid input
        }
        return applications.values().stream()
                .filter(app -> projectId.equals(app.getProjectId()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the predefined CSV header row.
     * @return Array of header strings.
     */
    private String[] getApplicationCsvHeader() {
        return HEADERS;
    }

    /**
     * Converts raw CSV data (list of string arrays) into Application objects
     * and populates the internal 'applications' map.
     *
     * @param applicationData List of string arrays read from the CSV file (excluding header).
     */
    private void deserializeApplications(List<String[]> applicationData) {
        // Assumes 'applications' map is already cleared before calling this
        if (applicationData == null || applicationData.isEmpty()) {
            return; // Nothing to deserialize
        }

        for (String[] row : applicationData) {
            // Check minimum expected columns based on header
            if (row.length < HEADERS.length) {
                 System.err.println("WARN: Skipping malformed application line (fields=" + row.length + ", expected=" + HEADERS.length + "): " + String.join(",", row));
                continue;
            }

            try {
                String applicationId = row[0];
                String applicantNric = row[1];
                String projectId = row[2];
                LocalDate submissionDate = FileUtil.parseLocalDate(row[3]); // Assumes handles parse errors -> null
                ApplicationStatus status = FileUtil.parseEnum(ApplicationStatus.class, row[4]); // Assumes handles parse errors -> null
                LocalDate requestedWithdrawalDate = FileUtil.parseLocalDate(row[5]); // Can be null
                FlatType preferredFlatType = FileUtil.parseEnum(FlatType.class, row[6]); // Can be null

                // Basic validation for essential fields after parsing
                if (applicationId == null || applicationId.isBlank() ||
                    applicantNric == null || applicantNric.isBlank() ||
                    projectId == null || projectId.isBlank() ||
                    submissionDate == null || status == null)
                {
                    System.err.println("WARN: Skipping application line due to missing essential data: " + String.join(",", row));
                    continue;
                }

                // Create the application object using constructor
                Application application = new Application(applicationId, applicantNric, projectId, submissionDate);
                // Set fields that aren't set in constructor
                application.setStatus(status);
                application.setRequestedWithdrawalDate(requestedWithdrawalDate); // Use the setter
                application.setPreferredFlatType(preferredFlatType);       // Use the setter

                // Add to the map
                applications.put(applicationId, application);

            } catch (Exception e) { // Catch broader exceptions during processing a row
                System.err.println("ERROR: Failed to process application row: " + String.join(",", row) + " - " + e.getMessage());
                // Continue to next row
            }
        }
    }

    /**
     * Converts the current in-memory 'applications' map into a List of String arrays
     * suitable for writing to a CSV file.
     *
     * @return List of string arrays representing the application data.
     */
    private List<String[]> serializeApplications() {
        List<String[]> serializedData = new ArrayList<>();
        // Header is added by writeCsvLines

        for (Application application : applications.values()) {
            // Handle potentially null values gracefully for CSV output
            String withdrawalDateStr = (application.getRequestedWithdrawalDate() == null) ? "" : FileUtil.formatLocalDate(application.getRequestedWithdrawalDate());
            String flatTypeStr = (application.getPreferredFlatType() == null) ? "" : application.getPreferredFlatType().name(); // Use .name() for consistency

            serializedData.add(new String[] {
                    application.getApplicationId(),
                    application.getApplicantNric(),
                    application.getProjectId(),
                    FileUtil.formatLocalDate(application.getSubmissionDate()),
                    application.getStatus().name(), // Use .name() for consistency
                    withdrawalDateStr,
                    flatTypeStr
            });
        }
        return serializedData;
    }

    /**
     * Internal helper method to save the current state of the 'applications' map to the CSV file.
     */
    private void saveAllInternal() {
         try {
             List<String[]> data = serializeApplications();
             FileUtil.writeCsvLines(APPLICATION_FILE_PATH, data, getApplicationCsvHeader());
         } catch (IOException e) {
             // Log error prominently, potentially throw an unchecked exception
             System.err.println("FATAL: Error saving applications to file: " + APPLICATION_FILE_PATH + " - " + e.getMessage());
             // Consider: throw new DataAccessException("Error saving applications", e);
         }
    }
}