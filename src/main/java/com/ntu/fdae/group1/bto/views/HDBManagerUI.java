package com.ntu.fdae.group1.bto.views;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ReportController;
import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.exceptions.AuthorizationException;
import com.ntu.fdae.group1.bto.exceptions.InvalidInputException;
import com.ntu.fdae.group1.bto.exceptions.RegistrationException;

/**
 * User interface for HDB Manager users in the BTO Management System.
 * <p>
 * This class provides a console-based interface for HDB managers to interact
 * with
 * the system. It allows managers to perform high-level administrative
 * operations
 * including:
 * - Creating, editing, and deleting BTO projects
 * - Managing project visibility
 * - Reviewing officer registrations for project handling
 * - Reviewing BTO applications and withdrawal requests
 * - Responding to enquiries
 * - Generating system reports
 * </p>
 * <p>
 * The UI follows a menu-driven approach, with options for project management,
 * administrative tasks, communication, and reporting. It leverages various UI
 * helper
 * classes to manage complex operations while maintaining separation of
 * concerns.
 * </p>
 */
public class HDBManagerUI extends BaseUI {
    /**
     * The authenticated HDB manager user currently using the interface.
     */
    private final HDBManager user;

    /**
     * Controller for user-related operations.
     */
    @SuppressWarnings("unused")
    private final UserController userController;

    /**
     * Controller for project-related operations.
     */
    private final ProjectController projectController;

    /**
     * Controller for application-related operations.
     */
    private final ApplicationController applicationController;

    /**
     * Controller for officer registration operations.
     */
    private final OfficerRegistrationController officerRegController;

    /**
     * Controller for enquiry-related operations.
     */
    private final EnquiryController enquiryController;

    /**
     * Controller for report generation operations.
     */
    private final ReportController reportController;

    /**
     * Controller for authentication-related operations.
     */
    @SuppressWarnings("unused")
    private final AuthenticationController authController;

    /**
     * Helper for project-related UI operations.
     */
    private final ProjectUIHelper projectUIHelper;

    /**
     * Helper for account-related UI operations.
     */
    private final AccountUIHelper accountUIHelper;

    /**
     * Helper for application-related UI operations.
     */
    private final ApplicationUIHelper applicationUIHelper;

    /**
     * Helper for enquiry-related UI operations.
     */
    private final EnquiryUIHelper enquiryUIHelper;

    /**
     * Helper for officer registration UI operations.
     */
    private final OfficerRegUIHelper officerRegUIHelper;

    /**
     * Current filters applied to project listings.
     */
    private Map<String, Object> currentProjectFilters;

    /**
     * Constructs a new HDBManagerUI with the specified dependencies.
     *
     * @param user       The authenticated HDB manager user
     * @param userCtrl   Controller for user operations
     * @param projCtrl   Controller for project operations
     * @param appCtrl    Controller for application operations
     * @param offRegCtrl Controller for officer registration operations
     * @param enqCtrl    Controller for enquiry operations
     * @param reportCtrl Controller for report generation operations
     * @param authCtrl   Controller for authentication operations
     * @param scanner    Scanner for reading user input
     * @throws NullPointerException if any parameter is null
     */
    public HDBManagerUI(HDBManager user,
            UserController userCtrl,
            ProjectController projCtrl,
            ApplicationController appCtrl,
            OfficerRegistrationController offRegCtrl,
            EnquiryController enqCtrl,
            ReportController reportCtrl,
            AuthenticationController authCtrl,
            Scanner scanner) {
        super(scanner);
        this.user = Objects.requireNonNull(user);
        this.userController = Objects.requireNonNull(userCtrl);
        this.projectController = Objects.requireNonNull(projCtrl);
        this.applicationController = Objects.requireNonNull(appCtrl);
        this.officerRegController = Objects.requireNonNull(offRegCtrl);
        this.enquiryController = Objects.requireNonNull(enqCtrl);
        this.reportController = Objects.requireNonNull(reportCtrl);
        this.authController = Objects.requireNonNull(authCtrl);
        this.projectUIHelper = new ProjectUIHelper(this, userCtrl, projCtrl);
        this.accountUIHelper = new AccountUIHelper(this, authCtrl);
        this.applicationUIHelper = new ApplicationUIHelper(this, appCtrl, projCtrl, userCtrl);
        this.enquiryUIHelper = new EnquiryUIHelper(this, userCtrl, projCtrl);
        this.officerRegUIHelper = new OfficerRegUIHelper(this, projCtrl, userCtrl);
        this.currentProjectFilters = new HashMap<>();
    }

    /**
     * Displays the main menu for HDB manager users and handles their selections.
     * <p>
     * This method shows a menu of options available to HDB managers, including
     * project management, administrative tasks, communication, and reporting.
     * It runs in a loop until the user chooses to log out, delegating to specific
     * handler methods based on user input.
     * </p>
     */
    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            if (user != null) {
                displayHeader("HDB Manager Menu - Welcome " + user.getName() + " (" + user.getAge() + ", "
                        + user.getMaritalStatus() + ")");
            } else {
                displayHeader("Applicant Menu - Welcome User (Age, Marital Status)");
            }

            System.out.println("--- Manager Project Role ---");
            System.out.println("[1] Manage My Projects (Create/Edit/Delete/Visbility)"); // Combined
            System.out.println("[2] View All Created Projects");
            System.out.println("[3] View My Managed Projects");
            System.out.println("-------------------------------------");
            System.out.println("--- Manager Tasks ---");
            System.out.println("[4] Review Officer Registration (Pending/Approved)");
            System.out.println("[5] Review Pending BTO Applications (Approve/Reject)");
            System.out.println("[6] Review Pending Application Withdrawals (Approve/Reject)");
            System.out.println("-------------------------------------");
            System.out.println("--- Communication & Reports ---");
            System.out.println("[7] View/Reply Enquiries");
            System.out.println("[8] Generate Booking Report");
            System.out.println("-------------------------------------");
            System.out.println("--- Account ---");
            System.out.println("[9] Change Password");
            System.out.println("-------------------------------------");
            System.out.println("[0] Logout");
            System.out.println("=====================================");

            int choice = promptForInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        handleManageProjects();
                        break;
                    case 2:
                        handleViewAllProjects();
                        break;
                    case 3:
                        handleViewMyProjects();
                        break;

                    case 4:
                        handleReviewOfficerRegistrations();
                        break;
                    case 5:
                        handleReviewApplications();
                        break;
                    case 6:
                        handleReviewWithdrawals();
                        break;

                    case 7:
                        handleViewReplyEnquiries();
                        break;
                    case 8:
                        handleGenerateReport();
                        break;

                    case 9:
                        if (handleChangePassword())
                            keepRunning = false; // Could just remove the break here, but this is clearer
                    case 0:
                        displayMessage("Logging out...");
                        keepRunning = false;
                        break;
                    default:
                        displayError("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                displayError("An error occurred: " + e.getMessage());
            }

            if (keepRunning && choice != 0) {
                pause();
            }

        }
    }

    /**
     * Handles the sub-menu for managing BTO projects.
     * <p>
     * Displays options for project management including creation, editing,
     * deletion, and visibility toggling. Delegates to specific handler methods
     * based on user selection.
     * </p>
     *
     * @throws RegistrationException If an error occurs while accessing officer
     *                               registration data
     * @throws InvalidInputException If invalid input is provided for project
     *                               management operations
     */
    private void handleManageProjects() throws RegistrationException, InvalidInputException {
        displayHeader("Manage BTO Projects");
        System.out.println("1. Create New Project");
        System.out.println("2. Edit Existing Project");
        System.out.println("3. Delete Project");
        System.out.println("4. Toggle Project Visibility");
        System.out.println("0. Back to Main Menu");
        int choice = promptForInt("Enter choice: ");

        switch (choice) {
            case 1:
                handleCreateProject();
                break;
            case 2:
                handleEditProject();
                break;
            case 3:
                handleDeleteProject();
                break;
            case 4:
                handleToggleVisibility();
                break;
            case 0:
                break;
            default:
                displayError("Invalid choice.");
        }
    }

    /**
     * Handles the workflow for creating a new BTO project.
     * <p>
     * This method guides the manager through:
     * - Entering basic project details (name, neighborhood, dates)
     * - Specifying the available flat types and unit counts
     * - Setting officer assignment slots
     * </p>
     * <p>
     * After collecting all required information, it delegates to the
     * ProjectController
     * to create the new project and displays the result to the user.
     * </p>
     *
     * @throws RegistrationException If an error occurs during project creation
     *                               related to officer registration
     * @throws InvalidInputException If invalid input is provided for project
     *                               creation
     */
    private void handleCreateProject() throws RegistrationException, InvalidInputException {
        String name = promptForInput("Enter Project Name: ");
        String neighborhood = promptForInput("Enter Neighborhood: ");
        LocalDate openDate = promptForDate("Enter Application Opening Date: ");
        LocalDate closeDate = promptForDate("Enter Application Closing Date: ");
        int officerSlots = promptForInt("Enter Max HDB Officer Slots (1-10): ");

        Map<String, ProjectFlatInfo> flatInfoMap = new HashMap<>();
        System.out.println("--- Enter Flat Details (Enter 0 units if type is not offered) ---");
        for (FlatType type : Arrays.asList(FlatType.TWO_ROOM, FlatType.THREE_ROOM)) {
            int totalUnits = promptForInt("Enter Total Units for " + type.name() + ": ");
            if (totalUnits > 0) {
                ProjectFlatInfo info = new ProjectFlatInfo(type, totalUnits, totalUnits, 0.0); // remaining = total
                flatInfoMap.put(type.name(), info); // Use enum name as String key
                displayMessage("Added " + type.name() + " with " + totalUnits + " units.");
            } else {
                displayMessage("Skipping " + type.name() + " as 0 units were entered.");
            }
        }

        if (flatInfoMap.isEmpty()) {
            displayError("Project creation failed: At least one flat type must have more than 0 units.");
            return;
        }

        // Call controller with the Map<String, ProjectFlatInfo>
        Project createdProject = projectController.createProject(user, name, neighborhood, flatInfoMap, openDate,
                closeDate, officerSlots);

        if (createdProject != null) {
            displayMessage("Project '" + createdProject.getProjectName() + "' created successfully with ID: "
                    + createdProject.getProjectId());
        } else {
            displayError("Project creation failed (check previous errors).");
        }
    }

    /**
     * Handles the workflow for editing an existing BTO project.
     * <p>
     * This method guides the manager through:
     * - Selecting a project to edit from those they manage
     * - Viewing the current project details including pending officer registrations
     * - Updating project attributes (name, neighborhood, dates, officer slots)
     * </p>
     * <p>
     * Changes are validated before being submitted to the ProjectController, and
     * both success and failure outcomes are reported to the user.
     * </p>
     *
     * @throws InvalidInputException If invalid input is provided for project
     *                               editing
     */
    private void handleEditProject() throws InvalidInputException {
        displayHeader("Edit Existing Project");

        // --- Step 1: Get projects managed by the current manager ---
        List<Project> myProjects;
        try {
            // Assuming getProjectsManagedBy only needs the user and doesn't throw checked
            // exceptions
            // other than potential RuntimeExceptions if the service fails.
            myProjects = projectController.getProjectsManagedBy(user);
        } catch (Exception e) {
            displayError("Error retrieving your managed projects: " + e.getMessage());
            // logger.log(Level.SEVERE, "Error retrieving managed projects", e);
            return; // Cannot proceed without the list
        }

        if (myProjects.isEmpty()) {
            displayMessage("You are not currently managing any projects to edit.");
            return;
        }

        // --- Step 2: Select the project to edit ---
        Project projectToEdit = this.projectUIHelper.selectProjectFromList(myProjects, "Select Project to Edit");
        if (projectToEdit == null) {
            // User selected 'Back'
            return;
        }

        // --- Step 3: Fetch Pending Count & Display Details ---
        try {
            // Fetch the pending count specifically for the selected project
            int projectSpecificPendingCount = officerRegController.getPendingRegistrationCountForProject(
                    this.user, projectToEdit.getProjectId());

            // Display the current details *including* the correct pending count
            this.projectUIHelper.displayStaffProjectDetails(projectToEdit, projectSpecificPendingCount);

        } catch (AuthorizationException ae) {
            // Catch auth error fetching the count (unlikely if getProjectsManagedBy worked,
            // but good practice)
            displayError("Authorization Error fetching project details: " + ae.getMessage());
            return; // Don't proceed if details couldn't be fully displayed
        } catch (IllegalArgumentException iae) {
            displayError("Internal Error: Invalid arguments fetching project details. " + iae.getMessage());
            return;
        } catch (RuntimeException re) {
            // Catch other errors fetching the count (project not found, service error)
            displayError("Error fetching project details: " + re.getMessage());
            // logger.log(Level.SEVERE, "Runtime error fetching project details for edit",
            // re);
            return; // Don't proceed if details couldn't be fully displayed
        }

        // --- Step 4: Prompt for Edits ---
        displayMessage("\nEnter new details (leave blank or enter just spaces to keep current value):");

        // Use helper methods that handle keeping the original value if input is
        // blank/spaces
        String name = promptForOptionalInput(
                "New Project Name [" + projectToEdit.getProjectName() + "]: ",
                projectToEdit.getProjectName()); // Pass original as default

        String neighborhood = promptForOptionalInput(
                "New Neighborhood [" + projectToEdit.getNeighborhood() + "]: ",
                projectToEdit.getNeighborhood());

        LocalDate openDate = promptForDateOrKeep(
                "New Opening Date (YYYY-MM-DD) [" + formatDateSafe(projectToEdit.getOpeningDate()) + "]:", // Use local
                                                                                                           // formatDate
                projectToEdit.getOpeningDate());

        LocalDate closeDate = promptForDateOrKeep(
                "New Closing Date (YYYY-MM-DD) [" + formatDateSafe(projectToEdit.getClosingDate()) + "]:", // Use local
                                                                                                           // formatDate
                projectToEdit.getClosingDate());

        int officerSlots = promptForIntOrKeep( // <<< Declaration is here
                "New Max Officer Slots [" + projectToEdit.getMaxOfficerSlots() + "] (Range: 1-10):",
                projectToEdit.getMaxOfficerSlots());

        // Optional: Add validation step here for officerSlots range if not done in
        // prompt
        if (officerSlots < 1 || officerSlots > 10) {
            displayError("Invalid number of officer slots. Must be between 1 and 10. Edit cancelled.");
            return;
        }
        // Optional: Add validation for dates (close date >= open date)
        if (closeDate.isBefore(openDate)) {
            displayError("Closing date cannot be before the opening date. Edit cancelled.");
            return;
        }

        // --- Step 5: Call Controller to Perform Edit ---
        try {
            boolean success = projectController.editProject(
                    user, // Authenticated manager
                    projectToEdit.getProjectId(), // ID of project to edit
                    name, // Use value returned by prompt (original or new)
                    neighborhood, // Use value returned by prompt
                    openDate, // Use value returned by prompt
                    closeDate, // Use value returned by prompt
                    officerSlots); // Use value returned by prompt

            if (success) {
                displayMessage("Project '" + name + "' updated successfully."); // Use updated name for feedback
            } else {
                // The controller ideally should throw an exception if it fails,
                // rather than returning false, to indicate *why* it failed.
                // If it MUST return boolean, the error message here is generic.
                displayError("Project update failed. Please check logs or contact support.");
            }
        } catch (Exception e) { // Catch runtime exceptions from the controller/service during edit
            displayError("An unexpected error occurred during project update: " + e.getMessage());
            // logger.log(Level.SEVERE, "Error editing project " +
            // projectToEdit.getProjectId(), e);
        }
    }

    /**
     * Handles the workflow for deleting a BTO project.
     * <p>
     * This method guides the manager through:
     * - Selecting a project to delete from those they manage
     * - Confirming the deletion request with appropriate warnings
     * </p>
     * <p>
     * Deletion is subject to business rules (e.g., no active applications).
     * The outcome of the deletion request is displayed to the user.
     * </p>
     */
    private void handleDeleteProject() {
        displayHeader("Delete Project");
        List<Project> myProjects = projectController.getProjectsManagedBy(this.user);

        // Delegate to Helper
        Project projectToDelete = this.projectUIHelper.selectProjectFromList(myProjects, "Select Project to Delete");
        if (projectToDelete == null)
            return;

        if (promptForConfirmation(
                "WARNING: Deleting a project might be irreversible and subject to rules (e.g., no active applications). Proceed?: ")) {
            boolean success = projectController.deleteProject(user, projectToDelete.getProjectId());
            if (success) {
                displayMessage("Project deletion request processed.");
            } else {
                displayError("Project deletion failed or not allowed (check logs/previous errors).");
            }
        } else {
            displayMessage("Deletion cancelled.");
        }
    }

    /**
     * Handles the workflow for toggling a project's visibility status.
     * <p>
     * This method guides the manager through:
     * - Selecting a project to modify from those they manage
     * - Viewing the current visibility status
     * - Confirming the toggle operation
     * </p>
     * <p>
     * The visibility change is delegated to the ProjectController, and
     * the outcome is reported to the user.
     * </p>
     */
    private void handleToggleVisibility() {
        displayHeader("Toggle Project Visibility");
        List<Project> myProjects = projectController.getProjectsManagedBy(this.user);

        // Delegate to Helper
        Project projectToToggle = this.projectUIHelper.selectProjectFromList(myProjects,
                "Select Project to Toggle Visibility");
        if (projectToToggle == null)
            return;

        displayMessage("Current visibility: " + (projectToToggle.isVisible() ? "ON" : "OFF"));
        if (promptForConfirmation(
                "Toggle visibility for project '" + projectToToggle.getProjectName() + "'?: ")) {
            boolean success = projectController.toggleProjectVisibility(user, projectToToggle.getProjectId());
            if (success) {
                displayMessage("Visibility toggled successfully.");
            } else {
                displayError("Failed to toggle visibility.");
            }
        } else {
            displayMessage("Operation cancelled.");
        }
    }

    /**
     * Handles the workflow for viewing all BTO projects in the system.
     * <p>
     * This method allows managers to:
     * - Apply or modify filters to the project list
     * - View a list of all projects matching the selected filters
     * - Select a specific project to view its details
     * </p>
     * <p>
     * For selected projects, their details are displayed including pending
     * officer registration counts.
     * </p>
     *
     * @throws AuthorizationException If the manager doesn't have sufficient
     *                                privileges
     */
    private void handleViewAllProjects() throws AuthorizationException {
        displayHeader("All BTO Projects - View & Filter");

        boolean filtersWereActive = !currentProjectFilters.isEmpty(); // Check if filters exist *before* asking
        if (filtersWereActive) {
            System.out.println("Current filters are active:");
            for (Map.Entry<String, Object> entry : currentProjectFilters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                // Format the value nicely (especially for enums)
                String valueStr = (value instanceof Enum) ? ((Enum<?>) value).name() : value.toString();
                System.out.println("  - " + key + ": " + valueStr);
            }
            System.out.println("----------------------------------");
            System.out.println("\nFilter Options:");
            System.out.println("[1] Keep current filters");
            System.out.println("[2] Clear current filters and view all");
            System.out.println("[3] Change/Set new filters");
            System.out.println("[0] Back"); // Option to back out entirely

            int filterAction = promptForInt("Choose filter action: ");

            switch (filterAction) {
                case 1:
                    // Keep filters - Do nothing, proceed with currentProjectFilters
                    displayMessage("Keeping existing filters.");
                    break;
                case 2:
                    // Clear filters and view all
                    this.currentProjectFilters.clear();
                    displayMessage("Filters cleared.");
                    // Proceed with empty filters map
                    break;
                case 3:
                    // Change/Set new filters
                    displayMessage("Clearing old filters to set new ones.");
                    this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true, false); // Get new filters
                    break;
                case 0:
                default: // Includes Back or invalid choice
                    displayMessage("Returning to main menu.");
                    return; // Exit the handleView method
            }
        } else {
            // No filters were active, ask if they want to apply some now
            if (promptForConfirmation("Apply filters before viewing?:")) {
                this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true, false);
            } else {
                this.currentProjectFilters.clear(); // Ensure empty if they say no
            }
        }

        List<Project> projectsToDisplay;
        try {
            // Pass the authenticated manager and the filter map
            projectsToDisplay = projectController.getAllProjects(this.user, currentProjectFilters);
        } catch (AuthorizationException ae) {
            displayError("Authorization Error: " + ae.getMessage());
            return; // Cannot proceed
        } catch (Exception e) {
            // Catch potential RuntimeExceptions from the controller/service during project
            // fetch
            displayError("Error retrieving projects: " + e.getMessage());
            return; // Cannot proceed
        }

        // --- Display Results ---
        if (projectsToDisplay.isEmpty()) {
            // Check if filter is empty
            if (currentProjectFilters != null && !currentProjectFilters.isEmpty()) {
                displayMessage("No projects match the specified filters.");
            } else {
                displayMessage("No projects found in the system.");
            }
        } else {
            // Use the ProjectUIHelper to display the list and handle selection
            String listTitle = (currentProjectFilters != null && !currentProjectFilters.isEmpty())
                    ? "Filtered Projects (" + projectsToDisplay.size() + " found)"
                    : "All Projects (" + projectsToDisplay.size() + " found)";
            Project selected = this.projectUIHelper.selectProjectFromList(projectsToDisplay, listTitle);

            // If the user selected a project (didn't choose 'Back')
            if (selected != null) {
                try {
                    // Get the PENDING registration count SPECIFICALLY for the selected project.
                    // Pass the authenticated manager (this.user) and the project ID.
                    int projectSpecificPendingCount = officerRegController.getPendingRegistrationCountForProject(
                            this.user, selected.getProjectId());

                    // Pass the project and the CORRECT pending count to the display method
                    this.projectUIHelper.displayStaffProjectDetails(selected, projectSpecificPendingCount);

                } catch (AuthorizationException ae) {
                    // Catch auth error specifically from getPendingRegistrationCountForProject
                    // (e.g., manager doesn't own the selected project)
                    displayError("Authorization Error fetching project details: " + ae.getMessage());
                } catch (IllegalArgumentException iae) {
                    // Catch programmer errors like null manager/projectId passed to controller
                    displayError("Internal Error: Invalid arguments fetching project details. " + iae.getMessage());
                    // logger.log(Level.WARNING, "Illegal arguments passed", iae);
                } catch (RuntimeException re) {
                    // Catch other errors from getPendingRegistrationCountForProject
                    // (e.g., project not found by ID in controller, service/repo errors)
                    displayError("Error fetching project details: " + re.getMessage());
                }
            }
            // If selected == null, the user chose 'Back' from the list. Return.
        }
    }

    /**
     * Handles the workflow for viewing BTO projects managed by the current user.
     * <p>
     * Similar to handleViewAllProjects but filters for projects where the
     * current manager is assigned as the manager. Allows the user to:
     * - Apply or modify filters to their project list
     * - View a list of their assigned projects matching the selected filters
     * - Select a specific project to view its details
     * </p>
     *
     * @throws AuthorizationException If the manager doesn't have sufficient
     *                                privileges
     */
    private void handleViewMyProjects() throws AuthorizationException {
        displayHeader("My Managed BTO Projects - View & Filter");

        boolean filtersWereActive = !currentProjectFilters.isEmpty(); // Check if filters exist *before* asking
        if (filtersWereActive) {
            System.out.println("Current filters are active:");
            for (Map.Entry<String, Object> entry : currentProjectFilters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                // Format the value nicely (especially for enums)
                String valueStr = (value instanceof Enum) ? ((Enum<?>) value).name() : value.toString();
                System.out.println("  - " + key + ": " + valueStr);
            }
            System.out.println("----------------------------------");
            System.out.println("\nFilter Options:");
            System.out.println("[1] Keep current filters");
            System.out.println("[2] Clear current filters and view all");
            System.out.println("[3] Change/Set new filters");
            System.out.println("[0] Back"); // Option to back out entirely

            int filterAction = promptForInt("Choose filter action: ");

            switch (filterAction) {
                case 1:
                    // Keep filters - Do nothing, proceed with currentProjectFilters
                    displayMessage("Keeping existing filters.");
                    break;
                case 2:
                    // Clear filters and view all
                    this.currentProjectFilters.clear();
                    displayMessage("Filters cleared.");
                    // Proceed with empty filters map
                    break;
                case 3:
                    // Change/Set new filters
                    displayMessage("Clearing old filters to set new ones.");
                    this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true, false); // Get new filters
                    break;
                case 0:
                default: // Includes Back or invalid choice
                    displayMessage("Returning to main menu.");
                    return; // Exit the handleView method
            }
        } else {
            // No filters were active, ask if they want to apply some now
            if (promptForConfirmation("Apply filters before viewing?:")) {
                this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true, false);
            } else {
                this.currentProjectFilters.clear(); // Ensure empty if they say no
            }
        }

        // --- Call Controller to Get MANAGED Projects with Filters ---
        List<Project> projectsToDisplay;
        try {
            // Call the controller method specifically for projects managed by this user
            projectsToDisplay = projectController.getProjectsManagedBy(this.user, currentProjectFilters);
        } catch (Exception e) { // Catch potential RuntimeExceptions from the controller/service
            displayError("Error retrieving managed projects: " + e.getMessage());
            // Consider logging e
            // Set to empty list or return, depending on desired flow. Returning is safer.
            return; // Cannot proceed if projects couldn't be retrieved
        }

        // --- Display Results ---
        if (projectsToDisplay.isEmpty()) {
            if (currentProjectFilters != null && !currentProjectFilters.isEmpty()) { // Check if filters were the reason
                displayMessage("No managed projects match the specified filters.");
            } else {
                // If no filters applied and list is empty, they manage no projects
                displayMessage("You are not currently managing any BTO projects.");
            }
        } else {
            // Use the ProjectUIHelper to display the list and handle selection
            String listTitle = (currentProjectFilters != null && !currentProjectFilters.isEmpty())
                    ? "Filtered Managed Projects (" + projectsToDisplay.size() + " found)"
                    : "My Managed Projects (" + projectsToDisplay.size() + " found)";
            Project selected = this.projectUIHelper.selectProjectFromList(projectsToDisplay, listTitle);

            // If the user selected a project (didn't choose 'Back')
            if (selected != null) {
                try {
                    // Get the PENDING registration count SPECIFICALLY for the selected project.
                    // Pass the authenticated manager (this.user) and the project ID.
                    int projectSpecificPendingCount = officerRegController.getPendingRegistrationCountForProject(
                            this.user, selected.getProjectId());

                    // Pass the project and the CORRECT pending count to the display method
                    this.projectUIHelper.displayStaffProjectDetails(selected, projectSpecificPendingCount);

                } catch (AuthorizationException ae) {
                    // Catch auth error from getPendingRegistrationCountForProject
                    // (Should be unlikely here since getManagedProjects already filtered, but good
                    // defense)
                    displayError("Authorization Error fetching project details: " + ae.getMessage());
                } catch (IllegalArgumentException iae) {
                    // Catch programmer errors like null manager/projectId passed to controller
                    displayError("Internal Error: Invalid arguments fetching project details. " + iae.getMessage());
                    // logger.log(Level.WARNING, "Illegal arguments passed", iae);
                } catch (RuntimeException re) {
                    // Catch other errors from getPendingRegistrationCountForProject
                    // (e.g., project somehow not found by ID, service/repo errors)
                    displayError("Error fetching project details: " + re.getMessage());
                    // logger.log(Level.SEVERE, "Runtime error fetching project details", re);
                }
                // The main application loop usually handles pausing.
            }
            // If selected == null, user chose 'Back', return naturally.
        }
    }

    /**
     * Handles the workflow for reviewing pending officer registrations.
     * <p>
     * This method allows managers to:
     * - View a list of pending officer registrations across all their projects
     * - Select a specific registration to review
     * - Approve or reject the selected registration
     * </p>
     * <p>
     * The approval or rejection is delegated to the OfficerRegistrationController,
     * and the outcome is reported to the user.
     * </p>
     *
     * @throws RegistrationException If an error occurs while accessing or
     *                               processing registration data
     */
    private void handleReviewOfficerRegistrations() throws RegistrationException {
        displayHeader("Review Pending Officer Registrations");
        List<OfficerRegistration> pendingRegs = officerRegController.getPendingRegistrations(user);

        if (pendingRegs.isEmpty()) {
            displayMessage("No pending officer registrations found.");
            return;
        }

        // Delegate to helper
        Map<Integer, OfficerRegistration> regMap = this.officerRegUIHelper.displayOfficerRegList(pendingRegs,
                "Pending Officer Registrations");
        if (regMap.isEmpty())
            return;

        int choice = promptForInt("Select registration number to review (or 0 to go back): ");
        if (choice == 0 || !regMap.containsKey(choice)) {
            if (choice != 0)
                displayError("Invalid selection.");
            return;
        }

        OfficerRegistration selectedReg = regMap.get(choice);
        boolean approve = promptForConfirmation("Approve registration for Officer " + selectedReg.getOfficerNric()
                + " for project " + selectedReg.getProjectId() + "?: ");

        boolean success = officerRegController.reviewRegistration(user, selectedReg.getRegistrationId(), approve);
        if (success) {
            displayMessage("Registration review processed successfully.");
        }
    }

    /**
     * Handles the workflow for reviewing pending BTO applications.
     * <p>
     * This method allows managers to:
     * - View a list of pending applications for projects they manage
     * - Select a specific application to review
     * - Approve or reject the selected application
     * </p>
     * <p>
     * The method filters applications to only show those for projects managed
     * by the current manager. The approval or rejection decision is delegated to
     * the ApplicationController.
     * </p>
     *
     * @throws ApplicationException If an error occurs while accessing or processing
     *                              application data
     */
    private void handleReviewApplications() throws ApplicationException {
        displayHeader("Review Pending BTO Applications");
        List<Application> pendingApps = applicationController.getApplicationsByStatus(user, ApplicationStatus.PENDING);

        if (pendingApps.isEmpty()) {
            displayMessage("No pending applications found globally.");
            return;
        }
        // Filter only applications for projects managed by this manager
        List<Project> myProjects = projectController.getProjectsManagedBy(this.user);
        Set<String> myProjectIds = myProjects.stream().map(Project::getProjectId).collect(Collectors.toSet());
        List<Application> relevantApps = pendingApps.stream()
                .filter(app -> myProjectIds.contains(app.getProjectId()))
                .collect(Collectors.toList());

        if (relevantApps.isEmpty()) {
            displayMessage("No pending applications found for the projects you manage.");
            return;
        }

        Map<Integer, Application> appMap = this.applicationUIHelper.displayApplicationList(relevantApps,
                "Pending Applications for Your Projects");
        if (appMap.isEmpty())
            return;

        int choice = promptForInt("Select application number to review (or 0 to go back): ");
        if (choice == 0 || !appMap.containsKey(choice)) {
            if (choice != 0)
                displayError("Invalid selection.");
            return;
        }

        Application selectedApp = appMap.get(choice);
        boolean approve = promptForConfirmation("Approve application " + selectedApp.getApplicationId()
                + " for Applicant " + selectedApp.getApplicantNric() + "?: ");

        boolean success = applicationController.reviewApplication(user, selectedApp.getApplicationId(), approve);
        if (success) {
            displayMessage("Application review processed successfully.");
        }
    }

    /**
     * Handles the workflow for reviewing pending application withdrawal requests.
     * <p>
     * This method allows managers to:
     * - View a list of applications with withdrawal requests for their projects
     * - Select a specific withdrawal request to review
     * - Approve or reject the selected withdrawal request
     * </p>
     * <p>
     * The method filters to show only withdrawal requests for projects managed
     * by the current manager. The decision is delegated to the
     * ApplicationController.
     * </p>
     *
     * @throws ApplicationException If an error occurs while accessing or processing
     *                              application data
     */
    private void handleReviewWithdrawals() throws ApplicationException {
        displayHeader("Review Pending Application Withdrawals");
        // Fetch apps that *could* have withdrawals (PENDING, SUCCESSFUL, or BOOKED)
        List<Application> allPotentialApps = new ArrayList<>(
                applicationController.getApplicationsByStatus(user, ApplicationStatus.PENDING));
        allPotentialApps.addAll(applicationController.getApplicationsByStatus(user, ApplicationStatus.SUCCESSFUL));
        allPotentialApps.addAll(applicationController.getApplicationsByStatus(user, ApplicationStatus.BOOKED));

        // Filter for those with withdrawal requests AND managed by this manager
        List<Project> myProjects = projectController.getProjectsManagedBy(this.user);
        Set<String> myProjectIds = myProjects.stream().map(Project::getProjectId).collect(Collectors.toSet());

        List<Application> pendingWithdrawals = allPotentialApps.stream()
                .filter(app -> app.getRequestedWithdrawalDate() != null && myProjectIds.contains(app.getProjectId()))
                .collect(Collectors.toList());

        if (pendingWithdrawals.isEmpty()) {
            displayMessage("No pending withdrawal requests found for the projects you manage.");
            return;
        }

        Map<Integer, Application> appMap = this.applicationUIHelper.displayApplicationList(pendingWithdrawals,
                "Pending Withdrawal Requests");
        if (appMap.isEmpty())
            return;

        int choice = promptForInt("Select application number to review withdrawal (or 0 to go back): ");
        if (choice == 0 || !appMap.containsKey(choice)) {
            if (choice != 0)
                displayError("Invalid selection.");
            return;
        }

        Application selectedApp = appMap.get(choice);
        boolean approve = promptForConfirmation(
                "Approve withdrawal for application " + selectedApp.getApplicationId() + "?: ");

        boolean success = applicationController.reviewWithdrawal(user, selectedApp.getApplicationId(), approve);
        if (success) {
            displayMessage("Withdrawal review processed successfully.");
        }
    }

    /**
     * Handles the workflow for viewing and replying to enquiries.
     * <p>
     * This method allows managers to:
     * - View a list of all enquiries in the system
     * - Select a specific enquiry to view and potentially reply to
     * - Submit a reply for unreplied enquiries
     * </p>
     * <p>
     * Managers can reply to general enquiries and enquiries for projects they
     * manage.
     * The reply process is delegated to the EnquiryController.
     * </p>
     *
     * @throws InvalidInputException If invalid input is provided while handling
     *                               enquiries
     */
    private void handleViewReplyEnquiries() throws InvalidInputException {
        displayHeader("View/Reply Enquiries");
        List<Enquiry> allEnquiries = enquiryController.viewAllEnquiries(); // Manager sees all

        if (allEnquiries.isEmpty()) {
            displayMessage("No enquiries found in the system.");
            return;
        }

        // Delegate to helper
        Map<Integer, Enquiry> enquiryMap = this.enquiryUIHelper.displayEnquiryList(allEnquiries,
                "All Enquiries (Sorted by Unreplied First)");
        if (enquiryMap.isEmpty())
            return;

        int choice = promptForInt("Select enquiry number to reply (or 0 to go back): ");
        if (choice == 0 || !enquiryMap.containsKey(choice)) {
            if (choice != 0)
                displayError("Invalid selection.");
            return;
        }

        Enquiry selectedEnq = enquiryMap.get(choice);

        if (selectedEnq.isReplied()) {
            displayMessage("This enquiry has already been replied to.");
            return;
        }

        // Preliminary permission check (Service layer does final check)
        boolean canReply = false;
        if (selectedEnq.getProjectId() == null) {
            canReply = true; // General enquiry
        } else {
            Project proj = projectController.findProjectById(selectedEnq.getProjectId());
            if (proj != null && proj.getManagerNric().equals(user.getNric())) {
                canReply = true; // Manager in charge
            }
        }
        if (!canReply) {
            displayError("You may not have permission to reply to this specific enquiry.");
            return;
        }

        String reply = promptForInput("Enter your reply: ");
        boolean success = enquiryController.replyToEnquiry(this.user, selectedEnq.getEnquiryId(), reply);
        if (success) {
            displayMessage("Reply submitted successfully.");
        }
    }

    /**
     * Handles the workflow for generating booking reports.
     * <p>
     * This method allows managers to:
     * - Specify filters for the report (flat type, project name, age, marital
     * status)
     * - Generate a formatted booking report based on the selected filters
     * </p>
     * <p>
     * The report generation is delegated to the ReportController, and the
     * formatted report is displayed to the user.
     * </p>
     */
    private void handleGenerateReport() {
        displayHeader("Generate Booking Report");
        Map<String, String> filters = new HashMap<>();

        displayMessage("Enter filter criteria (leave blank to ignore):");
        // --- Flat Type Filter ---
        List<FlatType> availableFlatTypes = Arrays.asList(FlatType.values()); // Or create dynamically if needed
        FlatType selectedFlatType = promptForEnum(
                "Filter by Flat Type (Choose number or 0 to cancel/skip):",
                FlatType.class,
                availableFlatTypes);

        if (selectedFlatType != null) { // Only add filter if user didn't cancel/skip
            filters.put("FLAT_TYPE", selectedFlatType.name());
        } else {
            displayMessage("Flat type filter skipped.");
        }
        // --- Project Name Filter ---
        String projectNameFilter = promptForInput("Filter by Project Name (exact match): ");
        if (!projectNameFilter.trim().isEmpty())
            filters.put("PROJECT_NAME", projectNameFilter);

        // --- Age Filter ---
        String ageFilter = promptForInput("Filter by Applicant Age (exact match): ");
        if (!ageFilter.trim().isEmpty()) {
            try {
                Integer.parseInt(ageFilter);
                filters.put("AGE", ageFilter);
            } catch (NumberFormatException e) {
                displayError("Invalid age '" + ageFilter + "'. Ignoring filter.");
            }
        }

        // --- Marital Status Filter ---
        MaritalStatus selectedMaritalStatus = promptForEnum(
                "Filter by Marital Status (Choose number or 0 to cancel/skip):",
                MaritalStatus.class,
                Arrays.asList(MaritalStatus.values()));

        if (selectedMaritalStatus != null) { // Only add filter if user didn't cancel/skip
            filters.put("MARITAL_STATUS", selectedMaritalStatus.name());
        } else {
            displayMessage("Marital status filter skipped.");
        }

        displayMessage("Generating report with filters: " + filters);
        String report = reportController.generateBookingReport(filters);
        displayMessage("\n--- Report Start ---");
        System.out.println(report);
        displayMessage("--- Report End ---");
        // pause();
    }

    /**
     * Handles the password change workflow for the HDB manager.
     * <p>
     * Delegates to the AccountUIHelper to manage the password change process.
     * </p>
     *
     * @return true if the password was successfully changed, false otherwise
     */
    private boolean handleChangePassword() {
        return this.accountUIHelper.handlePasswordChange(this.user);
    }

    /**
     * Prompts the user for a date input with the option to keep the current value.
     * <p>
     * If the user enters a blank value, the current date is retained.
     * Otherwise, the input is parsed as a date using the standard date formatter.
     * </p>
     *
     * @param prompt       The message to display to the user
     * @param currentValue The current date value to retain if input is blank
     * @return The new date value if provided, or the current value if input is
     *         blank
     */
    private LocalDate promptForDateOrKeep(String prompt, LocalDate currentValue) {
        while (true) {
            String input = promptForInput(
                    prompt + " (Enter YYYY-MM-DD or leave blank to keep '" + formatDateSafe(currentValue) + "'): ");
            if (input.trim().isEmpty()) {
                return currentValue; // Keep current
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                displayError("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    /**
     * Prompts the user for an integer input with the option to keep the current
     * value.
     * <p>
     * If the user enters a blank value, the current integer is retained.
     * Otherwise, the input is parsed as an integer.
     * </p>
     *
     * @param prompt       The message to display to the user
     * @param currentValue The current integer value to retain if input is blank
     * @return The new integer value if provided, or the current value if input is
     *         blank
     */
    private int promptForIntOrKeep(String prompt, int currentValue) {
        while (true) {
            String input = promptForInput(prompt + " (Enter number or leave blank to keep '" + currentValue + "'): ");
            if (input.trim().isEmpty()) {
                return currentValue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                displayError("Invalid number format.");
            }
        }
    }

    /**
     * Prompts the user for a string input with the option to keep the current
     * value.
     * <p>
     * If the user enters a blank value, the original string is retained.
     * Otherwise, the input is trimmed and returned.
     * </p>
     *
     * @param prompt        The message to display to the user
     * @param originalValue The original string value to retain if input is blank
     * @return The new string value if provided, or the original value if input is
     *         blank
     */
    private String promptForOptionalInput(String prompt, String originalValue) {
        // Assuming promptForInput exists in BaseUI and returns the raw user input
        // string
        String input = promptForInput(prompt); // Get input from BaseUI method
        if (input == null || input.trim().isEmpty()) {
            return originalValue; // Keep original if blank or only whitespace
        }
        return input.trim(); // Return trimmed user input
    }
}
