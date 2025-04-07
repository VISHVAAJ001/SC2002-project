package com.ntu.fdae.group1.bto.services.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.User;
// Import FlatType if needed for other methods later, but not createProject signature
// import com.ntu.fdae.group1.bto.models.enums.FlatType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface defining operations related to BTO Projects.
 */
public interface IProjectService {

    /**
     * Creates a new BTO project.
     * Handles internal conversion from String keys in flatInfoMap to FlatType keys.
     * Returns null if creation fails due to validation or eligibility errors.
     *
     * @param manager      The manager creating the project.
     * @param name         Project name.
     * @param neighborhood Project location/neighborhood.
     * @param flatInfoMap  Map of flat type names (String, e.g., "TWO_ROOM") to their information.
     *                     Must contain entries for "TWO_ROOM" and "THREE_ROOM".
     * @param openDate     Project application opening date.
     * @param closeDate    Project application closing date.
     * @param officerSlots Number of officer slots for this project (e.g., 1-10).
     * @return The created Project object, or null if creation failed.
     */
    Project createProject(HDBManager manager, String name, String neighborhood,
                          Map<String, ProjectFlatInfo> flatInfoMap, // Key is String
                          LocalDate openDate, LocalDate closeDate, int officerSlots);

    /**
     * Edits core details of an existing project.
     *
     * @param manager      The manager editing the project (for authorization).
     * @param projectId    ID of the project to edit.
     * @param name         New project name.
     * @param neighborhood New neighborhood.
     * @param openDate     New opening date.
     * @param closeDate    New closing date.
     * @param officerSlots New number of officer slots.
     * @return true if edit was successful, false otherwise (e.g., project not found, permission denied, invalid slots).
     */
    boolean editCoreProjectDetails(HDBManager manager, String projectId, String name,
                                   String neighborhood, LocalDate openDate,
                                   LocalDate closeDate, int officerSlots);

    /**
     * Deletes a project from the system.
     * Requires the corresponding repository method (e.g., deleteById) to be implemented.
     * May fail if business rules prevent deletion (e.g., active applications).
     *
     * @param manager   The manager deleting the project (for authorization).
     * @param projectId ID of the project to delete.
     * @return true if deletion was successful, false otherwise.
     */
    boolean deleteProject(HDBManager manager, String projectId);

    /**
     * Toggles the visibility flag of a project.
     *
     * @param manager   The manager toggling visibility (for authorization).
     * @param projectId ID of the project to toggle.
     * @return true if toggle was successful, false otherwise (e.g., project not found, permission denied).
     */
    boolean toggleVisibility(HDBManager manager, String projectId);

    /**
     * Gets a list of all projects currently in the system.
     *
     * @return A List of all Project objects. Returns an empty list if none exist.
     */
    List<Project> getAllProjects();

    /**
     * Gets a list of projects managed by a specific HDB Manager.
     *
     * @param managerNRIC NRIC of the manager.
     * @return A List of Project objects managed by the specified manager. Returns an empty list if NRIC is invalid or manager manages no projects.
     */
    List<Project> getProjectsManagedBy(String managerNRIC);

    /**
     * Finds and retrieves a single project by its unique ID.
     *
     * @param projectId The ID of the project to find.
     * @return The Project object if found, or null otherwise.
     */
    Project findProjectById(String projectId);

    /**
     * Gets a list of projects that are currently visible and potentially eligible
     * for a specific user (Applicant or Officer acting as one) to apply for.
     * Filters based on visibility, application period, and basic BTO eligibility rules.
     * Sorted alphabetically by project name.
     *
     * @param user The user (Applicant/Officer) for whom to check eligibility.
     * @return A List of eligible and visible Project objects, sorted by name. Returns an empty list if none match.
     */
    List<Project> getVisibleProjectsForUser(User user);

}