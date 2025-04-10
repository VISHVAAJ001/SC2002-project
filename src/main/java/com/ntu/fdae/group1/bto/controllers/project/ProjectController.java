package com.ntu.fdae.group1.bto.controllers.project;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.exceptions.AuthorizationException;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.project.IProjectService;

/**
 * Controller for project-related operations
 */
public class ProjectController {
    private final IProjectService projectService;

    /**
     * Constructs a new ProjectController
     * 
     * @param projectService The project service to use
     */
    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Creates a new project
     * 
     * @param manager      The manager creating the project
     * @param name         Project name
     * @param neighborhood Project location
     * @param flatInfoMap  Information about flat types
     * @param openDate     Opening date for applications
     * @param closeDate    Closing date for applications
     * @param officerSlots Number of slots for officers
     * @return The created project
     */
    public Project createProject(HDBManager manager, String name, String neighborhood,
            Map<String, ProjectFlatInfo> flatInfoMap,
            LocalDate openDate, LocalDate closeDate, int officerSlots) {
        return projectService.createProject(manager, name, neighborhood, flatInfoMap, openDate, closeDate,
                officerSlots);
    }

    /**
     * Edits an existing project
     * 
     * @param manager      The manager editing the project
     * @param projectId    ID of the project to edit
     * @param name         New name
     * @param neighborhood New location
     * @param openDate     New opening date
     * @param closeDate    New closing date
     * @param officerSlots New number of officer slots
     * @return true if edit was successful, false otherwise
     */
    public boolean editProject(HDBManager manager, String projectId, String name,
            String neighborhood, LocalDate openDate,
            LocalDate closeDate, int officerSlots) {
        return projectService.editCoreProjectDetails(manager, projectId, name, neighborhood,
                openDate, closeDate, officerSlots);
    }

    /**
     * Deletes a project
     * 
     * @param manager   The manager deleting the project
     * @param projectId ID of the project to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteProject(HDBManager manager, String projectId) {
        return projectService.deleteProject(manager, projectId);
    }

    /**
     * Toggles the visibility of a project
     * 
     * @param manager   The manager toggling visibility
     * @param projectId ID of the project to toggle
     * @return true if toggle was successful, false otherwise
     */
    public boolean toggleProjectVisibility(HDBManager manager, String projectId) {
        return projectService.toggleVisibility(manager, projectId);
    }

    /**
     * Gets projects visible to a user
     * 
     * @param user The user to get visible projects for
     * @return List of visible projects
     */
    public List<Project> getVisibleProjectsForUser(User user, Map<String, Object> filter) {
        // No specific authorization needed here usually, as the service filters based
        // on user.
        if (user == null) {
            // Handle error - perhaps return empty list or throw exception
            System.err.println("Error: User context is required to view visible projects.");
            return Collections.emptyList();
        }

        return projectService.getVisibleProjectsForUser(user, filter);
    }

    /**
     * Gets all projects in the system
     * 
     * @return All projects
     * @throws AuthorizationException
     */
    public List<Project> getAllProjects(User user, Map<String, Object> filter) throws AuthorizationException {
        if (user.getRole() != UserRole.HDB_MANAGER) {
            throw new AuthorizationException("Only HDB Managers can view all projects.");
        }

        return projectService.getAllProjects(user, filter);
    }

    /**
     * Gets projects managed by a specific manager
     * 
     * @param managerNRIC NRIC of the manager
     * @return List of projects managed by the manager
     */
    public List<Project> getProjectsManagedBy(String managerNRIC) {
        return projectService.getProjectsManagedBy(managerNRIC);
    }

    /**
     * Finds a project by its ID
     * 
     * @param projectId ID of the project to find
     * @return The project, or null if not found
     */
    public Project findProjectById(String projectId) {
        return projectService.findProjectById(projectId);
    }

    /**
     * Gets projects potentially available for the given officer to register for
     * handling.
     * Delegates filtering logic (e.g., excluding projects already applied for or
     * registered for)
     * to the ProjectService.
     *
     * @param officer The HDBOfficer requesting the list. Must not be null.
     * @return A List of Project objects available for registration, sorted by name.
     * @throws IllegalArgumentException if officer is null.
     * @throws RuntimeException         if an unexpected error occurs during
     *                                  retrieval.
     */
    public List<Project> getProjectsAvailableForRegistration(HDBOfficer officer) {
        java.util.Objects.requireNonNull(officer, "Officer cannot be null for getProjectsAvailableForRegistration");

        try {
            return projectService.getProjectsAvailableForOfficerRegistration(officer);
        } catch (Exception e) {
            System.err.println("Controller ERROR: Failed to retrieve projects available for registration for officer "
                    + officer.getNric());
            return Collections.emptyList();
        }
    }
}
