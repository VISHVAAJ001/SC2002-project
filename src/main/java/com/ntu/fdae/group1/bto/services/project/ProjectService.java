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
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.*;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IOfficerRegistrationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.utils.IdGenerator;

/**
 * Service class for managing project-related operations in the BTO Management
 * System.
 * <p>
 * This class provides business logic for creating, editing, deleting, and
 * retrieving
 * project information. It interacts with repositories and other services to
 * enforce
 * application rules and ensure data consistency.
 * </p>
 * <p>
 * Key responsibilities include:
 * - Managing project lifecycle (creation, editing, deletion)
 * - Enforcing eligibility rules for managers and applicants
 * - Filtering and retrieving projects based on user roles and criteria
 * - Handling visibility toggles and officer registration constraints
 * </p>
 */
public class ProjectService implements IProjectService {

    /**
     * Repository for accessing and managing project data.
     */
    private final IProjectRepository projectRepo;

    /**
     * Service for checking eligibility rules for managers and applicants.
     */
    private final IEligibilityService eligibilityService;

    /**
     * Repository for accessing and managing application data.
     */
    private final IApplicationRepository applicationRepo;

    /**
     * Repository for accessing and managing officer registration data.
     */
    private final IOfficerRegistrationRepository officerRegRepo;

    /**
     * Constructs a new ProjectService with the required dependencies.
     *
     * @param projectRepo        Repository for project data
     * @param eligibilityService Service for eligibility checks
     * @param applicationRepo    Repository for application data
     * @param officerRegRepo     Repository for officer registration data
     * @throws NullPointerException if any of the required dependencies are null
     */
    public ProjectService(IProjectRepository projectRepo,
            IEligibilityService eligibilityService,
            IApplicationRepository applicationRepo,
            IOfficerRegistrationRepository officerRegRepo) {
        this.projectRepo = Objects.requireNonNull(projectRepo, "Project Repository cannot be null");
        this.eligibilityService = Objects.requireNonNull(eligibilityService, "Eligibility Service cannot be null");
        this.applicationRepo = Objects.requireNonNull(applicationRepo, "Application Repository cannot be null");
        this.officerRegRepo = Objects.requireNonNull(officerRegRepo, "Officer Registration Repository cannot be null");
    }

    /**
     * Creates a new project with the specified details.
     * <p>
     * Validates the input parameters, checks manager eligibility, and ensures
     * the project meets all business rules before saving it to the repository.
     * </p>
     *
     * @param manager      The HDB manager creating the project
     * @param name         The name of the project
     * @param neighborhood The neighborhood where the project is located
     * @param flatInfoMap  A map of flat types and their details
     * @param openDate     The opening date for applications
     * @param closeDate    The closing date for applications
     * @param officerSlots The maximum number of officer slots for the project
     * @return The created Project object, or null if creation fails
     */
    @Override
    public Project createProject(HDBManager manager, String name, String neighborhood,
            Map<String, ProjectFlatInfo> flatInfoMap,
            LocalDate openDate, LocalDate closeDate, int officerSlots) {

        // Input validation
        if (openDate == null || closeDate == null) {
            System.err.println("Service Error: Opening and Closing dates cannot be null.");
            return null;
        }
        // 1. Check if closing date is before or equal to opening date
        if (closeDate.isBefore(openDate) || closeDate.equals(openDate)) {
            System.err.println(
                    "Service Error: Closing date (" + closeDate + ") cannot be before or the same as opening date ("
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

        if (typedFlatInfoMap.isEmpty()) {
            System.err.println(
                    "Service Error: Project must offer at least one flat type (TWO_ROOM or THREE_ROOM) with units > 0.");
            return null;
        }

        Project newProject = new Project(projectId, name, neighborhood, typedFlatInfoMap, openDate, closeDate,
                manager.getNric(), officerSlots);
        projectRepo.save(newProject);
        System.out.println("Service: Project " + name + " created successfully with ID: " + projectId);
        return newProject;
    }

    /**
     * Edits the core details of an existing project.
     * <p>
     * Validates the input parameters, checks manager permissions, and ensures
     * the updated project meets all business rules before saving the changes.
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
        if (closeDate.isBefore(openDate) || closeDate.equals(openDate)) {
            System.err.println(
                    "Service Error: Closing date (" + closeDate + ") cannot be before or the same as opening date ("
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

    /**
     * Deletes a project if it meets the deletion criteria.
     * <p>
     * Ensures the project has no active applications and that the manager has
     * the necessary permissions to delete it.
     * </p>
     *
     * @param manager   The HDB manager requesting the deletion
     * @param projectId The ID of the project to delete
     * @return true if the project was successfully deleted, false otherwise
     */
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

    /**
     * Toggles the visibility of a project.
     * <p>
     * Allows the manager to make a project visible or hidden, depending on its
     * current state.
     * </p>
     *
     * @param manager   The HDB manager requesting the visibility change
     * @param projectId The ID of the project to toggle visibility for
     * @return true if the visibility was successfully toggled, false otherwise
     */
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

    /**
     * Retrieves all projects visible to a specific user, with optional filters.
     * <p>
     * Filters can include criteria such as neighborhood, flat type, and visibility.
     * </p>
     *
     * @param user    The user requesting the projects
     * @param filters A map of optional filters to apply
     * @return A list of projects matching the criteria
     */
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

    /**
     * Retrieves all projects managed by a specific manager.
     *
     * @param managerNRIC The NRIC of the manager
     * @return A list of projects managed by the specified manager
     */
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
    public List<Project> getProjectsManagedBy(String managerNRIC, Map<String, Object> filters) { // Add filters param
        if (managerNRIC == null || managerNRIC.trim().isEmpty()) {
            return Collections.emptyList(); // Use Collections.emptyList() for Java 8
        }
        Map<String, Project> projectMap = projectRepo.findAll();
        if (projectMap == null || projectMap.isEmpty()) {
            return Collections.emptyList();
        }

        Stream<Project> stream = projectMap.values().stream()
                .filter(p -> managerNRIC.equals(p.getManagerNric())); // Mandatory filter first

        // Apply optional filters (pass isStaffView=true as manager is staff)
        stream = applyOptionalFilters(stream, filters, true);

        // Apply default sorting (e.g., by Project ID or Name)
        stream = stream.sorted(Comparator.comparing(Project::getProjectId));

        return stream.collect(Collectors.toList());
    }

    /**
     * Retrieves a project by its unique identifier.
     *
     * @param projectId The ID of the project to retrieve
     * @return The Project object, or null if not found
     */
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
            // --- Base Filters ---
            // 1. Must be marked as Visible
            .filter(Project::isVisible)

            // 2. Must be within the Active Application Period
            .filter(project -> {
                // Check for non-null dates first
                if (project.getOpeningDate() == null || project.getClosingDate() == null) {
                    return false; // Cannot apply if dates are missing
                }
                // Check if currentDate is ON or AFTER openingDate AND ON or BEFORE closingDate
                return !currentDate.isBefore(project.getOpeningDate()) &&
                       !currentDate.isAfter(project.getClosingDate());
            })

            // 3. User must meet basic eligibility for the project (age, marital status vs flat types offered)
            .filter(project -> isProjectEligibleForApplicant(user, project));

        // Apply optional filters from the map (Neighbourhood, Flat Type)
        stream = applyOptionalFilters(stream, filters, false); // isStaffView = false

        // Apply DEFAULT Sorting by Project ID (last step before collect)
        stream = stream.sorted(Comparator.comparing(Project::getProjectId)); // Default sort

        return stream.collect(Collectors.toList());
    }

    @Override
    public List<Project> getProjectsAvailableForOfficerRegistration(HDBOfficer officer) {
        if (officer == null) {
            return Collections.emptyList();
        }
        String officerNric = officer.getNric();

        // 1. Find project applied for by this officer (if any)
        Application officerApplication = applicationRepo.findByApplicantNric(officerNric);
        String appliedProjectId = (officerApplication != null) ? officerApplication.getProjectId() : null;

        // 2. Get ALL registrations for this officer (needed for period overlap check)
        List<OfficerRegistration> allMyRegistrations = officerRegRepo.findByOfficerNric(officerNric);

        // 3. Separate out PENDING/APPROVED registrations and their project IDs/periods
        // We need Project details to check periods, so fetch projects for existing regs
        Map<String, OfficerRegistration> pendingOrApprovedRegsMap = allMyRegistrations.stream()
                .filter(reg -> reg.getStatus() == OfficerRegStatus.PENDING
                        || reg.getStatus() == OfficerRegStatus.APPROVED)
                .collect(Collectors.toMap(OfficerRegistration::getProjectId, reg -> reg, (reg1, reg2) -> reg1));

        // Fetch projects corresponding to these pending/approved registrations to get
        // their dates
        Map<String, Project> projectsOfExistingRegs = pendingOrApprovedRegsMap.keySet().stream()
                .map(projectRepo::findById) // Assumes findById returns Project or null
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Project::getProjectId, proj -> proj));

        // 4. Fetch all potential target projects
        List<Project> allProjects = new ArrayList<>(projectRepo.findAll().values());

        // 5. Filter based on Officer Registration rules
        List<Project> availableProjects = allProjects.stream()

                // Rule 1: Cannot register for the project they applied to as an applicant
                .filter(targetProject -> appliedProjectId == null
                        || !targetProject.getProjectId().equals(appliedProjectId))

                // Rule 2: Cannot register for a project they already have a Pending/Approved
                // registration for
                .filter(targetProject -> !pendingOrApprovedRegsMap.containsKey(targetProject.getProjectId()))

                // Rule 3: Cannot register if they have another Pending/Approved registration
                // for a DIFFERENT project whose application period OVERLAPS with this target
                // project.
                .filter(targetProject -> {
                    // If no other pending/approved regs exist, this rule passes automatically
                    if (pendingOrApprovedRegsMap.isEmpty()) {
                        return true;
                    }
                    // Check against each existing pending/approved registration's project period
                    for (String existingRegProjectId : pendingOrApprovedRegsMap.keySet()) {
                        Project existingRegProject = projectsOfExistingRegs.get(existingRegProjectId);
                        if (existingRegProject != null) { // Ensure we have project details for the existing reg
                            if (periodsOverlap(targetProject, existingRegProject)) {
                                return false; // Found an overlap with another registration, cannot register for
                                              // targetProject
                            }
                        } else {
                            // Handle case where project details for an existing registration couldn't be
                            // found?
                            // Maybe log a warning. For safety, could assume overlap or filter out target.
                            System.err.println(
                                    "Warning: Could not find project details for existing registration on project ID: "
                                            + existingRegProjectId);
                            return false; // Safer to block registration if details are missing
                        }
                    }
                    return true; // No overlapping registrations found
                })

                // 6. Sort the result
                .sorted(Comparator.comparing(Project::getProjectName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        return availableProjects;
    }

    /**
     * Checks if the application periods of two projects overlap.
     * 
     * Overlap occurs if one project's start date is before or on the other's end
     * date,
     * AND that first project's end date is after or on the other's start date.
     *
     * @param p1 Project 1
     * @param p2 Project 2
     * @return true if the periods overlap, false otherwise.
     */
    private boolean periodsOverlap(Project p1, Project p2) {
        if (p1 == null || p2 == null || p1.getOpeningDate() == null || p1.getClosingDate() == null ||
                p2.getOpeningDate() == null || p2.getClosingDate() == null) {
            return false; // Cannot determine overlap without valid dates
        }

        LocalDate start1 = p1.getOpeningDate();
        LocalDate end1 = p1.getClosingDate();
        LocalDate start2 = p2.getOpeningDate();
        LocalDate end2 = p2.getClosingDate();

        // Overlap condition: (StartA <= EndB) and (EndA >= StartB)
        return !start1.isAfter(end2) && !end1.isBefore(start2);
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

    /**
     * Applies optional filters to a stream of projects.
     * <p>
     * Filters can include neighborhood, flat type, and visibility, depending on
     * the user's role and the provided criteria.
     * </p>
     *
     * @param stream      The stream of projects to filter
     * @param filters     A map of filter criteria
     * @param isStaffView true if the user is a staff member, false otherwise
     * @return The filtered stream of projects
     */
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

        // Flat Type (Checks if project *offers* this type AND has units > 0)
        if (filters.containsKey("flatType")) {
            try {
                FlatType flatType = (FlatType) filters.get("flatType");
                if (flatType != null) {
                    stream = stream.filter(project -> {
                        // Check 1: Does the project have flat info
                        if (project.getFlatTypes() == null)
                            return false;
                        // Check 2: Does the project offer this specific flat type?
                        ProjectFlatInfo info = project.getFlatTypes().get(flatType);
                        // Check 3: Does this flat type actually have units defined?
                        // Filter based on whether units are CURRENTLY available (remaining > 0)
                        return info != null && info.getRemainingUnits() > 0;
                    });
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
