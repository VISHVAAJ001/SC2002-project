package com.ntu.fdae.group1.bto.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private final ProjectUIHelper projectUIHelper;
    private final AccountUIHelper accountUIHelper;
    private final ApplicationUIHelper applicationUIHelper;
    private final EnquiryUIHelper enquiryUIHelper;
    private final OfficerRegUIHelper officerRegUIHelper;

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
        this.projectUIHelper = new ProjectUIHelper(this, userCtrl); // Initialize helper; pass in BaseUI and UserController
        this.accountUIHelper = new AccountUIHelper(this, authCtrl); // Initialize account helper; pass in BaseUI and AuthController
        this.applicationUIHelper = new ApplicationUIHelper(this, appCtrl, projCtrl); // Initialize application helper; pass in BaseUI and appController, ProjController
        this.enquiryUIHelper = new EnquiryUIHelper(this, userCtrl, projCtrl); // Initialize enquiry helper; pass in BaseUI and ProjController
        this.officerRegUIHelper = new OfficerRegUIHelper(this, projCtrl); // Initialize officer registration helper; pass in BaseUI and ProjController
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
                        handleChangePassword();
                        break;
                    case 0:
                        displayMessage("Logging out...");
                        keepRunning = false;
                        break;
                    default:
                        displayError("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                displayError("An error occurred: " + e.getMessage());
                // e.printStackTrace(); // For debugging
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

    private void handleCreateProject() throws RegistrationException, InvalidInputException { // Consider if these
                                                                                             // exceptions are really
                                                                                             // thrown by
                                                                                             // controller/service
        displayHeader("Create New BTO Project");
        String name = promptForInput("Enter Project Name: ");
        String neighborhood = promptForInput("Enter Neighborhood: ");
        LocalDate openDate = promptForDate("Enter Application Opening Date: ");
        LocalDate closeDate = promptForDate("Enter Application Closing Date: ");
        int officerSlots = promptForInt("Enter Max HDB Officer Slots (1-10): ");

        Map<String, ProjectFlatInfo> flatInfoMap = new HashMap<>();
        System.out.println("--- Enter Flat Details ---");
        for (FlatType type : Arrays.asList(FlatType.TWO_ROOM, FlatType.THREE_ROOM)) {
            int totalUnits = promptForInt("Enter Total Units for " + type.name() + ": ");
            // Assuming price is not needed at creation or defaults to 0
            ProjectFlatInfo info = new ProjectFlatInfo(type, totalUnits, totalUnits, 0.0);
            flatInfoMap.put(type.name(), info); // Use enum name as String key
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
        List<Project> myProjects = projectController.getProjectsManagedBy(user);

        // Delegate to ProjectUIHelper
        Project projectToEdit = this.projectUIHelper.selectProjectFromList(myProjects, "Select Project to Edit");
        if (projectToEdit == null) return;

        this.projectUIHelper.displayStaffProjectDetails(projectToEdit);

        displayMessage("Enter new details (leave blank or enter ' ' to keep current):");
        String name = promptForInput("New Project Name [" + projectToEdit.getProjectName() + "]: ");
        String neighborhood = promptForInput("New Neighborhood [" + projectToEdit.getNeighborhood() + "]: ");

        LocalDate openDate = promptForDateOrKeep(
                "New Opening Date (YYYY-MM-DD) [" + projectToEdit.getOpeningDate() + "]:",
                projectToEdit.getOpeningDate());
        LocalDate closeDate = promptForDateOrKeep(
                "New Closing Date (YYYY-MM-DD) [" + projectToEdit.getClosingDate() + "]:",
                projectToEdit.getClosingDate());

        int officerSlots = promptForIntOrKeep(
                "New Max Officer Slots [" + projectToEdit.getMaxOfficerSlots() + "] (1-10):",
                projectToEdit.getMaxOfficerSlots());

        boolean success = projectController.editProject(user, projectToEdit.getProjectId(),
                name.trim().isEmpty() ? projectToEdit.getProjectName() : name.trim(),
                neighborhood.trim().isEmpty() ? projectToEdit.getNeighborhood() : neighborhood.trim(),
                openDate, // Use the potentially kept date
                closeDate, // Use the potentially kept date
                officerSlots); // Use the potentially kept slots

        if (success) {
            displayMessage("Project updated successfully.");
        } else {
            displayError("Project update failed.");
        }
    }

    private void handleDeleteProject() {
        displayHeader("Delete Project");
        List<Project> myProjects = projectController.getProjectsManagedBy(this.user);
       
        // Delegate to Helper
        Project projectToDelete = this.projectUIHelper.selectProjectFromList(myProjects, "Select Project to Delete");
        if (projectToDelete == null) return;

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
        Project projectToToggle = this.projectUIHelper.selectProjectFromList(myProjects, "Select Project to Toggle Visibility");
            if (projectToToggle == null) return;

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

    private void handleViewAllProjects() throws AuthorizationException{
        displayHeader("All BTO Projects - View & Filter");

        // --- Prepare Filters ---
        Map<String, Object> filterMap = new HashMap<>(); 
        boolean applyFilters = promptForConfirmation("Apply filters?: ");

        if (applyFilters) {
            displayMessage("Enter filter criteria (leave blank to skip a filter):");

            // 1. Neighborhood Filter (String)
            String neighborhoodFilter = promptForInput("Filter by Neighborhood (contains, case-insensitive): ");
            if (!neighborhoodFilter.trim().isEmpty()) {
                filterMap.put("neighborhood", neighborhoodFilter); // Service uses 'contains'
            }

            // 2. Flat Type Filter (FlatType Enum)
            String flatTypeFilterInput = promptForInput("Filter by Flat Type Offered (TWO_ROOM, THREE_ROOM): ").toUpperCase();
            if (!flatTypeFilterInput.trim().isEmpty()) {
                try {
                    FlatType selectedType = FlatType.valueOf(flatTypeFilterInput);
                    filterMap.put("flatType", selectedType); // Put the Enum object in the map
                } catch (IllegalArgumentException e) {
                    displayError("Invalid flat type '" + flatTypeFilterInput + "'. Flat type filter skipped.");
                }
            }

            // 3. Visibility Filter (Boolean - Manager Only)
            String visibilityInput = promptForInput("Filter by Visibility (ON, OFF): ").toUpperCase();
            if (!visibilityInput.trim().isEmpty()) {
                if ("ON".equals(visibilityInput)) {
                    filterMap.put("visibility", Boolean.TRUE);
                } else if ("OFF".equals(visibilityInput)) {
                    filterMap.put("visibility", Boolean.FALSE);
                } else {
                    displayError("Invalid visibility input '" + visibilityInput + "'. Visibility filter skipped.");
                }
            }

            displayMessage("Applying filters: " + filterMap);
        }

        // --- Call Controller with Filters ---
        List<Project> projectsToDisplay;
        try {
            // Pass the current manager user and the constructed filter map
            projectsToDisplay = projectController.getAllProjects(this.user, filterMap);
        } catch (AuthorizationException ae) { 
             displayError("Authorization Error: " + ae.getMessage());
             return; // Cannot proceed
        } catch (Exception e) { // Catch other unexpected errors
             displayError("Error retrieving projects: " + e.getMessage());
             return; // Cannot proceed
        }

        // --- Display Results ---
        if (projectsToDisplay.isEmpty()) {
            if (applyFilters) { // Check if filters were actually applied
                displayMessage("No projects match the specified filters.");
            } else {
                displayMessage("No projects found in the system.");
            }
        } else {
            String listTitle = applyFilters ? "Filtered Projects (" + projectsToDisplay.size() + " found)" : "All Projects";
            // Use ProjectUIHelper to display list and select
            Project selected = this.projectUIHelper.selectProjectFromList(projectsToDisplay, listTitle);
            if (selected != null) {
                // Use ProjectUIHelper to display details
                this.projectUIHelper.displayStaffProjectDetails(selected);
            }
        }
    }

    private void handleViewMyProjects() {
        displayHeader("My Managed BTO Projects - View & Filter");
        
        // 1. Get ONLY the projects managed by this manager using existing controller method
        List<Project> myManagedProjects = projectController.getProjectsManagedBy(this.user);

        if (myManagedProjects.isEmpty()) {
            displayMessage("You are not managing any projects.");
            return;
        }

        List<Project> projectsToDisplay = myManagedProjects; // Start with the managed list

        // 2. Offer Optional Filtering on THIS SUBSET
        if (promptForConfirmation("Apply filters to your managed projects?: ")) {
            displayMessage("Enter filter criteria (leave blank to skip a filter):");
            // Build filter map locally (same as in handleViewAllProjects)
            Map<String, Object> filterMap = new HashMap<>();

            // --- Get Neighborhood Filter ---
            String neighborhoodFilter = promptForInput("Filter by Neighborhood (contains, case-insensitive): ");
            if (!neighborhoodFilter.trim().isEmpty()) {
                filterMap.put("neighborhood", neighborhoodFilter);
            }

            // --- Get Flat Type Filter ---
            String flatTypeFilterInput = promptForInput("Filter by Flat Type Offered (TWO_ROOM, THREE_ROOM): ").toUpperCase();
            if (!flatTypeFilterInput.trim().isEmpty()) {
                try {
                    FlatType selectedType = FlatType.valueOf(flatTypeFilterInput);
                    filterMap.put("flatType", selectedType);
                } catch (IllegalArgumentException e) {
                    displayError("Invalid flat type '" + flatTypeFilterInput + "'. Filter skipped.");
                }
            }

             // --- Get Visibility Filter ---
            String visibilityInput = promptForInput("Filter by Visibility (ON, OFF): ").toUpperCase();
            if (!visibilityInput.trim().isEmpty()) {
                if ("ON".equals(visibilityInput)) { filterMap.put("visibility", Boolean.TRUE); }
                else if ("OFF".equals(visibilityInput)) { filterMap.put("visibility", Boolean.FALSE); }
                else { displayError("Invalid visibility input. Filter skipped."); }
            }

            // --- 3. Apply Filters Locally using Stream API ---
            // Start with the stream of already managed projects
            Stream<Project> filteredStream = myManagedProjects.stream();

            // Apply filters based on the collected map (similar logic to service's applyOptionalFilters)
            if (filterMap.containsKey("neighborhood")) {
                 String nf = (String) filterMap.get("neighborhood");
                 final String lowerNf = nf.toLowerCase(); // Optimize
                 filteredStream = filteredStream.filter(p -> p.getNeighborhood().toLowerCase().contains(lowerNf));
            }
            if (filterMap.containsKey("flatType")) {
                 FlatType ft = (FlatType) filterMap.get("flatType");
                 filteredStream = filteredStream.filter(p -> p.getFlatTypes() != null && p.getFlatTypes().containsKey(ft));
            }
             if (filterMap.containsKey("visibility")) {
                 Boolean vis = (Boolean) filterMap.get("visibility");
                 filteredStream = filteredStream.filter(p -> p.isVisible() == vis);
            }

            // Collect the results of local filtering
            projectsToDisplay = filteredStream.collect(Collectors.toList());

            displayMessage("Applying filters: " + filterMap + ". Found: " + projectsToDisplay.size());

        } // End if applyFilters

        // 4. Display the results (either all managed or filtered managed)
        if (projectsToDisplay.isEmpty()) {
            if (projectsToDisplay != myManagedProjects) { // Check if filtering happened
                displayMessage("No managed projects match the specified filters.");
            } else {
                // Should be caught by initial check, but safeguard
                displayMessage("You are not managing any projects.");
            }
        } else {
            String listTitle = (projectsToDisplay == myManagedProjects) ? "My Managed Projects" : "Filtered Managed Projects (" + projectsToDisplay.size() + " found)";
            // Use ProjectUIHelper to display list and select
            Project selected = this.projectUIHelper.selectProjectFromList(projectsToDisplay, listTitle);
            if (selected != null) {
                // Use ProjectUIHelper to display details
                this.projectUIHelper.displayStaffProjectDetails(selected);
            }
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
        Map<Integer, OfficerRegistration> regMap = this.officerRegUIHelper.displayOfficerRegList(pendingRegs, "Pending Officer Registrations");
         if (regMap.isEmpty()) return;

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

        Map<Integer, Application> appMap = this.applicationUIHelper.displayApplicationList(relevantApps, "Pending Applications for Your Projects");
         if (appMap.isEmpty()) return;

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

        Map<Integer, Application> appMap = this.applicationUIHelper.displayApplicationList(pendingWithdrawals, "Pending Withdrawal Requests");
        if(appMap.isEmpty()) return;

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
        Map<Integer, Enquiry> enquiryMap = this.enquiryUIHelper.displayEnquiryList(allEnquiries, "All Enquiries (Sorted by Unreplied First)");
         if(enquiryMap.isEmpty()) return;

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
        String flatTypeInput = promptForInput("Filter by Flat Type (TWO_ROOM, THREE_ROOM): ").toUpperCase();
        if (!flatTypeInput.trim().isEmpty()) {
            try {
                filters.put("FLAT_TYPE", FlatType.valueOf(flatTypeInput).name());
            } catch (IllegalArgumentException e) {
                displayError("Invalid flat type '" + flatTypeInput + "'. Ignoring filter.");
            }
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
        String maritalStatusInput = promptForInput("Filter by Marital Status (SINGLE, MARRIED): ").toUpperCase();
        if (!maritalStatusInput.trim().isEmpty()) {
            try {
                filters.put("MARITAL_STATUS", MaritalStatus.valueOf(maritalStatusInput).name());
            } catch (IllegalArgumentException e) {
                displayError("Invalid marital status '" + maritalStatusInput + "'. Ignoring filter.");
            }
        }

        displayMessage("Generating report with filters: " + filters);
        String report = reportController.generateBookingReport(filters);
        displayMessage("\n--- Report Start ---");
        System.out.println(report);
        displayMessage("--- Report End ---");
        // pause();
    }

    private void handleChangePassword() {
        this.accountUIHelper.handlePasswordChange(this.user);
    }


    private LocalDate promptForDateOrKeep(String prompt, LocalDate currentValue) {
        while (true) {
            String input = promptForInput(
                    prompt + " (Enter YYYY-MM-DD or leave blank to keep '" + formatDate(currentValue) + "'): ");
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

    private String formatDate(LocalDate date) {
        return (date == null) ? "N/A" : DATE_FORMATTER.format(date);
    }

}
