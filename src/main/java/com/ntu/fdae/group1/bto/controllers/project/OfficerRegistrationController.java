package com.ntu.fdae.group1.bto.controllers.project;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.services.project.IOfficerRegistrationService;
import com.ntu.fdae.group1.bto.services.project.IProjectService;

/**
 * Controller for officer registration operations.
 * Acts as an intermediary between the UI layer and the Service layer,
 * handling input validation and orchestrating calls to the service.
 */
public class OfficerRegistrationController {
    private final IOfficerRegistrationService registrationService;
    private final IProjectService projectService;

    /**
     * Constructs a new OfficerRegistrationController.
     * Dependencies (like the service) are typically injected.
     *
     * @param regService The registration service implementation to use. Must not be
     *                   null.
     */
    public OfficerRegistrationController(IOfficerRegistrationService regService, IProjectService projService) {
        this.registrationService = regService;
        this.projectService = projService;
    }

    /**
     * Handles an HDB Officer's request to register for a specific project.
     * Validates input and delegates the core logic, including eligibility checks,
     * to the registration service.
     *
     * @param officer   The HDBOfficer making the request. Must not be null.
     * @param projectId The ID of the project to register for. Must not be null or
     *                  blank.
     * @return The created OfficerRegistration object (initially PENDING) if
     *         successful.
     * @throws RegistrationException    if the officer is ineligible, the project
     *                                  doesn't exist,
     *                                  the officer is already registered, or
     *                                  another service-level error occurs.
     * @throws IllegalArgumentException if officer or projectId is null/blank
     *                                  (programmer error).
     */
    public OfficerRegistration requestRegistration(HDBOfficer officer, String projectId) throws RegistrationException {
        // Input validation within the controller
        Objects.requireNonNull(officer, "Officer cannot be null for requestRegistration");
        if (projectId == null || projectId.trim().isEmpty()) {
            // Using IllegalArgumentException for invalid parameters passed to the
            // controller method
            throw new IllegalArgumentException("Project ID cannot be null or blank for requestRegistration");
        }
        // Delegate the complex logic and business rule checks to the service layer
        // The service method is declared to throw RegistrationException for business
        // rule failures
        return registrationService.requestProjectRegistration(officer, projectId);
    }

    /**
     * Handles an HDB Manager's review (approval or rejection) of a pending officer
     * registration request.
     * Validates input and delegates the core logic, including authorization and
     * state checks,
     * to the registration service.
     *
     * @param manager        The HDBManager performing the review. Must not be null.
     * @param registrationId The ID of the OfficerRegistration to review. Must not
     *                       be null or blank.
     * @param approve        true to approve the registration, false to reject it.
     * @return true if the review action was successfully processed by the service.
     * @throws RegistrationException    if the registration is not found, not
     *                                  pending, the manager lacks permission,
     *                                  approval violates rules (e.g., slots full),
     *                                  or another service-level error occurs.
     * @throws IllegalArgumentException if manager or registrationId is null/blank
     *                                  (programmer error).
     */
    public boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve)
            throws RegistrationException {
        // Input validation within the controller
        Objects.requireNonNull(manager, "Manager cannot be null for reviewRegistration");
        if (registrationId == null || registrationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration ID cannot be null or blank for reviewRegistration");
        }
        // Delegate the complex logic and business rule checks to the service layer
        // The service method is declared to throw RegistrationException for business
        // rule failures
        return registrationService.reviewRegistration(manager, registrationId, approve);
    }

    /**
     * Retrieves the current registration status for a specific officer regarding a
     * specific project.
     *
     * @param officer   The HDBOfficer whose status is requested. Must not be null.
     * @param projectId The ID of the project. Must not be null or blank.
     * @return The OfficerRegStatus (PENDING, APPROVED, REJECTED), or null if no
     *         registration exists
     *         for this officer and project combination, or if input parameters are
     *         invalid.
     */
    public OfficerRegStatus getMyRegistrationStatus(HDBOfficer officer, String projectId) {
        if (officer == null || projectId == null || projectId.trim().isEmpty()) {
            System.err.println(
                    "Controller Warning: Officer and valid Project ID are required for getMyRegistrationStatus.");
            return null;
        }
        return registrationService.getRegistrationStatus(officer, projectId);
    }

    /**
     * Gets a list of all officer registrations currently in the PENDING state.
     * Typically requested by an HDB Manager.
     *
     * @param manager The HDBManager requesting the list. Must not be null
     *                (indicates authenticated context).
     * @return An immutable List of pending OfficerRegistration objects. Returns an
     *         empty list if none are pending.
     * @throws IllegalArgumentException if manager is null (programmer error -
     *                                  context missing).
     * @throws RuntimeException         if an unexpected error occurs during
     *                                  retrieval in the service/repository layer.
     */
    public List<OfficerRegistration> getPendingRegistrations(HDBManager manager) {
        // Check for valid context - ensuring a manager is passed indicates
        // authenticated access
        Objects.requireNonNull(manager, "HDBManager context cannot be null when calling getPendingRegistrations.");

        try {
            // Delegate to the service. Service method should not throw checked exceptions
            // here.
            List<OfficerRegistration> pending = registrationService.getPendingRegistrations();
            // Return an immutable view or copy if desired, though Arrays.asList() might be
            // sufficient if result isn't huge
            return pending != null ? Collections.unmodifiableList(new ArrayList<>(pending)) : Collections.emptyList();
        } catch (Exception e) {
            // Catch unexpected runtime errors from the service/repo layer
            System.err.println("Controller ERROR: Failed to retrieve pending registrations: " + e.getMessage());
            // Re-throw as a runtime exception to signal a system problem
            throw new RuntimeException("Failed to retrieve pending registrations due to an internal error.", e);
        }
    }

    /**
     * Gets all registrations (regardless of status) associated with a specific
     * project.
     * Typically requested by an HDB Staff member (Manager or Officer).
     *
     * @param staff     The HDBStaff member requesting the list (provides context).
     *                  Must not be null.
     * @param projectId ID of the project. Must not be null or blank.
     * @return An immutable List of OfficerRegistration objects for the project.
     *         Returns an empty list if none found or input is invalid.
     * @throws IllegalArgumentException if staff or projectId is null/blank
     *                                  (programmer error).
     * @throws RuntimeException         if an unexpected error occurs during
     *                                  retrieval in the service/repository layer.
     */
    public List<OfficerRegistration> getProjectRegistrations(HDBStaff staff, String projectId) {
        // Input validation
        Objects.requireNonNull(staff, "Staff context cannot be null for getProjectRegistrations");
        if (projectId == null || projectId.trim().isEmpty()) {
            System.err.println("Controller Warning: Project ID cannot be null or blank for getProjectRegistrations.");
            return Arrays.asList(); // Return empty list for invalid project ID
        }

        try {
            // Delegate to the service. Service method should not throw checked exceptions
            // here.
            List<OfficerRegistration> registrations = registrationService.getRegistrationsByProject(projectId);
            return registrations != null ? Collections.unmodifiableList(new ArrayList<>(registrations))
                    : Collections.emptyList();
        } catch (Exception e) {
            // Catch unexpected runtime errors from the service/repo layer
            System.err.println("Controller ERROR: Failed to retrieve project registrations for " + projectId + ": "
                    + e.getMessage());
            // Re-throw as a runtime exception to signal a system problem
            throw new RuntimeException("Failed to retrieve project registrations due to an internal error.", e);
        }
    }

    /**
     * Finds the single active project an officer is currently approved to handle.
     * Fetches all registrations for the officer, filters for APPROVED status,
     * and then selects the most relevant project (active or most recent past).
     *
     * @param officer The officer whose handling project is sought.
     * @return The Project object they are handling, or null if none found or not
     *         approved.
     */
    public Project findApprovedHandlingProject(HDBOfficer officer) {
        if (officer == null) {
            return null;
        }

        // Step 1: Get ALL registrations for this officer using the service
        List<OfficerRegistration> allMyRegistrations = registrationService.getRegistrationsByOfficer(officer.getNric());

        // Step 2: Filter this list for APPROVED status *within the controller*
        List<OfficerRegistration> approvedRegistrations = allMyRegistrations.stream()
                .filter(reg -> reg.getStatus() == OfficerRegStatus.APPROVED)
                .collect(Collectors.toList());

        // --- The rest of the logic remains the same as before ---

        if (approvedRegistrations.isEmpty()) {
            return null; // Not approved for any project
        }

        // Logic to select the "current" handling project from the approved list
        LocalDate currentDate = LocalDate.now();
        Project potentiallyActiveProject = null;
        Project mostRecentPastProject = null;
        LocalDate mostRecentPastEndDate = LocalDate.MIN;

        for (OfficerRegistration reg : approvedRegistrations) { // Loop through only the APPROVED ones
            try {
                // Use ProjectService to get details for the approved project ID
                Project project = projectService.findProjectById(reg.getProjectId());
                if (project != null) {
                    // Check if this approved project is currently active
                    if (!currentDate.isBefore(project.getOpeningDate())
                            && !currentDate.isAfter(project.getClosingDate())) {
                        potentiallyActiveProject = project;
                        break; // Prioritize active project
                    } else if (currentDate.isAfter(project.getClosingDate())) {
                        // Track the most recently ended past project
                        if (project.getClosingDate().isAfter(mostRecentPastEndDate)) {
                            mostRecentPastEndDate = project.getClosingDate();
                            mostRecentPastProject = project;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error fetching project " + reg.getProjectId() + " details: " + e.getMessage());
            }
        }

        // Return active or most recent past approved project
        return (potentiallyActiveProject != null) ? potentiallyActiveProject : mostRecentPastProject;
    }

    /**
     * Retrieves all registration requests submitted by the specified officer.
     *
     * @param officer The HDBOfficer whose registrations are requested. Must not be
     *                null.
     * @return An immutable List of OfficerRegistration objects for this officer.
     *         Returns an empty list if none found or input is invalid.
     * @throws IllegalArgumentException if officer is null.
     * @throws RuntimeException         if an unexpected error occurs during
     *                                  retrieval.
     */
    public List<OfficerRegistration> getMyRegistrations(HDBOfficer officer) {
        Objects.requireNonNull(officer, "Officer cannot be null for getMyRegistrations");

        try {
            List<OfficerRegistration> registrations = registrationService.getRegistrationsByOfficer(officer.getNric());
            // Return immutable copy or view
            return registrations != null ? Collections.unmodifiableList(new ArrayList<>(registrations))
                    : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Controller ERROR: Failed to retrieve registrations for officer " + officer.getNric()
                    + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve officer registrations due to an internal error.", e);
        }
    }
}
