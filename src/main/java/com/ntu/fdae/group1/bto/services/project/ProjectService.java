package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.*;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;

public class ProjectService implements IProjectService {
    private final IProjectRepository projectRepo;
    private final IEligibilityService eligibilityService; // Or put logic here

    public ProjectService(IProjectRepository projectRepo, IEligibilityService eligibilityService) {
        this.projectRepo = projectRepo;
        this.eligibilityService = eligibilityService;
    }

    @Override
    public List<Project> getVisibleProjectsForUser(User user) {
        // 1. Fetch all projects (handle potential map from repo)
        List<Project> allProjects = new ArrayList<>(projectRepo.findAll().values());

        // 2. Filter Stream
        List<Project> filteredProjects = allProjects.stream()
                // Filter 1: Must be visible
                .filter(Project::isVisible)
                // Optional: Sort alphabetically by name
                .sorted(Comparator.comparing(Project::getProjectName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        return filteredProjects;
    }

    @Override
    public Project createProject(HDBManager manager, String name, String neighborhood,
            Map<String, ProjectFlatInfo> flatInfoMap, LocalDate openDate, LocalDate closeDate, int officerSlots) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createProject'");
    }

    @Override
    public boolean editCoreProjectDetails(HDBManager manager, String projectId, String name, String neighborhood,
            LocalDate openDate, LocalDate closeDate, int officerSlots) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editCoreProjectDetails'");
    }

    @Override
    public boolean deleteProject(HDBManager manager, String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteProject'");
    }

    @Override
    public boolean toggleVisibility(HDBManager manager, String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toggleVisibility'");
    }

    @Override
    public List<Project> getAllProjects() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllProjects'");
    }

    @Override
    public List<Project> getProjectsManagedBy(String managerNRIC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProjectsManagedBy'");
    }

    @Override
    public Project findProjectById(String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findProjectById'");
    }
}
