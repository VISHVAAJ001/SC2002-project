package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.enums.*;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.*;
import com.ntu.fdae.group1.bto.models.project.Application; 

import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;

import com.ntu.fdae.group1.bto.utils.*; 

public class ProjectService implements IProjectService {
    private final IProjectRepository projectRepo;
    private final IUserRepository userRepo; 
    private final IEligibilityService eligibilityService;
    private final IApplicationRepository applicationRepo; 

    public ProjectService(IProjectRepository projectRepo,
                          IUserRepository userRepo, 
                          IEligibilityService eligibilityService,
                          IApplicationRepository applicationRepo) { 
        this.projectRepo = Objects.requireNonNull(projectRepo, "Project Repository cannot be null");
        this.userRepo = userRepo; // Assign if kept
        this.eligibilityService = Objects.requireNonNull(eligibilityService, "Eligibility Service cannot be null");
        this.applicationRepo = Objects.requireNonNull(applicationRepo, "Application Repository cannot be null"); // <<< ASSIGN FIELD HERE >>>
    }

    @Override
    public Project createProject(HDBManager manager, String name, String neighborhood,
                                 Map<String, ProjectFlatInfo> flatInfoMap,
                                 LocalDate openDate, LocalDate closeDate, int officerSlots) {

        // Eligibility Check
        // Need to handle Collection<Project> type potentially returned by findAll()
        Map<String, Project> projectMap = projectRepo.findAll();
        Collection<Project> allProjects = (projectMap != null) ? projectMap.values() : List.of();
        if (!eligibilityService.checkManagerProjectHandlingEligibility(manager, openDate, closeDate, allProjects)) {
             System.err.println("Service Error: Manager " + manager.getNric() + " is already handling another project during this application period. Project creation failed.");
             return null;
        }

        String projectId = IdGenerator.generateProjectId();

        // Convert Map<String, ProjectFlatInfo> to Map<FlatType, ProjectFlatInfo>
        Map<FlatType, ProjectFlatInfo> typedFlatInfoMap;
        try {
            typedFlatInfoMap = flatInfoMap.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> FlatType.valueOf(entry.getKey().trim().toUpperCase()),
                    Map.Entry::getValue
                ));
        } catch (IllegalArgumentException e) {
            System.err.println("Service Error: Invalid FlatType string found in flatInfoMap keys: " + e.getMessage());
            return null;
        } catch (Exception e) {
             System.err.println("Service Error: Failed to process flatInfoMap: " + e.getMessage());
             return null;
        }

        // Ensure both required flat types are present
        if (typedFlatInfoMap.size() != 2 || !typedFlatInfoMap.containsKey(FlatType.TWO_ROOM) || !typedFlatInfoMap.containsKey(FlatType.THREE_ROOM)) {
             System.err.println("Service Error: flatInfoMap must contain exactly TWO_ROOM and THREE_ROOM after conversion.");
             return null;
        }

        Project newProject = new Project(projectId, name, neighborhood, typedFlatInfoMap, openDate, closeDate, manager.getNric(), officerSlots);
        projectRepo.save(newProject);
        System.out.println("Service: Project " + name + " created successfully with ID: " + projectId);
        return newProject;
    }

    @Override
    public boolean editCoreProjectDetails(HDBManager manager, String projectId, String name, String neighborhood,
                                          LocalDate openDate, LocalDate closeDate, int officerSlots) {
        Project project = projectRepo.findById(projectId);
        if (project == null) {
            System.err.println("Service Error: Project not found with ID: " + projectId);
            return false;
        }
        if (!project.getManagerNric().equals(manager.getNric())) {
            System.err.println("Service Error: Manager " + manager.getNric() + " does not have permission to edit project " + projectId);
            return false;
        }
        // Check officer slot constraint against the actual list in the project object
        if (project.getApprovedOfficerNrics() != null && officerSlots < project.getApprovedOfficerNrics().size()) {
             System.err.println("Service Error: Cannot set max officer slots (" + officerSlots + ") below the current number of approved officers (" + project.getApprovedOfficerNrics().size() + ").");
             return false;
        }

        // Use try-catch for potential validation errors from setters
        try {
            project.setProjectName(name);
            project.setNeighborhood(neighborhood);
            project.setOpeningDate(openDate);
            project.setClosingDate(closeDate);
            project.setMaxOfficerSlots(officerSlots);
        } catch (IllegalArgumentException e) {
             System.err.println("Service Error: Invalid data provided for project update - " + e.getMessage());
             return false;
        }

        projectRepo.save(project);
        System.out.println("Service: Project " + projectId + " updated successfully.");
        return true;
    }

    @Override
    public boolean deleteProject(HDBManager manager, String projectId) {
        Project project = projectRepo.findById(projectId);
        if (project == null) {
            System.err.println("Service Error: Project not found with ID: " + projectId);
            return false;
        }
        if (!project.getManagerNric().equals(manager.getNric())) {
            System.err.println("Service Error: Manager " + manager.getNric() + " does not have permission to delete project " + projectId);
            return false;
        }

        List<Application> projectApps = this.applicationRepo.findByProjectId(projectId);
        boolean hasActiveApps = projectApps.stream()
            .anyMatch(app -> app.getStatus() == ApplicationStatus.PENDING ||
                             app.getStatus() == ApplicationStatus.SUCCESSFUL);

        if (hasActiveApps) {
             System.err.println("Service Error: Cannot delete project " + projectId + " because it has PENDING or SUCCESSFUL (unbooked) applications.");
             return false;
        }

        // Deletion attempt
        try {
             projectRepo.deleteById(projectId);
             System.out.println("Service: Project " + projectId + " deleted successfully.");
             return true;
        } catch (UnsupportedOperationException e) {
             System.err.println("Service Error: Deleting projects is not supported by the repository: " + e.getMessage());
             return false;
        } catch (Exception e) {
             System.err.println("Service Error: An error occurred while deleting project " + projectId + ": " + e.getMessage());
             return false;
        }
    }

    @Override
    public boolean toggleVisibility(HDBManager manager, String projectId) {
         Project project = projectRepo.findById(projectId);
         if (project == null) {
             System.err.println("Service Error: Project not found with ID: " + projectId);
             return false;
         }
         if (!project.getManagerNric().equals(manager.getNric())) {
             System.err.println("Service Error: Manager " + manager.getNric() + " does not have permission to change visibility for project " + projectId);
             return false;
         }
         // Use the setter from Project.java
         project.setVisibility(!project.isVisible());
         projectRepo.save(project);
         System.out.println("Service: Project " + projectId + " visibility set to: " + (project.isVisible() ? "ON" : "OFF"));
         return true;
    }

    @Override
    public List<Project> getAllProjects() {
        Map<String, Project> projectMap = projectRepo.findAll();
        return (projectMap == null) ? new ArrayList<>() : new ArrayList<>(projectMap.values());
    }

    @Override
    public List<Project> getProjectsManagedBy(String managerNRIC) {
        if (managerNRIC == null || managerNRIC.isBlank()) {
            return new ArrayList<>();
        }
        Map<String, Project> projectMap = projectRepo.findAll();
        if (projectMap == null || projectMap.isEmpty()) {
             return new ArrayList<>();
        }
        return projectMap.values().stream()
                .filter(p -> managerNRIC.equals(p.getManagerNric()))
                .collect(Collectors.toList());
    }

    @Override
    public Project findProjectById(String projectId) {
         if (projectId == null || projectId.isBlank()) {
            return null;
        }
        return projectRepo.findById(projectId);
    }

    @Override
    public List<Project> getVisibleProjectsForUser(User user) {
        LocalDate currentDate = LocalDate.now();
        Map<String, Project> projectMap = projectRepo.findAll();
         if (projectMap == null || projectMap.isEmpty()) {
             return new ArrayList<>();
         }
        List<Project> allProjects = new ArrayList<>(projectMap.values());

        return allProjects.stream()
                .filter(Project::isVisible)
                .filter(project -> project.getClosingDate() != null && !currentDate.isAfter(project.getClosingDate()))
                .filter(project -> isProjectEligibleForApplicant(user, project))
                .sorted(Comparator.comparing(Project::getProjectName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    // Helper method for eligibility check
    private boolean isProjectEligibleForApplicant(User user, Project project) {
        if (user == null || project == null) return false;
        if (user.getRole() == UserRole.HDB_MANAGER) {
            return false;
        }
        if (user.getRole() == UserRole.APPLICANT || user.getRole() == UserRole.HDB_OFFICER) {
            int age = user.getAge();
            MaritalStatus status = user.getMaritalStatus();
            Map<FlatType, ProjectFlatInfo> flats = project.getFlatTypes();
            if (flats == null || flats.isEmpty()) {
                return false;
            }
            if (status == MaritalStatus.SINGLE && age >= 35) {
                return flats.containsKey(FlatType.TWO_ROOM);
            } else if (status == MaritalStatus.MARRIED && age >= 21) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}