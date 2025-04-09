package com.ntu.fdae.group1.bto.services.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IProjectService {
        /**
         * Creates a new BTO project
         * 
         * @param manager      The manager creating the project
         * @param name         Project name
         * @param neighborhood Project location/neighborhood
         * @param flatInfoMap  Map of flat types and their information
         * @param openDate     Project application opening date
         * @param closeDate    Project application closing date
         * @param officerSlots Number of officer slots for this project
         * @return The created Project
         */
        Project createProject(HDBManager manager, String name, String neighborhood,
                        Map<String, ProjectFlatInfo> flatInfoMap,
                        LocalDate openDate, LocalDate closeDate, int officerSlots);

        /**
         * Edits core details of an existing project
         * 
         * @param manager      The manager editing the project
         * @param projectId    ID of the project to edit
         * @param name         New project name
         * @param neighborhood New neighborhood
         * @param openDate     New opening date
         * @param closeDate    New closing date
         * @param officerSlots New number of officer slots
         * @return true if edit was successful, false otherwise
         */
        boolean editCoreProjectDetails(HDBManager manager, String projectId, String name,
                        String neighborhood, LocalDate openDate,
                        LocalDate closeDate, int officerSlots);

        /**
         * Deletes a project from the system
         * 
         * @param manager   The manager deleting the project
         * @param projectId ID of the project to delete
         * @return true if deletion was successful, false otherwise
         */
        boolean deleteProject(HDBManager manager, String projectId);

        /**
         * Toggles the visibility of a project
         * 
         * @param manager   The manager toggling visibility
         * @param projectId ID of the project to toggle
         * @return true if toggle was successful, false otherwise
         */
        boolean toggleVisibility(HDBManager manager, String projectId);

        /**
         * Gets all projects visible to a specific user
         * 
         * @param user The user to get visible projects for
         * @return List of projects visible to the user
         */
        List<Project> getVisibleProjectsForUser(User user, Map<String, Object> filters);

        // Overload without filters
        
        List<Project> getVisibleProjectsForUser(User user);
        /**
         * Gets all projects in the system
         * 
         * @return All projects
         */
        List<Project> getAllProjects(User user, Map<String, Object> filters);

        /**
         * Gets projects managed by a specific manager
         * 
         * @param managerNRIC NRIC of the manager
         * @return List of projects managed by the manager
         */
        List<Project> getProjectsManagedBy(String managerNRIC);

        /**
        * Gets projects managed by a specific manager, optionally applying filters.
        *
        * @param managerNRIC NRIC of the manager.
        * @param filters     A map containing optional filters (e.g., "neighborhood", "flatType", "visibility").
        * @return List of projects managed by the manager that match the filters.
        */
        List<Project> getProjectsManagedBy(String managerNRIC, Map<String, Object> filters);

        /**
         * Finds a project by its ID
         * 
         * @param projectId ID of the project to find
         * @return The project, or null if not found
         */
        Project findProjectById(String projectId);
}
