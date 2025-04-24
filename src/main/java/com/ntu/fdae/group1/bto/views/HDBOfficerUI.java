package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.controllers.booking.BookingController;
import com.ntu.fdae.group1.bto.controllers.booking.ReceiptController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController; // Added
import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.project.*; // Import project models
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.exceptions.*; // Import custom exceptions
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * User interface for HDB Officer users in the BTO Management System.
 * <p>
 * This class provides a console-based interface for HDB officers to interact
 * with
 * the system. It allows officers to perform both applicant-like operations and
 * officer-specific duties, including:
 * - Browsing and applying for BTO projects (as an applicant)
 * - Managing their own applications and enquiries
 * - Requesting registration to handle specific projects
 * - Managing projects they are approved to handle
 * - Processing bookings and generating receipts
 * - Responding to project-related enquiries
 * </p>
 * <p>
 * The UI follows a menu-driven approach, with primary and sub-menu options that
 * delegate to specific handler methods. It leverages various UI helper classes
 * to manage complex operations while maintaining separation of concerns.
 * </p>
 */
public class HDBOfficerUI extends BaseUI {
    /**
     * The authenticated HDB officer user currently using the interface.
     */
    private final HDBOfficer user;

    /**
     * Controller for user-related operations.
     */
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
     * Controller for booking-related operations.
     */
    private final BookingController bookingController;

    /**
     * Controller for receipt generation operations.
     */
    private final ReceiptController receiptController;

    /**
     * Controller for enquiry-related operations.
     */
    private final EnquiryController enquiryController;

    /**
     * Controller for authentication-related operations.
     */
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
     * Helper for enquiry-related UI operations.
     */
    private final EnquiryUIHelper enquiryUIHelper;

    /**
     * Helper for application-related UI operations.
     */
    private final ApplicationUIHelper applicationUIHelper;

    /**
     * Helper for officer registration UI operations.
     */
    private final OfficerRegUIHelper officerRegUIHelper;

    /**
     * Helper for booking-related UI operations.
     */
    private final BookingUIHelper bookingUIHelper;

    /**
     * Current filters applied to project listings.
     */
    private Map<String, Object> currentProjectFilters;

    /**
     * Constructs a new HDBOfficerUI with the specified dependencies.
     *
     * @param user        The authenticated HDB officer user
     * @param userCtrl    Controller for user operations
     * @param projCtrl    Controller for project operations
     * @param appCtrl     Controller for application operations
     * @param offRegCtrl  Controller for officer registration operations
     * @param bookCtrl    Controller for booking operations
     * @param receiptCtrl Controller for receipt operations
     * @param enqCtrl     Controller for enquiry operations
     * @param authCtrl    Controller for authentication operations
     * @param scanner     Scanner for reading user input
     * @throws NullPointerException if any parameter is null
     */
    public HDBOfficerUI(HDBOfficer user,
            UserController userCtrl,
            ProjectController projCtrl,
            ApplicationController appCtrl,
            OfficerRegistrationController offRegCtrl,
            BookingController bookCtrl,
            ReceiptController receiptCtrl,
            EnquiryController enqCtrl,
            AuthenticationController authCtrl,
            Scanner scanner) {
        super(scanner);
        this.user = Objects.requireNonNull(user);
        this.userController = Objects.requireNonNull(userCtrl);
        this.projectController = Objects.requireNonNull(projCtrl);
        this.applicationController = Objects.requireNonNull(appCtrl);
        this.officerRegController = Objects.requireNonNull(offRegCtrl);
        this.bookingController = Objects.requireNonNull(bookCtrl);
        this.receiptController = Objects.requireNonNull(receiptCtrl);
        this.enquiryController = Objects.requireNonNull(enqCtrl);
        this.authController = Objects.requireNonNull(authCtrl);
        this.projectUIHelper = new ProjectUIHelper(this, userController, projectController);
        this.accountUIHelper = new AccountUIHelper(this, authController);
        this.enquiryUIHelper = new EnquiryUIHelper(this, userController, projectController);
        this.applicationUIHelper = new ApplicationUIHelper(this, applicationController, projectController,
                userController);
        this.officerRegUIHelper = new OfficerRegUIHelper(this, projectController, userController);
        this.bookingUIHelper = new BookingUIHelper(this, userController);
        this.currentProjectFilters = new HashMap<>();
    }

    /**
     * Displays the main menu for HDB officer users and handles their selections.
     * <p>
     * This method shows a menu of options available to HDB officers, including
     * applicant-like operations, officer-specific duties, and account management.
     * It runs in a loop until the user chooses to log out, delegating to specific
     * handler methods based on user input.
     * </p>
     */
    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            if (user != null) {
                displayHeader("HDB Officer Menu - Welcome " + user.getName() + " (" + user.getAge() + ", "
                        + user.getMaritalStatus() + ")");
            } else {
                displayHeader("Applicant Menu - Welcome User (Age, Marital Status)");
            }

            System.out.println("--- View/Apply (Applicant Role) ---");
            System.out.println("[1] View & Apply for Available Projects"); // Combined
            System.out.println("[2] View My Application Status & Request Withdrawal"); // Combined
            System.out.println("-------------------------------------");
            System.out.println("--- Officer Project Role ---");
            System.out.println("[3] Register for Project Handling");
            System.out.println("[4] View My Registration Status");
            System.out.println("[5] Manage Project Being Handled"); // Combined view/book/receipt/enquiry actions
            System.out.println("-------------------------------------");
            System.out.println("--- Enquiries (Personal) ---");
            System.out.println("[6] Submit Enquiry (My Own)");
            System.out.println("[7] Manage My Enquiries (View/Edit/Delete)"); // Combined
            System.out.println("-------------------------------------");
            System.out.println("--- Account ---");
            System.out.println("[8] Change Password");
            System.out.println("-------------------------------------");
            System.out.println("[0] Logout");
            System.out.println("=====================================");

            int choice = promptForInt("Enter your choice: ");

            try {
                switch (choice) {
                    // Applicant Role Actions (Delegate to similar methods as ApplicantUI)
                    case 1:
                        handleViewAndApplyProjects();
                        break; // Uses Applicant View/Apply logic
                    case 2:
                        handleViewAndWithdrawApplication();
                        break; // Uses Applicant View/Withdraw logic

                    // Officer Role Actions
                    case 3:
                        handleRequestRegistration();
                        break;
                    case 4:
                        handleViewRegistrationStatus();
                        break;
                    case 5:
                        handleManageHandlingProject();
                        break;

                    // Personal Enquiry Actions (Delegate to similar methods as ApplicantUI)
                    case 6:
                        handleSubmitEnquiry();
                        break; // Officer submits own enquiry
                    case 7:
                        handleManageMyEnquiries();
                        break; // Officer manages own enquiries

                    // Account
                    case 8:
                        if (handleChangePassword())
                            keepRunning = false; // Could just remove the break here, but this is clearer
                        break;
                    case 0:
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

    // --- Handler Methods ---

    /**
     * Handles the workflow for viewing and applying for BTO projects.
     * Restrict certain users (based on application status) from applying to
     * projects.
     * 
     * <p>
     * This method allows officers (in their applicant capacity) to:
     * - Manage filters for the project list view
     * - Browse available projects
     * - View project details
     * - Apply for a selected project
     * </p>
     */
    private void handleViewAndApplyProjects() {
        // Upfront check for existing active application
        // If users have the statuses PENDING, SUCCESSFUL, or BOOKED, they are not able
        // to apply for any projects
        // If status is UNSUCCESSFUL or WITHDRAWN, the user *can* apply again
        try {
            Application currentApp = applicationController.getMyApplication(this.user);
            if (currentApp != null) {
                ApplicationStatus status = currentApp.getStatus();
                if (status == ApplicationStatus.PENDING ||
                        status == ApplicationStatus.SUCCESSFUL ||
                        status == ApplicationStatus.BOOKED) {
                    String reason = String.format(
                            "Sorry. You are not able to apply for any projects.\nReason: You already have an active application (ID: %s, Status: %s). You cannot submit a new one until it is concluded.",
                            currentApp.getApplicationId(), status);
                    displayError(reason); // Display the specific reason
                    return; // Exit the method immediately, user cannot proceed
                }
            }
        } catch (Exception e) {
            displayError("Error checking your current application status: " + e.getMessage());
            return;
        }

        displayHeader("View Available BTO Projects");

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
                    this.currentProjectFilters = projectUIHelper.promptForProjectFilters(false, false); // Get new filters
                    break;
                case 0:
                default: // Includes Back or invalid choice
                    displayMessage("Returning to main menu.");
                    return; // Exit the handleView method
            }
        } else {
            // No filters were active, ask if they want to apply some now
            if (promptForConfirmation("Apply filters before viewing?:")) {
                this.currentProjectFilters = projectUIHelper.promptForProjectFilters(false, false);
            } else {
                this.currentProjectFilters.clear(); // Ensure empty if they say no
            }
        }

        List<Project> projects = projectController.getVisibleProjectsForUser(this.user,
                this.currentProjectFilters);

        Project selectedProject = projectUIHelper.selectProjectFromList(projects,
                "Select Project to View Details & Apply");

        if (selectedProject != null) {
            // Display details using the specific applicant view helper method
            projectUIHelper.displayApplicantProjectDetails(selectedProject); // Use the tailored view

            // --- Contextual Action ---
            System.out.println("\nOptions:");
            System.out.println("[1] Apply for " + selectedProject.getProjectName());
            System.out.println("[0] Back");

            int actionChoice = promptForInt("Enter option: ");
            if (actionChoice == 1) {
                applicationUIHelper.performApplicationSubmission(this.user, selectedProject.getProjectId());
            }
        }
    }

    /**
     * Handles the workflow for viewing and withdrawing applications.
     * <p>
     * Delegates to the ApplicationUIHelper for displaying the officer's
     * personal applications and handling withdrawal requests.
     * </p>
     */
    private void handleViewAndWithdrawApplication() {
        applicationUIHelper.performViewAndWithdraw(this.user);
    }

    /**
     * Handles the workflow for submitting a new personal enquiry.
     * <p>
     * This method allows officers (in their applicant capacity) to:
     * - Select a project to enquire about
     * - Enter the content of their enquiry
     * - Submit the enquiry to the system
     * </p>
     */
    private void handleSubmitEnquiry() {
        displayHeader("Submit Enquiry");

        // Use a LinkedHashMap to store projects, preventing duplicates and maintaining
        // order
        Map<String, Project> projectsForEnquiryMap = new LinkedHashMap<>();

        // 1. Get projects normally visible for application (active period, eligible
        // etc.)
        try {
            List<Project> activeProjects = projectController.getVisibleProjectsForUser(this.user,
                    this.currentProjectFilters);
            if (activeProjects != null) {
                activeProjects.forEach(p -> projectsForEnquiryMap.put(p.getProjectId(), p));
            }
        } catch (Exception e) {
            displayError("Error retrieving available projects: " + e.getMessage());
            // Decide if you want to continue or return. Let's continue to check for booked
            // project.
        }

        // 2. Check if the user has an active application (PENDING, SUCCESSFUL, BOOKED)
        Project associatedProject = null;
        try {
            Application currentApp = applicationController.getMyApplication(this.user);
            if (currentApp != null) {
                ApplicationStatus status = currentApp.getStatus();
                if (status == ApplicationStatus.PENDING ||
                        status == ApplicationStatus.SUCCESSFUL ||
                        status == ApplicationStatus.BOOKED) {
                    // Fetch the project details for this application
                    associatedProject = projectController.findProjectById(currentApp.getProjectId());
                    if (associatedProject != null) {
                        // Add this project to the map (if not already present)
                        // putIfAbsent ensures we don't overwrite if it was already added from the
                        // active list
                        projectsForEnquiryMap.putIfAbsent(associatedProject.getProjectId(), associatedProject);
                    } else {
                        displayMessage("Note: Could not find details for project ID " + currentApp.getProjectId()
                                + " associated with your application.");
                    }
                }
            }
        } catch (Exception e) {
            displayError("Error checking your current application status: " + e.getMessage());
            // Continue, maybe they can still select from active projects
        }

        // 3. Convert the map values back to a list for the selection helper
        List<Project> projectsToShow = new ArrayList<>(projectsForEnquiryMap.values());

        if (projectsToShow.isEmpty()) {
            displayMessage("There are no projects available for you to submit an enquiry about at this time.");
            return;
        }

        // 4. Let the user select from the combined list
        Project selectedProject = projectUIHelper.selectProjectFromList(projectsToShow,
                "Select Project to Submit Enquiry About"); // Use the combined list

        if (selectedProject != null) {
            String content = promptForInput("Enter your enquiry content: ");
            if (content != null && !content.trim().isEmpty()) {
                try {
                    enquiryController.createEnquiry(this.user, selectedProject.getProjectId(), content);
                    displayMessage("Enquiry submitted successfully!");
                } catch (Exception e) {
                    displayError("Failed to submit enquiry: " + e.getMessage());
                }
            } else {
                displayError("Enquiry content cannot be empty.");
            }
        }
    }

    /**
     * Handles the workflow for managing personal enquiries.
     * <p>
     * This method allows officers to:
     * - View a list of their submitted enquiries
     * - View details of a specific enquiry
     * - Edit or delete an enquiry (if it hasn't been replied to)
     * </p>
     */
    private void handleManageMyEnquiries() {
        displayHeader("Manage My Enquiries");
        List<Enquiry> enquiries = enquiryController.viewMyEnquiries(this.user); // Get data

        // Use helper to display list and get selection
        Enquiry selectedEnquiry = enquiryUIHelper.selectEnquiryFromList(enquiries, "My Submitted Enquiries");

        if (selectedEnquiry != null) {
            // Use helper to display details
            enquiryUIHelper.displayEnquiryDetails(selectedEnquiry);

            // --- Applicant Contextual Actions ---
            // Check if editable/deletable based on rules (e.g., not replied - FAQ p26)
            boolean canManage = !selectedEnquiry.isReplied();

            if (canManage) {
                System.out.println("\nOptions:");
                System.out.println("[1] Edit Enquiry Content");
                System.out.println("[2] Delete Enquiry");
                System.out.println("[0] Back");

                int actionChoice = promptForInt("Enter option: ");
                switch (actionChoice) {
                    case 1:
                        handleEditEnquiryAction(selectedEnquiry.getEnquiryId());
                        break;
                    case 2:
                        handleDeleteEnquiryAction(selectedEnquiry.getEnquiryId());
                        break;
                    // Default or 0: Do nothing
                }
            } else {
                displayMessage("This enquiry has been replied to and cannot be modified.");
                displayMessage("\n[0] Back");
                promptForInt("Enter 0 to go back: ");
            }
        }
    }

    /**
     * Handles the action of editing an existing personal enquiry.
     * <p>
     * Prompts the user for new content and updates the enquiry if valid.
     * </p>
     *
     * @param enquiryId The ID of the enquiry to edit
     */
    private void handleEditEnquiryAction(String enquiryId) {
        String newContent = promptForInput("Enter new content for the enquiry: ");
        if (newContent != null && !newContent.trim().isEmpty()) {
            try {
                boolean success = enquiryController.editEnquiry(enquiryId, newContent, this.user);
                if (success) {
                    displayMessage("Enquiry edited successfully!");
                } else {
                    displayError("Failed to edit enquiry. It may not exist or you may not have permission.");
                }
            } catch (Exception e) {
                displayError("Failed to edit enquiry: " + e.getMessage());
            }
        } else {
            displayError("New content cannot be empty.");
        }
    }

    /**
     * Handles the action of deleting an existing personal enquiry.
     * <p>
     * Attempts to delete the enquiry and displays the result to the user.
     * </p>
     *
     * @param enquiryId The ID of the enquiry to delete
     */
    private void handleDeleteEnquiryAction(String enquiryId) {
        try {
            boolean success = enquiryController.deleteEnquiry(enquiryId, this.user);

            if (success) {
                displayMessage("Enquiry deleted successfully!");
            } else {
                displayError("Failed to delete enquiry. It may not exist or you may not have permission.");
            }
        } catch (Exception e) {
            displayError("Failed to delete enquiry: " + e.getMessage());
            return;
        }
    }

    /**
     * Handles the process for an HDB Officer to select a project and request
     * registration to handle it.
     * <p>
     * This method guides the officer through:
     * - Viewing available projects that need officer handling
     * - Selecting a specific project
     * - Confirming their registration request
     * - Viewing the details of their submitted registration
     * </p>
     */
    private void handleRequestRegistration() {
        displayHeader("Register for Project Handling");
        try {
            List<Project> availableProjects = projectController.getProjectsAvailableForRegistration(this.user);
            if (availableProjects == null || availableProjects.isEmpty()) {
                return;
            }

            Project selectedProject = projectUIHelper.selectProjectFromList(availableProjects,
                    "Select Project to Register For...");
            if (selectedProject == null) {
                return;
            }

            String projectIdToRegister = selectedProject.getProjectId();
            displayMessage(
                    "You selected Project: " + selectedProject.getProjectName() + " (ID: " + projectIdToRegister + ")");

            if (!promptForConfirmation("Confirm registration request...?")) {
                return;
            }

            OfficerRegistration registration = officerRegController.requestRegistration(this.user, projectIdToRegister);
            displayMessage("Registration requested successfully!");

            this.officerRegUIHelper.displayOfficerRegistrationDetails(registration);

        } catch (RegistrationException e) {
        } catch (Exception e) {
        }
    }

    /**
     * Handles viewing the status of all project handling registrations submitted by
     * the current HDB Officer.
     * <p>
     * Retrieves and displays a list of all registration requests the officer has
     * submitted, showing their current status and related information.
     * </p>
     */
    private void handleViewRegistrationStatus() {
        displayHeader("View My Project Registration Status");
        try {
            List<OfficerRegistration> myRegistrations = officerRegController.getMyRegistrations(this.user);
            if (myRegistrations == null || myRegistrations.isEmpty()) {
                displayMessage("You have no submitted registration requests.");
                return;
            }

            this.officerRegUIHelper.displayOfficerRegListForViewing(myRegistrations, "Your Registration Requests");

        } catch (Exception e) {
            displayError("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Handles the sub-menu for managing the specific project the officer is
     * approved to handle.
     * <p>
     * This method:
     * - Finds the project the officer is handling
     * - Displays comprehensive project details
     * - Offers a sub-menu of management actions including booking, receipts, and
     * enquiries
     * - Delegates to specific handler methods based on user selection
     * </p>
     */
    private void handleManageHandlingProject() {
        displayHeader("Manage Approved Project(s)");

        // --- Step 1: Find ALL approved projects ---
        List<Project> allApprovedProjects;
        try {
            allApprovedProjects = officerRegController.findApprovedHandlingProject(this.user);
        } catch (Exception e) {
            displayError("Error retrieving the list of projects you are approved for: " + e.getMessage());
            return;
        }

        // --- Step 2: Check if any approved projects exist at all ---
        if (allApprovedProjects.isEmpty()) {
            displayMessage("You are not currently approved to handle any specific project.");
            displayMessage("Please ensure your registration request has been approved by a manager.");
            return;
        }

        // --- Step 3: Display ALL approved projects with their status ---
        displayMessage("Projects you are approved for:");
        LocalDate currentDate = LocalDate.now();
        List<Project> activeManageableProjects = new ArrayList<>();

        for (Project project : allApprovedProjects) {
            if (project == null)
                continue;

            String statusTag = "";

            // Determine status and populate the active list
            if (project.getOpeningDate() == null || project.getClosingDate() == null) {
                statusTag = " (Status Unavailable - Missing Dates)";
            } else if (!currentDate.isBefore(project.getOpeningDate())
                    && !currentDate.isAfter(project.getClosingDate())) {
                statusTag = " (Active)";
                activeManageableProjects.add(project);
            } else if (currentDate.isBefore(project.getOpeningDate())) {
                statusTag = String.format(" (Upcoming - Opens %s)", project.getOpeningDate());
            } else {
                statusTag = String.format(" (Past - Closed %s)", project.getClosingDate());
            }

            System.out.printf("  - %s (%s)%s\n",
                    project.getProjectName(),
                    project.getProjectId(),
                    statusTag);
        }
        System.out.println("-------------------------------------------");
        pause();

        // --- Step 4: Handle scenarios based on the number of ACTIVE projects ---
        Project selectedProject = null;

        if (activeManageableProjects.isEmpty()) {
            displayMessage("None of your approved projects are currently active for management.");
            return;

        } else if (activeManageableProjects.size() == 1) {
            selectedProject = activeManageableProjects.get(0);
            displayMessage("\nManaging the currently active project: " + selectedProject.getProjectName());

        } else {
            // Multiple ACTIVE projects - Prompt for selection from the ACTIVE list
            displayMessage("\nMultiple projects are active. Please select one to manage:");
            // Iterate ONLY over activeManageableProjects for selection menu
            for (int i = 0; i < activeManageableProjects.size(); i++) {
                Project p = activeManageableProjects.get(i);
                System.out.printf("[%d] %s (%s)\n", i + 1, p.getProjectName(), p.getProjectId());
            }
            System.out.println("-------------------------------------------");
            System.out.println("[0] Back to Main Officer Menu");
            System.out.println("===========================================");

            int choice = -1;
            while (selectedProject == null) {
                choice = promptForInt("Enter your choice: ");
                if (choice == 0) {
                    return;
                } else if (choice > 0 && choice <= activeManageableProjects.size()) {
                    selectedProject = activeManageableProjects.get(choice - 1);
                } else {
                    displayError("Invalid choice. Please enter a number between 0 and "
                            + activeManageableProjects.size() + ".");
                }
            }
            clearConsole();
        }

        // --- Step 5: Manage the SELECTED *ACTIVE* Project ---
        boolean keepManaging = true;
        while (keepManaging) {
            clearConsole();

            // --- Step 5a: Fetch Pending Count ---
            int projectSpecificPendingCount = 0;
            try {
                projectSpecificPendingCount = officerRegController.getPendingRegistrationCountForProject(
                        this.user,
                        selectedProject.getProjectId());
            } catch (AuthorizationException | RuntimeException e) {
                displayError("Warning: Could not fetch pending registration count: " + e.getMessage());
            }

            // --- Step 5b: Display Details for the SELECTED Project ---
            displayMessage("Managing Active Project: " + selectedProject.getProjectName() + " ("
                    + selectedProject.getProjectId() + ")");
            displayMessage("--------------------------------------------------");
            projectUIHelper.displayStaffProjectDetails(selectedProject, projectSpecificPendingCount);

            // --- Step 5c: Display Contextual Actions Sub-Menu ---
            System.out.println("\n--- Management Actions for this Active Project ---");
            System.out.println("[1] Book Flat for Successful Applicant");
            System.out.println("[2] Generate Booking Receipt for Applicant");
            System.out.println("[3] View / Reply Enquiries for this Project");
            System.out.println("-------------------------------------------");
            System.out.println("[0] Back");
            System.out.println("===========================================");

            int actionChoice = promptForInt("Enter action: ");

            try {
                switch (actionChoice) {
                    case 1:
                        handlePerformBookingAction(selectedProject);
                        break;
                    case 2:
                        handleGenerateReceiptAction(selectedProject);
                        break;
                    case 3:
                        handleViewAndReplyProjectEnquiriesAction(selectedProject.getProjectId());
                        break;
                    case 0:
                        keepManaging = false;
                        break;
                    default:
                        displayError("Invalid choice.");
                        break;
                }
            } catch (DataAccessException | BookingException | InvalidInputException e) {
                displayError("Operation failed: " + e.getMessage());
            } catch (Exception e) {
                displayError("An unexpected error occurred: " + e.getMessage());
            }

            if (keepManaging && actionChoice != 0) {
                pause();
            }
        }
    }

    /**
     * Handles booking by listing eligible (SUCCESSFUL) applicants for the
     * project and processing a flat booking.
     * <p>
     * This method guides the officer through:
     * - Viewing successful applicants eligible for booking
     * - Selecting an applicant
     * - Viewing available flat types
     * - Selecting and confirming a flat type
     * - Creating the booking through the BookingController
     * </p>
     *
     * @param project The project from which flats can be booked
     * @throws BookingException      If there's an error in the booking process
     * @throws InvalidInputException If invalid input is provided
     * @throws DataAccessException   If there's an error accessing project data
     */
    private void handlePerformBookingAction(Project project)
            throws BookingException, InvalidInputException, DataAccessException {
        displayHeader("Book Flat for Applicant (Project: " + project.getProjectId() + ")");

        // 1. Get and filter applications (Keep UI logic for now)
        List<Application> allProjectApps;
        try {
            allProjectApps = applicationController.getProjectApplications(this.user, project.getProjectId());
        } catch (ApplicationException e) {
            displayError("Failed to retrieve applications");
            return;
        }
        List<Application> successfulApps = allProjectApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL).collect(Collectors.toList());
        if (successfulApps.isEmpty()) {
            displayMessage("No applicants ready for booking.");
            return;
        }

        // 2. Display list of eligible applicants (Manual display linked to selection)
        displayMessage("--- Applicants Ready for Booking ---");
        AtomicInteger counter = new AtomicInteger(1);
        successfulApps.forEach(app -> {
            String applicantName = userController.getUserName(app.getApplicantNric());
            String preference = (app.getPreferredFlatType() != null) ? "Pref: " + app.getPreferredFlatType()
                    : "Pref: N/A";
            displayMessage(String.format("[%d] AppID: %s | NRIC: %s (%s) | %s",
                    counter.getAndIncrement(), app.getApplicationId(), app.getApplicantNric(), applicantName,
                    preference));
        });
        displayMessage("[0] Cancel");
        displayMessage("----------------------------------");

        // 3. Prompt Officer to select applicant
        int choice = promptForInt("Select applicant number: ");
        if (choice <= 0 || choice > successfulApps.size()) {
            displayMessage("Booking cancelled.");
            return;
        }
        Application selectedApp = successfulApps.get(choice - 1);
        FlatType applicantPreference = selectedApp.getPreferredFlatType();

        // 4. DELEGATE display of available flats to ProjectUIHelper
        Project currentProject = projectController.findProjectById(project.getProjectId()); // Refresh project data
        if (currentProject == null)
            throw new DataAccessException("Project " + project.getProjectId() + " not found for booking.", null);
        this.projectUIHelper.displayFlatAvailability(currentProject);
        if (applicantPreference != null) {
            displayMessage("Applicant's Preference: " + applicantPreference);
        }

        // 5. Prompt for final flat type
        FlatType finalFlatType = promptForEnum("Enter FINAL Flat Type chosen: ", FlatType.class,
                currentProject.getFlatTypes().keySet().stream() // Filter choices based on project offering
                        .collect(Collectors.toList()));
        if (finalFlatType == null) {
            displayMessage("Booking cancelled.");
            return;
        }

        // 6. Confirmation
        if (!promptForConfirmation(
                String.format("Confirm booking %s for %s?", finalFlatType, selectedApp.getApplicantNric()))) {
            displayMessage("Booking cancelled.");
            return;
        }

        // 7. Call Controller
        Booking booking = bookingController.createBooking(this.user, selectedApp.getApplicantNric(), finalFlatType);

        // 8. Display Success
        displayMessage("Booking successful! Booking ID: " + booking.getBookingId());
        displayMessage("Booked Flat Type: " + booking.getBookedFlatType());
    }

    /**
     * Handles the process of generating a booking receipt for a specific project.
     * <p>
     * This method guides the officer through:
     * - Viewing completed bookings for the project
     * - Selecting a specific booking
     * - Retrieving receipt details from the ReceiptController
     * - Displaying the formatted receipt
     * </p>
     *
     * @param project The project for which to generate receipts
     * @throws DataAccessException   If there's an error accessing booking data
     * @throws InvalidInputException If invalid input is provided
     * @throws BookingException      If there's an error retrieving bookings
     */
    private void handleGenerateReceiptAction(Project project)
            throws DataAccessException, InvalidInputException, BookingException { // Declare exceptions
                                                                                  // controller/service might throw

        Objects.requireNonNull(project, "Project cannot be null for generating receipts.");
        displayHeader("Generate Booking Receipt (Project: " + project.getProjectId() + ")");

        // 1. Get completed bookings for this project from the controller
        List<Booking> projectBookings;
        try {
            projectBookings = bookingController.getBookingsForProject(project.getProjectId());
        } catch (BookingException e) {
            displayError("Failed to retrieve bookings: " + e.getMessage());
            return;
        } catch (Exception e) {
            displayError("An unexpected error occurred while retrieving bookings: " + e.getMessage());
            return;
        }

        // 2. Check if there are any bookings to process
        if (projectBookings == null || projectBookings.isEmpty()) { // Added null check
            displayMessage("No completed bookings found for this project.");
            return;
        }

        // 3. Delegate booking list display AND selection to BookingUIHelper
        String listTitle = "--- Completed Bookings for Project " + project.getProjectId() + " ---";
        Booking selectedBooking = this.bookingUIHelper.selectBookingFromList(projectBookings, listTitle);

        // 4. Check if the user made a valid selection or cancelled
        if (selectedBooking == null) {
            return;
        }

        // 5. Call Receipt Controller to get the detailed info object
        BookingReceiptInfo receiptInfo = null; // Initialize
        try {
            // Pass the current HDBOfficer user for context/authorization if needed by
            // controller/service
            receiptInfo = receiptController.getBookingReceiptInfo(this.user, selectedBooking);
        } catch (Exception e) {
            displayError("Failed to generate receipt information: " + e.getMessage());
            return; // Cannot proceed if receipt info fails
        }

        // 6. Defensive Check: Ensure receiptInfo is not null
        if (receiptInfo == null) {
            displayError("Failed to retrieve receipt details (null returned from controller).");
            return;
        }

        // 7. Delegate the formatted display of the receipt to BookingUIHelper
        this.bookingUIHelper.displayBookingReceipt(receiptInfo);
    }

    /**
     * Handles viewing and replying to enquiries for the project being handled.
     * <p>
     * This method guides the officer through:
     * - Viewing all enquiries for the specific project
     * - Selecting an enquiry to view details
     * - Replying to unanswered enquiries
     * </p>
     *
     * @param projectId The ID of the project being managed
     * @throws InvalidInputException If invalid input is provided
     * @throws DataAccessException   If there's an error accessing enquiry data
     */
    private void handleViewAndReplyProjectEnquiriesAction(String projectId)
            throws InvalidInputException, DataAccessException {
        // 1. Fetch all enquiries for this project
        List<Enquiry> enquiries;
        try {
            enquiries = enquiryController.viewProjectEnquiries(this.user, projectId);
        } catch (Exception e) {
            displayError("Failed to retrieve enquiries for project " + projectId);
            return;
        }

        // 2. Use helper to display list and get selection
        String listTitle = "Enquiries for Project: " + projectId;
        Enquiry selectedEnquiry = enquiryUIHelper.selectEnquiryFromList(enquiries, listTitle);

        // 3. Check if user selected an enquiry or went back
        if (selectedEnquiry == null) {
            return;
        }

        // 4. Use helper to display full details of the selected enquiry
        enquiryUIHelper.displayEnquiryDetails(selectedEnquiry);

        // 5. Offer Reply option if applicable (Logic remains in this UI class)
        if (!selectedEnquiry.isReplied()) {
            System.out.println("\nOptions:");
            System.out.println("[1] Reply to this Enquiry");
            System.out.println("[0] Back"); // Back to the project management sub-menu

            int actionChoice = promptForInt("Enter option: ");
            if (actionChoice == 1) {
                // --- Reply Logic ---
                String replyContent = promptForInput("Enter your reply: ");
                if (replyContent.trim().isEmpty()) {
                    displayError("Reply content cannot be empty.");
                    pause();
                    return;
                }

                // Call Enquiry Controller
                try {
                    if (enquiryController.replyToEnquiry(this.user, selectedEnquiry.getEnquiryId(), replyContent)) {
                        displayMessage("Reply sent successfully.");
                    } else {
                        displayError("Failed to send reply. Please check or try again.");
                    }
                } catch (Exception e) { // Catch specific exceptions if controller throws them
                    displayError("Error sending reply: " + e.getMessage());
                }
                // Pause after action before potentially re-displaying sub-menu
                pause();
            }
        } else {
            // Already replied, just offer to go back
            System.out.println("\n[0] Back");
            promptForInt("Enter 0 to go back: "); // Just wait for back input
        }
    }

    /**
     * Handles the password change workflow for the HDB officer.
     * <p>
     * Delegates to the AccountUIHelper to manage the password change process.
     * </p>
     *
     * @return true if the password was successfully changed, false otherwise
     */
    private boolean handleChangePassword() {
        return accountUIHelper.handlePasswordChange(this.user);
    }
}