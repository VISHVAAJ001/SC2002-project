package com.ntu.fdae.group1.bto.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public class HDBManagerUI extends BaseUI {
    private final HDBManager user;
    private final UserController userController;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final OfficerRegistrationController officerRegController;
    private final EnquiryController enquiryController;
    private final ReportController reportController;
    private final AuthenticationController authController;
    private final ProjectUIHelper projectUIHelper;
    private final AccountUIHelper accountUIHelper;
    private final ApplicationUIHelper applicationUIHelper;
    private final EnquiryUIHelper enquiryUIHelper;
    private final OfficerRegUIHelper officerRegUIHelper;
    private Map<String, Object> currentProjectFilters;

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
        this.projectUIHelper = new ProjectUIHelper(this, userCtrl, projCtrl); // Initialize helper; pass in BaseUI and
                                                                              // UserController
        this.accountUIHelper = new AccountUIHelper(this, authCtrl); // Initialize account helper; pass in BaseUI and
                                                                    // AuthController
        this.applicationUIHelper = new ApplicationUIHelper(this, appCtrl, projCtrl); // Initialize application helper;
                                                                                     // pass in BaseUI and
                                                                                     // appController, ProjController
        this.enquiryUIHelper = new EnquiryUIHelper(this, userCtrl, projCtrl); // Initialize enquiry helper; pass in
                                                                              // BaseUI and ProjController
        this.officerRegUIHelper = new OfficerRegUIHelper(this, projCtrl); // Initialize officer registration helper;
                                                                          // pass in BaseUI and ProjController
        this.currentProjectFilters = new HashMap<>();
    }

    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            displayHeader("HDB Manager Menu - Welcome " + (user != null ? user.getName() : "User"));

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
                    this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true); // Get new filters
                    break;
                case 0:
                default: // Includes Back or invalid choice
                    displayMessage("Returning to main menu.");
                    return; // Exit the handleView method
            }
        } else {
            // No filters were active, ask if they want to apply some now
            if (promptForConfirmation("Apply filters before viewing?:")) {
                this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true);
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
                    this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true); // Get new filters
                    break;
                case 0:
                default: // Includes Back or invalid choice
                    displayMessage("Returning to main menu.");
                    return; // Exit the handleView method
            }
        } else {
            // No filters were active, ask if they want to apply some now
            if (promptForConfirmation("Apply filters before viewing?:")) {
                this.currentProjectFilters = projectUIHelper.promptForProjectFilters(true);
            } else {
                this.currentProjectFilters.clear(); // Ensure empty if they say no
            }
        }

        // --- Call Controller to Get MANAGED Projects with Filters ---
        List<Project> projectsToDisplay;
        try {
            // Call the controller method specifically for projects managed by this user
            projectsToDisplay = projectController.getManagedProjects(this.user, currentProjectFilters);
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

    private void handleReviewWithdrawals() throws ApplicationException {
        displayHeader("Review Pending Application Withdrawals");
        // Fetch apps that *could* have withdrawals (PENDING or SUCCESSFUL)
        List<Application> allPotentialApps = new ArrayList<>(
                applicationController.getApplicationsByStatus(user, ApplicationStatus.PENDING));
        allPotentialApps.addAll(applicationController.getApplicationsByStatus(user, ApplicationStatus.SUCCESSFUL));

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

    private boolean handleChangePassword() {
        return this.accountUIHelper.handlePasswordChange(this.user);
    }

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
