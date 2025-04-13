package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.repository.IRepository;

import java.util.List;

/**
 * Repository interface for accessing and manipulating Application entities in
 * the
 * data store.
 * <p>
 * This interface defines the contract for application data access operations,
 * including:
 * - Finding applications by various criteria (applicant, project, status)
 * - Saving new applications
 * - Updating existing applications
 * - Deleting applications
 * </p>
 * <p>
 * The repository follows the Repository pattern, abstracting the data access
 * logic from the rest of the application and providing a collection-like
 * interface
 * for application objects.
 * </p>
 */
public interface IApplicationRepository extends IRepository<Application, String> {
    /**
     * Finds an application by the applicant's NRIC.
     *
     * @param nric The NRIC (National Registration Identity Card) of the applicant
     * @return The application associated with the given NRIC, or null if none
     *         exists
     */
    Application findByApplicantNric(String nric);

    /**
     * Finds all applications associated with a specific project.
     *
     * @param projectId The unique identifier of the project
     * @return A list of applications for the specified project
     */
    List<Application> findByProjectId(String projectId);

    /**
     * Finds all applications with a specific status.
     *
     * @param status The application status to filter by
     * @return A list of applications with the specified status
     */
    List<Application> findByStatus(ApplicationStatus status);
}
