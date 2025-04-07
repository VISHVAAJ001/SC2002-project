// ****** CORRECTION: Ensure this file is in the .repository.user package ******
package com.ntu.fdae.group1.bto.repository.user;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.user.OfficerRegistration;
import com.ntu.fdae.group1.bto.utils.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap; // Using ConcurrentHashMap for thread-safety
import java.util.stream.Collectors;

public class OfficerRegistrationRepository implements IOfficerRegistrationRepository {

    // ****** CORRECTION: Adjust path if needed ******
    private static final String OFFICER_REGISTRATION_FILE_PATH = "data/officer_registrations.csv"; // Ensure this path is correct relative to execution
    private static final String[] HEADERS = {"registrationId", "officerNric", "projectId", "requestDate", "status"};

    private final Map<String, OfficerRegistration> registrations; // Use final for the map reference

    public OfficerRegistrationRepository() {
        this.registrations = new ConcurrentHashMap<>(); // Use ConcurrentHashMap for thread safety
        try {
            // Load data during initialization
            loadAll();
        } catch (DataAccessException e) {
            System.err.println("WARN: Failed to load officer registrations on startup: " + e.getMessage());
            // Decide if application should proceed with an empty map or halt
        }
    }

    // ****** CORRECTION: Add @Override, ensure return type matches interface ******
    @Override
    public OfficerRegistration findById(String registrationId) {
        return registrations.get(registrationId);
    }

    // ****** CORRECTION: Add @Override, ensure return type matches interface ******
    @Override
    public Map<String, OfficerRegistration> findAll() {
        // Return a defensive copy to prevent external modification of the internal map
        return new ConcurrentHashMap<>(registrations);
    }

    // ****** CORRECTION: Add @Override, ensure parameter types match interface ******
    @Override
    public void save(OfficerRegistration registration) {
        // Add null checks for safety
        Objects.requireNonNull(registration, "Cannot save a null OfficerRegistration");
        Objects.requireNonNull(registration.getRegistrationId(), "OfficerRegistration ID cannot be null for saving");
        registrations.put(registration.getRegistrationId(), registration);
        // Decide on save strategy: immediately save all or have a separate persistence trigger
        saveAllInternal(); // Call internal save method
    }

    // Note: The interface expects Map<String, OfficerRegistration>, this implementation
    // replaces the internal map and saves it.
    @Override
    public void saveAll(Map<String, OfficerRegistration> entities) {
        // Replace the internal map completely and persist
         Objects.requireNonNull(entities, "Cannot save a null map of entities");
         // Use ConcurrentHashMap if multi-threaded access is possible
         this.registrations.clear();
         this.registrations.putAll(entities);
         saveAllInternal(); // Call internal save method
    }

    @Override
    public Map<String, OfficerRegistration> loadAll() throws DataAccessException {
        registrations.clear(); // Clear current map before loading
        try {
            // Ensure FileUtil path is correct and it handles file-not-found gracefully (e.g., return empty list)
            List<String[]> lines = FileUtil.readCsvLines(OFFICER_REGISTRATION_FILE_PATH);

            // Skip header line more reliably
            if (!lines.isEmpty() && isHeaderRow(lines.get(0), HEADERS)) {
                lines = lines.subList(1, lines.size());
            }

            for (String[] fields : lines) {
                if (fields.length >= HEADERS.length) { // Check against header length
                    String regId = fields[0];
                    String officerNric = fields[1];
                    String projectId = fields[2];
                    LocalDate requestDate = FileUtil.parseLocalDate(fields[3]); // Ensure FileUtil handles parse errors
                    OfficerRegStatus statusFromFile = FileUtil.parseEnum(OfficerRegStatus.class, fields[4]);
                    // If parsing fails (returns null), use the default PENDING status
                    OfficerRegStatus status = (statusFromFile != null) ? statusFromFile : OfficerRegStatus.PENDING;
                    // Add validation for required fields being non-null/empty
                    // Add validation for required fields being non-null/empty
                    if (regId != null && !regId.isBlank() &&
                        officerNric != null && !officerNric.isBlank() &&
                        projectId != null && !projectId.isBlank() &&
                        requestDate != null && status != null) // status should not be null now
                    {
                        OfficerRegistration reg = new OfficerRegistration(regId, officerNric, projectId, requestDate);
                        reg.setStatus(status); // Set status loaded from file or default
                        registrations.put(regId, reg);
                    } else {
                        System.err.println("WARN: Skipping invalid officer registration line: " + String.join(",", fields));
                        }
                } else {
                     System.err.println("WARN: Skipping malformed officer registration line (fields=" + fields.length + ", expected=" + HEADERS.length + "): " + String.join(",", fields));
                }
            }
             System.out.println("INFO: Loaded " + registrations.size() + " officer registrations from " + OFFICER_REGISTRATION_FILE_PATH);
        } catch (IOException e) {
            // If file doesn't exist on first load, treat as empty, don't throw error unless required
             System.err.println("INFO: Officer registrations file not found or failed to read, starting empty: " + OFFICER_REGISTRATION_FILE_PATH + " (" + e.getMessage() + ")");
             // Optional: throw new DataAccessException("Failed to load officer registrations from file: " + OFFICER_REGISTRATION_FILE_PATH, e);
        } catch (Exception e) { // Catch other potential parsing errors
             System.err.println("ERROR: Error parsing officer registration data: " + e.getMessage());
             // Optional: throw new DataAccessException("Error parsing officer registration data: " + e.getMessage(), e);
        }
        // Return a defensive copy
        return new ConcurrentHashMap<>(registrations);
    }

    @Override
    public List<OfficerRegistration> findByOfficerNric(String nric) {
        if (nric == null || nric.isBlank()) {
            return List.of(); // Return empty list for invalid input
        }
        return registrations.values().stream()
                .filter(registration -> nric.equals(registration.getOfficerNric()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OfficerRegistration> findByProjectId(String projectId) {
         if (projectId == null || projectId.isBlank()) {
            return List.of(); // Return empty list for invalid input
        }
        return registrations.values().stream()
                .filter(registration -> projectId.equals(registration.getProjectId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
     if (id != null && registrations.remove(id) != null) {
        saveAllInternal(); // Persist after removal
        System.out.println("INFO: Deleted OfficerRegistration with ID: " + id);
    } else {
        System.err.println("WARN: Attempted to delete non-existent OfficerRegistration with ID: " + id);
    }
}


    // --- Internal Helper Methods ---

    // Helper to check if the first row is a header
    private boolean isHeaderRow(String[] firstRow, String[] expectedHeaders) {
        return firstRow != null && expectedHeaders != null &&
               firstRow.length == expectedHeaders.length &&
               java.util.Arrays.equals(firstRow, expectedHeaders);
    }

    // Centralized method to save the current state to file
    private void saveAllInternal() {
        try {
            FileUtil.writeCsvLines(OFFICER_REGISTRATION_FILE_PATH, serializeRegistrations(), HEADERS);
        } catch (IOException e) {
            // Log error prominently, potentially throw an unchecked exception
            // as failure to save can lead to data loss.
            System.err.println("FATAL: Error saving officer registrations to file: " + OFFICER_REGISTRATION_FILE_PATH + " - " + e.getMessage());
            // Consider: throw new DataAccessException("Error saving officer registrations", e);
        }
    }

    private List<String[]> serializeRegistrations() {
        List<String[]> serializedData = new ArrayList<>();
        // No need for header here, writeCsvLines adds it

        for (OfficerRegistration registration : registrations.values()) {
            serializedData.add(new String[] {
                    registration.getRegistrationId(),
                    registration.getOfficerNric(),
                    registration.getProjectId(),
                    FileUtil.formatLocalDate(registration.getRequestDate()), // Ensure FileUtil handles null dates
                    registration.getStatus().name() // Use name() for enum serialization
            });
        }
        return serializedData;
    }

     // Deserialization is handled within loadAll now
    // private Map<String, OfficerRegistration> deserializeRegistrations(List<String[]> registrationData) { ... }

}