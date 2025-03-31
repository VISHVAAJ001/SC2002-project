package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.IProjectRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

public class ProjectController {
    private IProjectRepository projectRepo;

    public ProjectController(IProjectRepository projRepo) {
        this.projectRepo = projRepo;
    }

    public Project createProject(HDBManager manager, String name, String neighborhood,
            Map<String, ProjectFlatInfo> flatInfoMap, LocalDate openDate,
            LocalDate closeDate, int officerSlots) {
        // Implementation
        return null;
    }

    public boolean editProject(HDBManager manager, String projectId, String name,
            String neighborhood, LocalDate openDate,
            LocalDate closeDate, int officerSlots) {
        // Implementation
        return false;
    }

    public boolean deleteProject(HDBManager manager, String projectId) {
        // Implementation
        return false;
    }

    public boolean toggleVisibility(HDBManager manager, String projectId) {
        // Implementation
        return false;
    }

    public List<Project> getVisibleProjectsForUser(User user) {
        // Implementation
        return null;
    }

    public List<Project> getAllProjects(HDBManager manager) {
        // Implementation
        return null;
    }

    public List<Project> getProjectsManagedBy(String managerNRIC) {
        // Implementation
        return null;
    }

    public Project findProjectById(String projectId) {
        // Implementation
        return null;
    }
}
