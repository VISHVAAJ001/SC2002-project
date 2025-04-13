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

public class ApplicationRepository implements IApplicationRepository {
    private static final String APPLICATION_FILE_PATH = "resources/applications.csv";

    private static final String[] APPLICATION_CSV_HEADER = new String[] {
        "applicationId", "applicantNric", "projectId", "submissionDate",
        "status", "requestedWithdrawalDate", "preferredFlatType"
    };

    private Map<String, Application> applications;
    private final CsvRepositoryHelper<String, Application> csvHelper;

    public ApplicationRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                APPLICATION_FILE_PATH,
                APPLICATION_CSV_HEADER,
                this::deserializeApplications,
                this::serializeApplications
        );
        try {
            this.applications = this.csvHelper.loadData();
         } catch (DataAccessException e) {
            System.err.println("Initial load failed: " + e.getMessage());
            this.applications = new HashMap<>();
         }
    }

    @Override
    public Application findById(String applicationId) {
        return applications.get(applicationId);
    }

    @Override
    public Map<String, Application> findAll() {
        return new HashMap<>(applications);
    }

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

    @Override
    public Map<String, Application> loadAll() throws DataAccessException {
        this.applications = csvHelper.loadData();
        return new HashMap<>(applications); // Return a copy
    }

    @Override
    public Application findByApplicantNric(String nric) {
        for (Application application : applications.values()) {
            if (application.getApplicantNric().equals(nric)) {
                return application;
            }
        }
        return null;
    }

    @Override
    public List<Application> findByProjectId(String projectId) {
        return applications.values().stream()
                .filter(app -> app.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> findByStatus(ApplicationStatus status) {
        return applications.values().stream()
                .filter(app -> app.getStatus() == status)
                .collect(Collectors.toList());
    }

    // --- Serialization/Deserialization Logic (Specific to Application) ---
    // These methods are now private and used by the helper via method references.

    private Map<String, Application> deserializeApplications(List<String[]> applicationData) {
        Map<String, Application> applicationMap = new HashMap<>();
        if (applicationData == null) return applicationMap; // Handle null data

        // Skip header row if present (FileUtil.readCsvLines usually handles this depending on implementation)
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
                ApplicationStatus status = FileUtil.parseEnum(ApplicationStatus.class, row[4], ApplicationStatus.PENDING); 

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

    private List<String[]> serializeApplications(Map<String, Application> appsToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
        if (appsToSerialize == null) return serializedData;

        for (Application application : appsToSerialize.values()) {
            serializedData.add(new String[] {
                    application.getApplicationId(),
                    application.getApplicantNric(),
                    application.getProjectId(),
                    FileUtil.formatLocalDate(application.getSubmissionDate()),
                    application.getStatus().toString(),
                    FileUtil.formatLocalDate(application.getRequestedWithdrawalDate()), // Util handles null
                    application.getPreferredFlatType() != null ? application.getPreferredFlatType().toString() : "" // Handle null enum
            });
        }
        return serializedData;
    }
}