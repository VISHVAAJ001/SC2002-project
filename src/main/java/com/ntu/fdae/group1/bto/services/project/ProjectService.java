package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects; 
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.models.enums.FlatType;
import com.ntu.fdae.group1.bto.models.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.models.enums.UserRole;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.*;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService; 
import com.ntu.fdae.group1.bto.exception.RegistrationException; 
import com.ntu.fdae.group1.bto.util.IdGenerator;


public class ProjectService implements IProjectService {
    private final IProjectRepository projectRepo;
    private final IUserRepository userRepo; // Keep if needed for manager validation, otherwise remove
    private final IEligibilityService eligibilityService;

    public ProjectService(IProjectRepository projectRepo, IUserRepository userRepo,
                          IEligibilityService eligibilityService) {
        this.projectRepo = Objects.requireNonNull(projectRepo, "Project Repository cannot be null");
        this.userRepo = userRepo; // Can be null if not strictly needed here
        this.eligibilityService = Objects.requireNonNull(eligibilityService, "Eligibility Service cannot be null");
    }

    @Override
    public Project createProject(HDBManager manager, String name, String neighborhood, Map<FlatType, ProjectFlatInfo> flatInfoMap, 
                                 LocalDate openDate, LocalDate closeDate, int officerSlots) throws RegistrationException { 

        // SRP: Eligibility check delegated
        // Assuming IEligibilityService has checkManagerProjectHandlingEligibility method
        // You might need to adapt the signature based on your IEligibilityService interface
        if (!eligibilityService.checkManagerProjectHandlingEligibility(manager, openDate, closeDate, projectRepo.findAll().values())) {
            throw new RegistrationException("Manager " + manager.getNric() + " is already handling another project during this application period.");
        }
        // OCP/DIP: Using IProjectRepository interface

        String projectId = IdGenerator.generateProjectId(); // Utility SRP
        Project newProject = new Project(projectId, name, neighborhood, flatInfoMap, openDate, closeDate, manager.getNric(), officerSlots);
        projectRepo.save(newProject); // SRP: Repository handles persistence
        System.out.println("Service: Project " + name + " created successfully with ID: " + projectId); // Feedback
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
        // SRP: Authorization check
        if (!project.getManagerNric().equals(manager.getNric())) {
            System.err.println("Service Error: Manager " + manager.getNric() + " does not have permission to edit project " + projectId);
            return false;
        }

        // Add validation if needed (e.g., cannot reduce slots below approved officers)
        if (officerSlots < project.getApprovedOfficerNrics().size()) {
             System.err.println("Service Error: Cannot set max officer slots (" + officerSlots + ") below the current number of approved officers (" + project.getApprovedOfficerNrics().size() + ").");
             return false;
        }
        // Add validation for dates if necessary (e.g., cannot change dates if application period started?)

        project.setProjectName(name);
        project.setNeighborhood(neighborhood);
        project.setOpeningDate(openDate);
        project.setClosingDate(closeDate);
        project.setMaxOfficerSlots(officerSlots);

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

        // Add Business Rule: Check if project has active applications/bookings? (Depends on requirements)
        // Example: if (applicationRepo.findByProjectId(projectId).stream().anyMatch(a -> a.getStatus() != ApplicationStatus.UNSUCCESSFUL && a.getStatus() != ApplicationStatus.WITHDRAWN )) { // Assuming WITHDRAWN status exists
        //    System.err.println("Service Error: Cannot delete project " + projectId + " with active/successful applications or bookings.");
        //    return false;
        // }

        // Check if your IProjectRepository interface has a deleteById method
        // If not, you'll need to add it and implement it in the concrete repository.
        try {
             projectRepo.deleteById(projectId); // Assuming this method exists
             System.out.println("Service: Project " + projectId + " deleted successfully.");
             return true;
        } catch (UnsupportedOperationException e) {
             System.err.println("Service Error: Deleting projects is not supported by the repository implementation.");
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
         project.setVisibility(!project.isVisible()); // Toggle the boolean flag
         projectRepo.save(project);
         System.out.println("Service: Project " + projectId + " visibility set to: " + (project.isVisible() ? "ON" : "OFF"));
         return true;
    }

    @Override
    public List<Project> getAllProjects() {
        // DIP: Returns list from repository interface method
        Map<String, Project> projectMap = projectRepo.findAll();
        return (projectMap == null) ? new ArrayList<>() : new ArrayList<>(projectMap.values());
    }

    @Override
    public List<Project> getProjectsManagedBy(String managerNRIC) {
        if (managerNRIC == null || managerNRIC.isBlank()) {
            return new ArrayList<>();
        }
        return projectRepo.findAll().values().stream()
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

    // Keep your existing getVisibleProjectsForUser and isProjectEligibleForApplicant methods
    // as they handle the logic for viewing projects from an Applicant's perspective.
    @Override
    public List<Project> getVisibleProjectsForUser(User user) {
        LocalDate currentDate = LocalDate.now();
        List<Project> allProjects = new ArrayList<>(projectRepo.findAll().values());

        return allProjects.stream()
                .filter(Project::isVisible)
                .filter(project -> !currentDate.isAfter(project.getClosingDate()))
                .filter(project -> isProjectEligibleForApplicant(user, project))
                .sorted(Comparator.comparing(Project::getProjectName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private boolean isProjectEligibleForApplicant(User user, Project project) {
        if (user.getRole() == UserRole.HDB_MANAGER) {
            return false;
        }
        if (user.getRole() == UserRole.APPLICANT || user.getRole() == UserRole.HDB_OFFICER) {
            int age = user.getAge();
            MaritalStatus status = user.getMaritalStatus();
            if (project.getFlatTypes() == null || project.getFlatTypes().isEmpty()) {
                return false;
            }
            if (status == MaritalStatus.SINGLE && age >= 35) {
                return project.getFlatTypes().containsKey(FlatType.TWO_ROOM);
            } else if (status == MaritalStatus.MARRIED && age >= 21) {
                return true; // Eligible for any project with flats
            } else {
                return false;
            }
        }
        return false;
    }
}