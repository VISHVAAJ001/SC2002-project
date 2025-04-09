package com.ntu.fdae.group1.bto.controllers.project;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
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
 * Controller for officer registration operations
 */
public class OfficerRegistrationController {
    private final IOfficerRegistrationService registrationService;
    private final IProjectService projectService;

    /**
     * Constructs a new OfficerRegistrationController
     * 
     * @param regService The registration service to use
     */
    public OfficerRegistrationController(IOfficerRegistrationService regService, IProjectService projService) {
        this.registrationService = regService;
        this.projectService = projService;
    }

    /**
     * Requests registration for a project
     * 
     * @param officer   The officer requesting registration
     * @param projectId ID of the project to register for
     * @return OfficerRegistration object if request was successful
     * @throws RegistrationException if the registration cannot be completed
     */
    public OfficerRegistration requestRegistration(HDBOfficer officer, String projectId) throws RegistrationException {
        return registrationService.requestProjectRegistration(officer, projectId);
    }

    /**
     * Reviews a registration request
     * 
     * @param manager        The manager reviewing the request
     * @param registrationId ID of the registration to review
     * @param approve        Whether to approve or reject the request
     * @return true if review was successful, false otherwise
     * @throws RegistrationException if the review cannot be completed
     */
    public boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve)
            throws RegistrationException {
        return registrationService.reviewRegistration(manager, registrationId, approve);
    }

    /**
     * Gets the status of an officer's registration for a project
     * 
     * @param officer   The officer
     * @param projectId ID of the project
     * @return The registration status, or null if not found
     */
    public OfficerRegStatus getMyRegistrationStatus(HDBOfficer officer, String projectId) {
        return registrationService.getRegistrationStatus(officer, projectId);
    }

    /**
     * Gets all pending registrations
     * 
     * @param manager The manager requesting pending registrations
     * @return List of pending registrations
     */
    public List<OfficerRegistration> getPendingRegistrations(HDBManager manager) {

        Objects.requireNonNull(manager, "HDBManager context cannot be null when calling getPendingRegistrations.");

        try {
            // Call the service method, which doesn't throw checked exceptions
            return registrationService.getPendingRegistrations();
        } catch (Exception e) { // Optional: Catch unexpected runtime errors
            System.err.println("Controller ERROR: Failed to retrieve pending registrations: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve pending registrations due to an internal error.", e);
        }
    }

    /**
     * Gets all registrations for a specific project
     * 
     * @param staff     The staff member requesting the registrations
     * @param projectId ID of the project
     * @return List of registrations for the project
     */
    public List<OfficerRegistration> getProjectRegistrations(HDBStaff staff, String projectId) {
        return registrationService.getRegistrationsByProject(projectId);
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
}
