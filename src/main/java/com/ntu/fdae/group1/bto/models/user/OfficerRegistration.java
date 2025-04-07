package com.ntu.fdae.group1.bto.models.user; // Or your models/entities package

import com.ntu.fdae.group1.bto.enums.*;
import java.time.LocalDate;
import java.util.Objects;

public class OfficerRegistration {
    private final String registrationId;
    private final String officerNric;
    private final String projectId;
    private final LocalDate requestDate;
    private OfficerRegStatus status;

    /**
     * Constructor for OfficerRegistration.
     * Initializes status to PENDING.
     *
     * @param registrationId Unique ID for the registration.
     * @param officerNric    NRIC of the HDB Officer requesting registration.
     * @param projectId      ID of the Project the officer wants to register for.
     * @param requestDate    The date the registration request was made.
     */
    public OfficerRegistration(String registrationId, String officerNric, String projectId, LocalDate requestDate) {
        this.registrationId = Objects.requireNonNull(registrationId, "Registration ID cannot be null");
        this.officerNric = Objects.requireNonNull(officerNric, "Officer NRIC cannot be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID cannot be null");
        this.requestDate = Objects.requireNonNull(requestDate, "Request date cannot be null");
        this.status = OfficerRegStatus.PENDING; // Default status
    }

    // --- Getters ---
    public String getRegistrationId() {
        return registrationId;
    }

    public String getOfficerNric() {
        return officerNric;
    }

    public String getProjectId() {
        return projectId;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public OfficerRegStatus getStatus() {
        return status;
    }

    // --- Setter (as per UML) ---
    /**
     * Updates the status of the registration. Should typically be called by the OfficerRegistrationService.
     * @param newStatus The new status (PENDING, APPROVED, REJECTED).
     */
    public void setStatus(OfficerRegStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "Status cannot be null");
    }

    // --- equals, hashCode, toString (Optional but Recommended) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfficerRegistration that = (OfficerRegistration) o;
        return registrationId.equals(that.registrationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationId);
    }

    @Override
    public String toString() {
        return "OfficerRegistration{" +
               "registrationId='" + registrationId + '\'' +
               ", officerNric='" + officerNric + '\'' +
               ", projectId='" + projectId + '\'' +
               ", requestDate=" + requestDate +
               ", status=" + status +
               '}';
    }
}