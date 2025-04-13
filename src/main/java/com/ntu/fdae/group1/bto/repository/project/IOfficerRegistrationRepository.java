package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.repository.IRepository;

import java.util.List;

/**
 * Repository interface for accessing and manipulating OfficerRegistration
 * entities
 * in the BTO Management System.
 * <p>
 * This interface extends the generic IRepository interface, inheriting standard
 * CRUD operations while adding officer registration-specific query methods. It
 * handles
 * the persistence and retrieval of OfficerRegistration records, which represent
 * the assignment of HDB officers to specific projects.
 * </p>
 * <p>
 * Officer registrations track which officers are authorized to work with which
 * projects, along with their approval status. This information is crucial for
 * enforcing business rules about which officers can perform actions on
 * projects.
 * </p>
 */
public interface IOfficerRegistrationRepository extends IRepository<OfficerRegistration, String> {

    /**
     * Retrieves all project registrations for a specific officer.
     * <p>
     * This method finds all registration records for the specified officer,
     * allowing the system to determine which projects an officer is associated
     * with and their registration status for each project.
     * </p>
     * 
     * @param nric The NRIC of the officer whose registrations should be retrieved
     * @return A list of registration records for the specified officer, or an empty
     *         list if none exist
     */
    List<OfficerRegistration> findByOfficerNric(String nric);

    /**
     * Retrieves all officer registrations for a specific project.
     * <p>
     * This method finds all registration records for the specified project,
     * allowing the system to determine which officers are associated with the
     * project and their registration status.
     * </p>
     * 
     * @param projectId The ID of the project whose officer registrations should be
     *                  retrieved
     * @return A list of registration records for the specified project, or an empty
     *         list if none exist
     */
    List<OfficerRegistration> findByProjectId(String projectId);
}
