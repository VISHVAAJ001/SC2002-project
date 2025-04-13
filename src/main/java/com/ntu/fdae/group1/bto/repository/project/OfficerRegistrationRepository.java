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

public class OfficerRegistrationRepository implements IOfficerRegistrationRepository {
    private static final String OFFICER_REGISTRATION_FILE_PATH = "resources/officer_registrations.csv";
    private static final String[] REGISTRATION_CSV_HEADER = new String[] {
        "registrationId", "officerNric", "projectId", "requestDate", "status"
    };

    private Map<String, OfficerRegistration> registrations;
    private final CsvRepositoryHelper<String, OfficerRegistration> csvHelper; 

    public OfficerRegistrationRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                OFFICER_REGISTRATION_FILE_PATH,
                REGISTRATION_CSV_HEADER,
                this::deserializeRegistrations, // Method reference
                this::serializeRegistrations   // Method reference
        );
        // Load initial data
        try {
            this.registrations = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial officer registration load failed: " + e.getMessage());
            this.registrations = new HashMap<>(); // Start with empty map on failure
        }
    }

    @Override
    public OfficerRegistration findById(String registrationId) {
        return registrations.get(registrationId);
    }

    @Override
    public Map<String, OfficerRegistration> findAll() {
        return new HashMap<>(registrations);
    }

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
           System.err.println("Failed to save officer registration " + registration.getRegistrationId() + ": " + e.getMessage());
           throw e;
       }
    }

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

    @Override
    public Map<String, OfficerRegistration> loadAll() throws DataAccessException {
        this.registrations = csvHelper.loadData();
        return new HashMap<>(registrations);
    }

    @Override
    public List<OfficerRegistration> findByOfficerNric(String nric) {
        return registrations.values().stream()
                .filter(registration -> registration.getOfficerNric().equals(nric))
                .collect(Collectors.toList());
    }

    @Override
    public List<OfficerRegistration> findByProjectId(String projectId) {
        return registrations.values().stream()
                .filter(registration -> registration.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    private Map<String, OfficerRegistration> deserializeRegistrations(List<String[]> registrationData) {
        Map<String, OfficerRegistration> registrationMap = new HashMap<>();
        if (registrationData == null) return registrationMap;

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

                // Constructor creates with PENDING status by default if model is designed that way,
                // otherwise, set it explicitly if the constructor doesn't handle status.
                OfficerRegistration registration = new OfficerRegistration(
                        registrationId,
                        officerNric,
                        projectId,
                        requestDate);
                registration.setStatus(status); // Set parsed status

                registrationMap.put(registrationId, registration);
            } catch (Exception e) {
                System.err.println("Error parsing officer registration row: " + String.join(",", row) + " - " + e.getMessage());
            }
        }
        return registrationMap;
    }

    private List<String[]> serializeRegistrations(Map<String, OfficerRegistration> regsToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
         if (regsToSerialize == null) return serializedData;

        for (OfficerRegistration registration : regsToSerialize.values()) {
            serializedData.add(new String[] {
                    registration.getRegistrationId(),
                    registration.getOfficerNric(),
                    registration.getProjectId(),
                    FileUtil.formatLocalDate(registration.getRequestDate()), // Util handles null
                    registration.getStatus() != null ? registration.getStatus().toString() : OfficerRegStatus.PENDING.toString() // Handle null status defensively
            });
        }
        return serializedData;
    }
}