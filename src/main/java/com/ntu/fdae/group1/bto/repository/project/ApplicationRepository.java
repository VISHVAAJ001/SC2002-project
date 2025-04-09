package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationRepository implements IApplicationRepository {
    private static final String APPLICATION_FILE_PATH = "resources/applications.csv";

    private Map<String, Application> applications;

    public ApplicationRepository() {
        this.applications = new HashMap<>();
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
        applications.put(application.getApplicationId(), application);
        saveAll(applications);
    }

    @Override
    public void saveAll(Map<String, Application> entities) {
        this.applications = entities;
        try {
            FileUtil.writeCsvLines(APPLICATION_FILE_PATH, serializeApplications(), getApplicationCsvHeader());
        } catch (IOException e) {
            throw new DataAccessException("Error saving applications to file: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Application> loadAll() throws DataAccessException {
        try {
            List<String[]> applicationData = FileUtil.readCsvLines(APPLICATION_FILE_PATH);
            applications = deserializeApplications(applicationData);
        } catch (IOException e) {
            throw new DataAccessException("Error loading applications from file: " + e.getMessage(), e);
        }
        return applications;
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

    // Helper methods for serialization/deserialization
    private String[] getApplicationCsvHeader() {
        return new String[] {
                "applicationId", "applicantNric", "projectId", "submissionDate",
                "status", "requestedWithdrawalDate", "preferredFlatType"
        };
    }

    private Map<String, Application> deserializeApplications(List<String[]> applicationData) {
        Map<String, Application> applicationMap = new HashMap<>();

        if (applicationData == null || applicationData.isEmpty()) {
            return applicationMap;
        }

        for (String[] row : applicationData) {
            if (row.length < 5)
                continue; // Skip invalid rows

            try {
                String applicationId = row[0];
                String applicantNric = row[1];
                String projectId = row[2];
                LocalDate submissionDate = FileUtil.parseLocalDate(row[3]);
                ApplicationStatus status = FileUtil.parseEnum(ApplicationStatus.class, row[4]);

                // Create the application
                Application application = new Application(applicationId, applicantNric, projectId, submissionDate);
                application.setStatus(status);

                // Set withdrawal date if exists
                if (row[5] != null && !row[5].trim().isEmpty()) {
                    LocalDate withdrawalDate = FileUtil.parseLocalDate(row[5]);
                    if (withdrawalDate != null) {
                        application.setRequestedWithdrawalDate(withdrawalDate);
                    }
                }

                // Set preferred flat type if exists
                if (row.length > 6 && row[6] != null && !row[6].trim().isEmpty()) {
                    FlatType preferredFlatType = FileUtil.parseEnum(FlatType.class, row[6]);
                    if (preferredFlatType != null) {
                        application.setPreferredFlatType(preferredFlatType);
                    }
                }

                applicationMap.put(applicationId, application);
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing application data: " + e.getMessage());
            }
        }

        return applicationMap;
    }

    private List<String[]> serializeApplications() {
        List<String[]> serializedData = new ArrayList<>();

        for (Application application : applications.values()) {
            String withdrawalDate = "";
            if (application.getRequestedWithdrawalDate() != null) {
                withdrawalDate = FileUtil.formatLocalDate(application.getRequestedWithdrawalDate());
            }

            String flatType = "";
            if (application.getPreferredFlatType() != null) {
                flatType = application.getPreferredFlatType().toString();
            }

            serializedData.add(new String[] {
                    application.getApplicationId(),
                    application.getApplicantNric(),
                    application.getProjectId(),
                    FileUtil.formatLocalDate(application.getSubmissionDate()),
                    application.getStatus().toString(),
                    withdrawalDate,
                    flatType
            });
        }

        return serializedData;
    }
}