package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.*;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;

public class ProjectService implements IProjectService {
    private final IProjectRepository projectRepo;
    private final IUserRepository userRepo;
    private final IEligibilityService eligibilityService;

    public ProjectService(IProjectRepository projectRepo, IUserRepository userRepo,
            IEligibilityService eligibilityService) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.eligibilityService = eligibilityService;
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
        // Find the project by ID in the repository
        Project project = projectRepo.findById(projectId);
        if (project == null) {
            // Handle the case where the project is not found
            throw new IllegalArgumentException("Project with ID " + projectId + " not found.");
        }

        // Return the found project
        return project;
    }

    /**
     * Gets a list of projects that are currently visible and eligible for a
     * specific user
     * (typically an Applicant or Officer acting as one) to potentially apply for.
     * Filters based on project visibility, application closing date, and user
     * eligibility rules.
     * The list is sorted alphabetically by project name.
     *
     * @param user The user for whom to filter the projects.
     * @return A List of eligible and visible Project objects, sorted by name.
     */
    @Override
    public List<Project> getVisibleProjectsForUser(User user) {
        // 1. Get current date for filtering based on application period (FAQ pg 35)
        LocalDate currentDate = LocalDate.now();

        // 2. Fetch all projects (handle potential map return type)
        List<Project> allProjects = new ArrayList<>(projectRepo.findAll().values());

        // 3. Filter the projects using a stream
        List<Project> filteredProjects = allProjects.stream()
                // Filter 1: Project must be explicitly set to visible
                .filter(Project::isVisible)

                // Filter 2: Application period must still be active (current date <= closing
                // date)
                // (FAQ pg 36: not visible for *new* applications after closing date)
                .filter(project -> !currentDate.isAfter(project.getClosingDate())) // Keep if today is on or before
                                                                                   // closing date

                // Filter 3: Project must be eligible based on the user's profile (age, status,
                // required flats)
                .filter(project -> isProjectEligibleForApplicant(user, project)) // Delegate eligibility check

                // 4. Sort the resulting list alphabetically by project name (case-insensitive)
                .sorted(Comparator.comparing(Project::getProjectName, String.CASE_INSENSITIVE_ORDER))

                // 5. Collect the results into a new list
                .collect(Collectors.toList());

        return filteredProjects;
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

            // Check if the project actually offers any flats
            if (project.getFlatTypes() == null || project.getFlatTypes().isEmpty()) {
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

}
