package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ntu.fdae.group1.bto.exceptions.AuthorizationException;
import com.ntu.fdae.group1.bto.exceptions.InvalidInputException;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.*;

/**
 * Service interface for project-related operations in the BTO Management
 * System.
 * <p>
 * This interface defines the contract for all project management operations,
 * including:
 * - Creating, editing, and deleting projects
 * - Managing project visibility
 * - Retrieving projects based on various filters and user roles
 * - Checking project eligibility for different users
 * </p>
 * <p>
 * The service layer sits between controllers and repositories, implementing
 * business rules and orchestrating multiple data operations to fulfill
 * use cases related to BTO projects.
 * </p>
 */
public interface IProjectService {

        /**
         * Creates a new BTO project with the specified details.
         * <p>
         * Validates project parameters and performs business rule checks, including:
         * - Valid date range (opening date before closing date)
         * - Manager's eligibility (not managing other projects during the same period)
         * - Officer slots within permitted range
         * - At least one flat type offered
         * </p>
         * <p>
         * If validation passes, a new project is created with a unique ID.
         * </p>
         *
         * @param manager      The HDB manager creating the project
         * @param name         The name of the project
         * @param neighborhood The neighborhood where the project is located
         * @param flatInfoMap  A map of flat types and their details (units, price)
         * @param openDate     The opening date for applications
         * @param closeDate    The closing date for applications
         * @param officerSlots The maximum number of officer slots for the project
         * @return The newly created Project object, or null if creation fails
         */
        Project createProject(HDBManager manager, String name, String neighborhood,
                        Map<String, ProjectFlatInfo> flatInfoMap,
                        LocalDate openDate, LocalDate closeDate, int officerSlots);

        /**
         * Edits the core details of an existing project.
         * <p>
         * Validates the changes against business rules, including:
         * - Valid date range
         * - Manager's eligibility and authorization
         * - Officer slots within permitted range and not less than current approved
         * count
         * </p>
         *
         * @param manager      The HDB manager editing the project
         * @param projectId    The ID of the project to edit
         * @param name         The new name of the project
         * @param neighborhood The new neighborhood of the project
         * @param openDate     The new opening date for applications
         * @param closeDate    The new closing date for applications
         * @param officerSlots The new maximum number of officer slots
         * @return true if the project was successfully updated, false otherwise
         */
        boolean editCoreProjectDetails(HDBManager manager, String projectId, String name, String neighborhood,
                        LocalDate openDate, LocalDate closeDate, int officerSlots);

        /**
         * Deletes a project from the system.
         * <p>
         * Deletion is only allowed if:
         * - The project exists
         * - The manager is authorized (is the project manager)
         * - The project has no active applications
         * </p>
         *
         * @param manager   The HDB manager requesting the deletion
         * @param projectId The ID of the project to delete
         * @return true if the project was successfully deleted, false otherwise
         */
        boolean deleteProject(HDBManager manager, String projectId);

        /**
         * Toggles the visibility of a project between visible and hidden states.
         * <p>
         * Only the project manager can toggle visibility. Hidden projects are not
         * visible to applicants but remain accessible to staff.
         * </p>
         *
         * @param manager   The HDB manager requesting the visibility change
         * @param projectId The ID of the project to toggle visibility for
         * @return true if the visibility was successfully toggled, false otherwise
         */
        boolean toggleVisibility(HDBManager manager, String projectId);

        /**
         * Retrieves all projects accessible to a specific user, with optional filters.
         * <p>
         * Staff can view all projects, while applicants can only view visible projects
         * for which they are eligible. Filters can include criteria such as
         * neighborhood,
         * flat type, and visibility.
         * </p>
         *
         * @param user    The user requesting the projects
         * @param filters A map of optional filters to apply (neighborhood, flat type,
         *                visibility)
         * @return A list of projects matching the criteria
         */
        List<Project> getAllProjects(User user, Map<String, Object> filters);

        /**
         * Retrieves all projects managed by a specific manager.
         * 
         * @param managerNRIC The NRIC of the manager
         * @return A list of projects managed by the specified manager
         */
        List<Project> getProjectsManagedBy(String managerNRIC);

        /**
         * Retrieves all projects managed by a specific manager with optional filters.
         *
         * @param managerNRIC The NRIC of the manager
         * @param filters     A map of optional filters to apply (neighborhood, flat
         *                    type, visibility)
         * @return A list of projects managed by the manager that match the filter
         *         criteria
         */
        List<Project> getProjectsManagedBy(String managerNRIC, Map<String, Object> filters);

        /**
         * Retrieves a project by its unique identifier.
         *
         * @param projectId The ID of the project to retrieve
         * @return The Project object, or null if not found
         */
        Project findProjectById(String projectId);

        /**
         * Retrieves all currently visible projects for which the user is eligible to
         * apply.
         * <p>
         * This is a convenience overload that calls getVisibleProjectsForUser with an
         * empty filter map.
         * </p>
         *
         * @param user The user for whom to filter the projects
         * @return A list of visible projects for which the user is eligible
         */
        List<Project> getVisibleProjectsForUser(User user);

        /**
         * Retrieves all currently visible projects for which the user is eligible to
         * apply,
         * filtered by the provided criteria.
         * <p>
         * Project visibility and application closing date are checked, along with
         * user-specific eligibility based on age, marital status, and flat types
         * offered.
         * </p>
         *
         * @param user    The user for whom to filter the projects
         * @param filters A map of optional filters to apply (neighborhood, flat type)
         * @return A list of visible projects for which the user is eligible that match
         *         the filters
         */
        List<Project> getVisibleProjectsForUser(User user, Map<String, Object> filters);

        /**
         * Retrieves all projects available for an HDB Officer to register for.
         * <p>
         * An officer can register for a project if:
         * - They have not applied for the project as an applicant
         * - They are not already registered for the project
         * - They are not registered for another project with an overlapping application
         * period
         * </p>
         *
         * @param officer The HDB Officer for whom to find available projects
         * @return A list of projects available for officer registration
         */
        List<Project> getProjectsAvailableForOfficerRegistration(HDBOfficer officer);
}
