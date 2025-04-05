package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OfficerRegistrationRepository implements IOfficerRegistrationRepository {
    private static final String OFFICER_REGISTRATION_FILE_PATH = "data/officer_registrations.csv";

    private Map<String, OfficerRegistration> registrations;

    public OfficerRegistrationRepository() {
        this.registrations = new HashMap<>();
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
        registrations.put(registration.getRegistrationId(), registration);
        saveAll(registrations);
    }

    @Override
    public void saveAll(Map<String, OfficerRegistration> entities) {
        this.registrations = entities;
        try {
            FileUtils.writeCsvLines(OFFICER_REGISTRATION_FILE_PATH, serializeRegistrations(),
                    getRegistrationCsvHeader());
        } catch (IOException e) {
            throw new DataAccessException("Error saving officer registrations to file: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, OfficerRegistration> loadAll() throws DataAccessException {
        try {
            List<String[]> registrationData = FileUtils.readCsvLines(OFFICER_REGISTRATION_FILE_PATH);
            registrations = deserializeRegistrations(registrationData);
        } catch (IOException e) {
            throw new DataAccessException("Error loading officer registrations from file: " + e.getMessage(), e);
        }
        return registrations;
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

    // Helper methods for serialization/deserialization
    private String[] getRegistrationCsvHeader() {
        return new String[] {
                "registrationId", "officerNric", "projectId", "requestDate", "status"
        };
    }

    private Map<String, OfficerRegistration> deserializeRegistrations(List<String[]> registrationData) {
        Map<String, OfficerRegistration> registrationMap = new HashMap<>();

        if (registrationData == null || registrationData.isEmpty()) {
            return registrationMap;
        }

        for (String[] row : registrationData) {
            if (row.length < 5)
                continue; // Skip invalid rows

            try {
                String registrationId = row[0];
                String officerNric = row[1];
                String projectId = row[2];
                LocalDate requestDate = FileUtils.parseLocalDate(row[3]);
                OfficerRegStatus status = FileUtils.parseEnum(OfficerRegStatus.class, row[4]);

                // Create the registration
                OfficerRegistration registration = new OfficerRegistration(
                        registrationId,
                        officerNric,
                        projectId,
                        requestDate);

                // Set status
                registration.setStatus(status);

                registrationMap.put(registrationId, registration);
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing officer registration data: " + e.getMessage());
            }
        }

        return registrationMap;
    }

    private List<String[]> serializeRegistrations() {
        List<String[]> serializedData = new ArrayList<>();

        for (OfficerRegistration registration : registrations.values()) {
            serializedData.add(new String[] {
                    registration.getRegistrationId(),
                    registration.getOfficerNric(),
                    registration.getProjectId(),
                    FileUtils.formatLocalDate(registration.getRequestDate()),
                    registration.getStatus().toString()
            });
        }

        return serializedData;
    }
}