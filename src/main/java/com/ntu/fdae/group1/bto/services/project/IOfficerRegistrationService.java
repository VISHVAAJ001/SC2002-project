package com.ntu.fdae.group1.bto.services.project;

import java.util.List;

import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;

/**
 * Service interface defining operations for managing HDB Officer registrations
 * to projects.
 * <p>
 * This interface provides methods for officers to request registration to
 * projects,
 * for managers to review these requests, and for querying registration status
 * and
 * details. The officer registration process is a key part of the authorization
 * system,
 * determining which officers can work with which projects.
 * </p>
 * 
 * Key responsibilities include:
 * <ul>
 * <li>Enabling officers to request association with specific projects</li>
 * <li>Supporting manager approval/rejection of registration requests</li>
 * <li>Providing registration status information for authorization checks</li>
 * <li>Retrieving lists of registrations by various criteria</li>
 * </ul>
 * 
 */
public interface IOfficerRegistrationService {

    /**
     * Requests registration of an HDB officer to a project.
     * <p>
     * This is the first step in the registration process, where an officer
     * requests to be associated with a project. The registration starts in
     * PENDING status and requires manager approval.
     * </p>
     * 
     * @param officer   The HDB officer requesting registration
     * @param projectId The ID of the project the officer wants to register for
     * @return The created registration record
     * @throws RegistrationException if the registration request fails (e.g.,
     *                               officer is not eligible)
     */
    OfficerRegistration requestProjectRegistration(HDBOfficer officer, String projectId) throws RegistrationException;

    /**
     * Reviews (approves or rejects) an officer's registration request.
     * <p>
     * This method allows a project manager to decide whether to approve or reject
     * an officer's request to be associated with their project.
     * </p>
     * 
     * @param manager        The manager reviewing the registration request
     * @param registrationId The ID of the registration to review
     * @param approve        True to approve, false to reject
     * @return True if the review was successful
     * @throws RegistrationException if the review fails (e.g., unauthorized
     *                               manager)
     */
    boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve) throws RegistrationException;

    /**
     * Gets the registration status of an officer for a specific project.
     * <p>
     * This method is primarily used for authorization checks to determine if
     * an officer is approved to work on a specific project.
     * </p>
     * 
     * @param officer   The officer whose status should be checked
     * @param projectId The ID of the project to check
     * @return The registration status, or null if no registration exists
     */
    OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId);

    /**
     * Gets all pending registration requests across all projects.
     * <p>
     * This method is typically used by managers to view all pending
     * registration requests that need their attention.
     * </p>
     * 
     * @return A list of all pending registration requests
     */
    List<OfficerRegistration> getPendingRegistrations();

    /**
     * Gets all pending registration requests for a specific project.
     * <p>
     * This method allows project managers to view registration requests
     * specifically for their projects.
     * </p>
     * 
     * @param projectId The ID of the project to check
     * @return A list of pending registration requests for the specified project
     */
    List<OfficerRegistration> getPendingRegistrationsForProject(String projectId);

    /**
     * Gets the count of pending registration requests for a specific project.
     * <p>
     * This method provides a quick way to check how many pending registration
     * requests exist for a project without retrieving the full details.
     * </p>
     * 
     * @param projectId The ID of the project to check
     * @return The number of pending registration requests for the project
     */
    int getPendingRegistrationCountForProject(String projectId);

    /**
     * Gets all registration records for a specific project.
     * <p>
     * This method retrieves all officer registrations associated with a project,
     * regardless of their status.
     * </p>
     * 
     * @param projectId The ID of the project
     * @return A list of all registration records for the project
     */
    List<OfficerRegistration> getRegistrationsByProject(String projectId);

    /**
     * Gets all registration records for a specific officer.
     * <p>
     * This method retrieves all project registrations associated with an officer,
     * regardless of their status.
     * </p>
     * 
     * @param officerNric The NRIC of the officer
     * @return A list of all registration records for the officer
     */
    List<OfficerRegistration> getRegistrationsByOfficer(String officerNric);
}
