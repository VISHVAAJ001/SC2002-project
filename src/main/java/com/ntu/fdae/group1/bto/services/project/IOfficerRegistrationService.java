package com.ntu.fdae.group1.bto.services.project;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;

public interface IOfficerRegistrationService {
    /**
     * Requests registration for a project by an officer
     * 
     * @param officer   The officer requesting registration
     * @param projectId ID of the project to register for
     * @return The created registration
     * @throws RegistrationException if registration request fails
     */
    OfficerRegistration requestProjectRegistration(HDBOfficer officer, String projectId) throws RegistrationException;

    /**
     * Reviews an officer registration request
     * 
     * @param manager        The manager reviewing the request
     * @param registrationId ID of the registration to review
     * @param approve        true to approve, false to reject
     * @return true if review was successful, false otherwise
     */
    boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve);

    /**
     * Gets the status of an officer's registration for a project
     * 
     * @param officer   The officer
     * @param projectId ID of the project
     * @return The registration status, or null if not found
     */
    OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId);

    /**
     * Gets all pending registrations
     * 
     * @return List of pending registrations
     */
    List<OfficerRegistration> getPendingRegistrations();

    /**
     * Gets all registrations for a specific project
     * 
     * @param projectId ID of the project
     * @return List of registrations for the project
     */
    List<OfficerRegistration> getRegistrationsByProject(String projectId);

    /**
     * Gets all registrations for a specific officer
     * 
     * @param officerId ID of the officer
     * @return List of registrations for the officer
     */
    List<OfficerRegistration> getRegistrationsByOfficer(String officerId);
}
