package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.*;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.utils.IdGenerator;

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
        this.applicationRepo = Objects.requireNonNull(applicationRepo, "Application Repository cannot be null");
    }

    @Override
    public Project createProject(HDBManager manager, String name, String neighborhood,
            Map<String, ProjectFlatInfo> flatInfoMap,
            LocalDate openDate, LocalDate closeDate, int officerSlots) {

        // Input validation
        if (openDate == null || closeDate == null) {
            System.err.println("Service Error: Opening and Closing dates cannot be null.");
            return null;
        }
        // 1. Check if closing date is before opening date
        if (closeDate.isBefore(openDate)) {
            System.err.println("Service Error: Closing date (" + closeDate + ") cannot be before opening date ("
                    + openDate + "). Project creation failed.");
            return null;
        }
        // 2. Check HDB officer slots (1-10)
        if (officerSlots < 1 || officerSlots > 10) {
            System.err.println("Service Error: Max HDB Officer Slots must be between 1 and 10 (was " + officerSlots
                    + "). Project creation failed.");
            return null;
        }

        // Eligibility Check
        // Need to handle Collection<Project> type potentially returned by findAll()
        Map<String, Project> projectMap = projectRepo.findAll();
        Collection<Project> allProjects = (projectMap != null) ? projectMap.values() : Arrays.asList();
        if (!eligibilityService.checkManagerProjectHandlingEligibility(manager, openDate, closeDate, allProjects)) {
            System.err.println("Service Error: Manager " + manager.getNric()
                    + " is already handling another project during this application period. Project creation failed.");
            return null;
        }

        String projectId = IdGenerator.generateProjectId();

        // Convert Map<String, ProjectFlatInfo> to Map<FlatType, ProjectFlatInfo>
        Map<FlatType, ProjectFlatInfo> typedFlatInfoMap;
        try {
            typedFlatInfoMap = flatInfoMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> FlatType.valueOf(entry.getKey().trim().toUpperCase()),
                            Map.Entry::getValue));
        } catch (IllegalArgumentException e) {
            System.err.println("Service Error: Invalid FlatType string found in flatInfoMap keys: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Service Error: Failed to process flatInfoMap: " + e.getMessage());
            return null;
        }

        // Ensure both required flat types are present
        if (typedFlatInfoMap.size() != 2 || !typedFlatInfoMap.containsKey(FlatType.TWO_ROOM)
                || !typedFlatInfoMap.containsKey(FlatType.THREE_ROOM)) {
            System.err.println(
                    "Service Error: flatInfoMap must contain exactly TWO_ROOM and THREE_ROOM after conversion.");
            return null;
        }

        Project newProject = new Project(projectId, name, neighborhood, typedFlatInfoMap, openDate, closeDate,
                manager.getNric(), officerSlots);
        projectRepo.save(newProject);
        System.out.println("Service: Project " + name + " created successfully with ID: " + projectId);
        return newProject;
    }

    @Override
    public boolean editCoreProjectDetails(HDBManager manager, String projectId, String name, String neighborhood,
            LocalDate openDate, LocalDate closeDate, int officerSlots) {
        Project project = projectRepo.findById(projectId);

        // Input validation
        if (openDate == null || closeDate == null) {
            System.err.println("Service Error: Opening and Closing dates cannot be null.");
            return false;
        }
        // 1. Check if closing date is before opening date
        if (closeDate.isBefore(openDate)) {
            System.err.println("Service Error: Closing date (" + closeDate + ") cannot be before opening date ("
                    + openDate + "). Project edit failed.");
            return false;
        }
        // 2. Check HDB officer slots (1-10)
        if (officerSlots < 1 || officerSlots > 10) {
            System.err.println("Service Error: Max HDB Officer Slots must be between 1 and 10 (was " + officerSlots
                    + "). Project edit failed.");
            return false;
        }

        if (project == null) {
            System.err.println("Service Error: Project not found with ID: " + projectId);
            return false;
        }
        if (!project.getManagerNric().equals(manager.getNric())) {
            System.err.println("Service Error: Manager " + manager.getNric()
                    + " does not have permission to edit project " + projectId);
            return false;
        }
        // Check officer slot constraint against the actual list in the project object
        if (project.getApprovedOfficerNrics() != null && officerSlots < project.getApprovedOfficerNrics().size()) {
            System.err.println("Service Error: Cannot set max officer slots (" + officerSlots
                    + ") below the current number of approved officers (" + project.getApprovedOfficerNrics().size()
                    + ").");
            return false;
        }

        // Check Manager Concurrency Eligibility with NEW dates
        Map<String, Project> projectMap = projectRepo.findAll();
        // Exclude the current project being edited from the check list
        final String currentProjectId = projectId; // Final variable for lambda
        Collection<Project> otherProjects = (projectMap != null) ? projectMap.values().stream()
                .filter(p -> !p.getProjectId().equals(currentProjectId)) // Exclude self
                .collect(Collectors.toList())
                : Arrays.asList();

        if (!eligibilityService.checkManagerProjectHandlingEligibility(manager, openDate, closeDate, otherProjects)) {
            System.err.println("Service Error: The new dates for project " + projectId
                    + " overlap with another project managed by " + manager.getNric() + ". Edit failed.");
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
            System.err.println("Service Error: Manager " + manager.getNric()
                    + " does not have permission to delete project " + projectId);
            return false;
        }

        List<Application> projectApps = this.applicationRepo.findByProjectId(projectId);
        boolean hasActiveApps = projectApps.stream()
                .anyMatch(app -> app.getStatus() == ApplicationStatus.PENDING ||
                        app.getStatus() == ApplicationStatus.SUCCESSFUL);

        if (hasActiveApps) {
            System.err.println("Service Error: Cannot delete project " + projectId
                    + " because it has PENDING or SUCCESSFUL (unbooked) applications.");
            return false;
        }

        // Deletion attempt
        try {
            projectRepo.deleteById(projectId);
            System.out.println("Service: Project " + projectId + " deleted successfully.");
            return true;
        } catch (UnsupportedOperationException e) {
            System.err
                    .println("Service Error: Deleting projects is not supported by the repository: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println(
                    "Service Error: An error occurred while deleting project " + projectId + ": " + e.getMessage());
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
            System.err.println("Service Error: Manager " + manager.getNric()
                    + " does not have permission to change visibility for project " + projectId);
            return false;
        }
        // Use the setter from Project.java
        project.setVisibility(!project.isVisible());
        projectRepo.save(project);
        System.out.println(
                "Service: Project " + projectId + " visibility set to: " + (project.isVisible() ? "ON" : "OFF"));
        return true;
    }

    @Override
    public List<Project> getAllProjects(User user, Map<String, Object> filters) {
        // Authorization should be in Controller
        Map<String, Project> projectMap = projectRepo.findAll();
        if (projectMap == null || projectMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<Project> allProjects = new ArrayList<>(projectMap.values());

        Stream<Project> stream = allProjects.stream(); // Start with full stream

        // Apply optional filters from the map (Neighbourhood, Flat Type, Visibility)
        stream = applyOptionalFilters(stream, filters, true); // isStaffView = true

        // Apply DEFAULT Sorting by Project ID (last step before collect)
        stream = stream.sorted(Comparator.comparing(Project::getProjectId)); // Default sort

        return stream.collect(Collectors.toList());
    }

    @Override
    public List<Project> getProjectsManagedBy(String managerNRIC) {
        if (managerNRIC == null || managerNRIC.trim().isEmpty()) {
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
        if (projectId == null || projectId.trim().isEmpty()) {
            return null;
        }
        return projectRepo.findById(projectId);
    }

    // Overload without filters (calls the one with filters using empty map)
    @Override
    public List<Project> getVisibleProjectsForUser(User user) {
        return getVisibleProjectsForUser(user, Collections.emptyMap()); // Delegate to filter version
    }

    /**
     * Gets a list of projects that are currently visible and eligible for a
     * specific user
     * (typically an Applicant or Officer acting as one) to potentially apply for.
     * Filters based on project visibility, application closing date, and user
     * eligibility rules.
     * The list is sorted alphabetically by project name.
     *
     * @param user    The user for whom to filter the projects.
     * @param filters A map of optional filters (e.g., neighbourhood, flat type).
     * @return A List of eligible and visible Project objects, sorted by name.
     */
    @Override
    public List<Project> getVisibleProjectsForUser(User user, Map<String, Object> filters) {
        LocalDate currentDate = LocalDate.now();
        Map<String, Project> projectMap = projectRepo.findAll();
        if (projectMap == null || projectMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<Project> allProjects = new ArrayList<>(projectMap.values());

        Stream<Project> stream = allProjects.stream()
                // Base filters: Visible, Not Closed, Eligible for User Type
                .filter(Project::isVisible)
                .filter(project -> !currentDate.isAfter(project.getClosingDate()))
                .filter(project -> isProjectEligibleForApplicant(user, project));

        // Apply optional filters from the map (Neighbourhood, Flat Type)
        stream = applyOptionalFilters(stream, filters, false); // isStaffView = false

        // Apply DEFAULT Sorting by Project ID (last step before collect)
        stream = stream.sorted(Comparator.comparing(Project::getProjectId)); // Default sort

        return stream.collect(Collectors.toList());
    }

    /**
     * Helper method to check if a specific project is eligible for a given user
     * based on the BTO application rules (age, marital status, flat types offered).
     *
     * @param user    The user (Applicant or Officer) applying.
     * @param project The project being checked.
     * @return true if the user is eligible for this project, false otherwise.
     */
    private boolean isProjectEligibleForApplicant(User user, Project project) {
        // Managers cannot apply for BTOs.
        if (user.getRole() == UserRole.HDB_MANAGER) {
            return false;
        }

        // Consider both Applicants and Officers (who can act as applicants)
        if (user.getRole() == UserRole.APPLICANT || user.getRole() == UserRole.HDB_OFFICER) {

            // Officers might have additional restrictions (e.g., cannot apply for project
            // they handle)
            // but those checks belong in the *application submission* logic, not here in
            // the *viewing* logic.
            // This method checks only the basic BTO eligibility rules.

            int age = user.getAge();
            MaritalStatus status = user.getMaritalStatus();
            Map<FlatType, ProjectFlatInfo> flats = project.getFlatTypes();

            // Check if the project actually offers any flats
            if (flats == null || flats.isEmpty()) {
                return false; // Cannot apply to a project with no flats defined
            }

            // Apply rules from Assignment PDF Page 3:
            if (status == MaritalStatus.SINGLE && age >= 35) {
                // Singles >= 35: Eligible ONLY IF the project offers 2-Room flats.
                return project.getFlatTypes().containsKey(FlatType.TWO_ROOM);
            } else if (status == MaritalStatus.MARRIED && age >= 21) {
                // Married >= 21: Eligible for projects offering ANY flat type (2-Room or
                // 3-Room).
                // Since we checked above that getFlatTypes is not empty, they are eligible.
                return true;
            } else {
                // User does not meet the minimum criteria (e.g., Single < 35, Married < 21)
                return false;
            }
        }

        // Default case for any other unforeseen roles? Return false.
        return false;
    }

    private Stream<Project> applyOptionalFilters(Stream<Project> stream, Map<String, Object> filters,
            boolean isStaffView) {
        if (filters == null || filters.isEmpty()) {
            return stream; // No filters to apply
        }

        // Neighbourhood
        if (filters.containsKey("neighborhood")) {
            String neighborhood = (String) filters.get("neighborhood");
            if (neighborhood != null && !neighborhood.trim().isEmpty()) {
                stream = stream.filter(p -> p.getNeighborhood().equalsIgnoreCase(neighborhood.trim()));
            }
        }

        // Flat Type (Checks if project *offers* this type)
        if (filters.containsKey("flatType")) {
            try {
                FlatType flatType = (FlatType) filters.get("flatType"); // Assumes correct type in map
                if (flatType != null) {
                    stream = stream.filter(p -> p.getFlatTypes() != null && p.getFlatTypes().containsKey(flatType));
                }
            } catch (ClassCastException e) {
                System.err.println("Filter Error: Invalid object type for flatType filter.");
            }
        }

        // --- Staff Only Filters ---
        if (isStaffView) {
            // Visibility Filter
            if (filters.containsKey("visibility")) {
                try {
                    Boolean isVisible = (Boolean) filters.get("visibility");
                    if (isVisible != null) {
                        stream = stream.filter(p -> p.isVisible() == isVisible);
                    }
                } catch (ClassCastException e) {
                    System.err.println("Filter Error: Invalid object type for visibility filter.");
                }
            }
        }

        return stream;
    }
}
