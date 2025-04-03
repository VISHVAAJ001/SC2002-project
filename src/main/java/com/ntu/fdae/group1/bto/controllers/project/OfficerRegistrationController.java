package com.ntu.fdae.group1.bto.controllers.project;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.services.project.IOfficerRegistrationService;

/**
 * Controller for officer registration operations
 */
public class OfficerRegistrationController {
    private final IOfficerRegistrationService registrationService;

    /**
     * Constructs a new OfficerRegistrationController
     * 
     * @param regService The registration service to use
     */
    public OfficerRegistrationController(IOfficerRegistrationService regService) {
        this.registrationService = regService;
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
    public List<OfficerRegistration> getPendingRegistrations(HDBManager manager) throws AuthenticationException {
        // 1. Authorization Check
        if (manager == null) { // Or more robust check if needed
            throw new AuthenticationException("Authentication required.");
        }

        return registrationService.getPendingRegistrations();
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
}
