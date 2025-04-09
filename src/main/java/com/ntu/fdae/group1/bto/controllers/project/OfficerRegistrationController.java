package com.ntu.fdae.group1.bto.controllers.project;

import java.util.List;
import java.util.Objects;
import java.util.Arrays;

import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.services.project.IOfficerRegistrationService;

/**
 * Controller for officer registration operations.
 * Acts as an intermediary between the UI layer and the Service layer,
 * handling input validation and orchestrating calls to the service.
 */
public class OfficerRegistrationController {
    private final IOfficerRegistrationService registrationService;

    /**
     * Constructs a new OfficerRegistrationController.
     * Dependencies (like the service) are typically injected.
     *
     * @param regService The registration service implementation to use. Must not be null.
     */
    public OfficerRegistrationController(IOfficerRegistrationService regService) {
        // Use Objects.requireNonNull to ensure the dependency is provided (fail-fast)
        this.registrationService = Objects.requireNonNull(regService, "IOfficerRegistrationService cannot be null");
    }

    /**
     * Handles an HDB Officer's request to register for a specific project.
     * Validates input and delegates the core logic, including eligibility checks,
     * to the registration service.
     *
     * @param officer   The HDBOfficer making the request. Must not be null.
     * @param projectId The ID of the project to register for. Must not be null or blank.
     * @return The created OfficerRegistration object (initially PENDING) if successful.
     * @throws RegistrationException if the officer is ineligible, the project doesn't exist,
     *                               the officer is already registered, or another service-level error occurs.
     * @throws IllegalArgumentException if officer or projectId is null/blank (programmer error).
     */
    public OfficerRegistration requestRegistration(HDBOfficer officer, String projectId) throws RegistrationException {
        // Input validation within the controller
        Objects.requireNonNull(officer, "Officer cannot be null for requestRegistration");
        if (projectId == null || projectId.trim().isEmpty()) {
            // Using IllegalArgumentException for invalid parameters passed to the controller method
            throw new IllegalArgumentException("Project ID cannot be null or blank for requestRegistration");
        }
        // Delegate the complex logic and business rule checks to the service layer
        // The service method is declared to throw RegistrationException for business rule failures
        return registrationService.requestProjectRegistration(officer, projectId);
    }

    /**
     * Handles an HDB Manager's review (approval or rejection) of a pending officer registration request.
     * Validates input and delegates the core logic, including authorization and state checks,
     * to the registration service.
     *
     * @param manager        The HDBManager performing the review. Must not be null.
     * @param registrationId The ID of the OfficerRegistration to review. Must not be null or blank.
     * @param approve        true to approve the registration, false to reject it.
     * @return true if the review action was successfully processed by the service.
     * @throws RegistrationException if the registration is not found, not pending, the manager lacks permission,
     *                               approval violates rules (e.g., slots full), or another service-level error occurs.
     * @throws IllegalArgumentException if manager or registrationId is null/blank (programmer error).
     */
    public boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve)
            throws RegistrationException {
        // Input validation within the controller
        Objects.requireNonNull(manager, "Manager cannot be null for reviewRegistration");
        if (registrationId == null || registrationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration ID cannot be null or blank for reviewRegistration");
        }
        // Delegate the complex logic and business rule checks to the service layer
        // The service method is declared to throw RegistrationException for business rule failures
        return registrationService.reviewRegistration(manager, registrationId, approve);
    }

    /**
     * Retrieves the current registration status for a specific officer regarding a specific project.
     *
     * @param officer   The HDBOfficer whose status is requested. Must not be null.
     * @param projectId The ID of the project. Must not be null or blank.
     * @return The OfficerRegStatus (PENDING, APPROVED, REJECTED), or null if no registration exists
     *         for this officer and project combination, or if input parameters are invalid.
     */
    public OfficerRegStatus getMyRegistrationStatus(HDBOfficer officer, String projectId) {
        if (officer == null || projectId == null || projectId.trim().isEmpty()) {
            System.err.println("Controller Warning: Officer and valid Project ID are required for getMyRegistrationStatus.");
            return null;
        }
        return registrationService.getRegistrationStatus(officer, projectId);
    }

    /**
     * Gets a list of all officer registrations currently in the PENDING state.
     * Typically requested by an HDB Manager.
     *
     * @param manager The HDBManager requesting the list. Must not be null (indicates authenticated context).
     * @return An immutable List of pending OfficerRegistration objects. Returns an empty list if none are pending.
     * @throws IllegalArgumentException if manager is null (programmer error - context missing).
     * @throws RuntimeException if an unexpected error occurs during retrieval in the service/repository layer.
     */
    public List<OfficerRegistration> getPendingRegistrations(HDBManager manager) {
        // Check for valid context - ensuring a manager is passed indicates authenticated access
        Objects.requireNonNull(manager, "HDBManager context cannot be null when calling getPendingRegistrations.");

        try {
            // Delegate to the service. Service method should not throw checked exceptions here.
            List<OfficerRegistration> pending = registrationService.getPendingRegistrations();
            // Return an immutable view or copy if desired, though Arrays.asList() might be sufficient if result isn't huge
            return pending != null ? List.copyOf(pending) : Arrays.asList();
        } catch (Exception e) {
            // Catch unexpected runtime errors from the service/repo layer
            System.err.println("Controller ERROR: Failed to retrieve pending registrations: " + e.getMessage());
            // Re-throw as a runtime exception to signal a system problem
            throw new RuntimeException("Failed to retrieve pending registrations due to an internal error.", e);
        }
    }

    /**
     * Gets all registrations (regardless of status) associated with a specific project.
     * Typically requested by an HDB Staff member (Manager or Officer).
     *
     * @param staff     The HDBStaff member requesting the list (provides context). Must not be null.
     * @param projectId ID of the project. Must not be null or blank.
     * @return An immutable List of OfficerRegistration objects for the project. Returns an empty list if none found or input is invalid.
     * @throws IllegalArgumentException if staff or projectId is null/blank (programmer error).
     * @throws RuntimeException if an unexpected error occurs during retrieval in the service/repository layer.
     */
    public List<OfficerRegistration> getProjectRegistrations(HDBStaff staff, String projectId) {
        // Input validation
        Objects.requireNonNull(staff, "Staff context cannot be null for getProjectRegistrations");
        if (projectId == null || projectId.trim().isEmpty()) {
            System.err.println("Controller Warning: Project ID cannot be null or blank for getProjectRegistrations.");
            return Arrays.asList(); // Return empty list for invalid project ID
        }

        try {
            // Delegate to the service. Service method should not throw checked exceptions here.
            List<OfficerRegistration> registrations = registrationService.getRegistrationsByProject(projectId);
            return registrations != null ? List.copyOf(registrations) : Arrays.asList();
        } catch (Exception e) {
            // Catch unexpected runtime errors from the service/repo layer
            System.err.println("Controller ERROR: Failed to retrieve project registrations for " + projectId + ": " + e.getMessage());
            // Re-throw as a runtime exception to signal a system problem
            throw new RuntimeException("Failed to retrieve project registrations due to an internal error.", e);
        }
    }
}
