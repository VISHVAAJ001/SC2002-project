package com.ntu.fdae.group1.bto.services.user; 

import com.ntu.fdae.group1.bto.exceptions.*;
import com.ntu.fdae.group1.bto.enums.*;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.OfficerRegistration;

import java.util.List;

/**
 * Interface defining operations related to HDB Officer project registrations.
 */
public interface IOfficerRegistrationService {

    /**
     * Allows an HDB Officer to request registration for a specific project.
     * Performs eligibility checks before creating the registration record.
     *
     * @param officer   The HDBOfficer making the request.
     * @param projectId The ID of the project they wish to register for.
     * @return The created OfficerRegistration object (initially PENDING).
     * @throws RegistrationException if the officer is ineligible, project not found, or other errors occur.
     */
    OfficerRegistration requestProjectRegistration(HDBOfficer officer, String projectId) throws RegistrationException;

    /**
     * Allows an HDB Manager to review (approve or reject) a pending officer registration.
     *
     * @param manager        The HDBManager performing the review (must be the manager of the project).
     * @param registrationId The ID of the OfficerRegistration to review.
     * @param approve        true to approve, false to reject.
     * @return true if the review was successfully processed.
     * @throws RegistrationException if registration not found, not pending, manager lacks permission, or approval violates rules (e.g., slots full).
     */
    boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve) throws RegistrationException;

    /**
     * Gets the current registration status for a specific officer and project.
     *
     * @param officer   The HDBOfficer whose status is requested.
     * @param projectId The ID of the project.
     * @return The OfficerRegStatus (PENDING, APPROVED, REJECTED), or null if no registration exists for this combination.
     */
    OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId);

    /**
     * Retrieves a list of all officer registrations that are currently in the PENDING state.
     * Typically used by HDB Managers.
     *
     * @return A List of OfficerRegistration objects with PENDING status.
     */
    List<OfficerRegistration> getPendingRegistrations();

    /**
     * Retrieves a list of all officer registrations (regardless of status) for a specific project.
     *
     * @param projectId The ID of the project.
     * @return A List of OfficerRegistration objects associated with the project.
     */
    List<OfficerRegistration> getRegistrationsByProject(String projectId);

}