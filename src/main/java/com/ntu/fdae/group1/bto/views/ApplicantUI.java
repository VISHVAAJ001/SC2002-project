package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Objects;

/**
 * User interface for Applicant users in the BTO Management System.
 * <p>
 * This class provides a console-based interface for applicants to interact with
 * the system. It allows applicants to:
 * - Browse available BTO projects
 * - Apply for BTO flats
 * - View and withdraw applications
 * - Submit and manage enquiries
 * - Change their password
 * </p>
 * <p>
 * The UI follows a menu-driven approach, with each option delegating to
 * specific
 * handler methods. It leverages various UI helper classes to manage complex
 * operations while maintaining separation of concerns.
 * </p>
 */
public class ApplicantUI extends BaseUI {
    /**
     * The authenticated applicant user currently using the interface.
     */
    private final Applicant user;

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
    @SuppressWarnings("unused")
    private final ApplicationController applicationController;

    /**
     * Controller for enquiry-related operations.
     */
    private final EnquiryController enquiryController;

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
     * Helper for enquiry-related UI operations.
     */
    private final EnquiryUIHelper enquiryUIHelper;

    /**
     * Helper for application-related UI operations.
     */
    private final ApplicationUIHelper applicationUIHelper;

    /**
     * Current filters applied to project listings.
     */
    private Map<String, Object> currentProjectFilters;

    /**
     * Constructs a new ApplicantUI with the specified dependencies.
     *
     * @param user     The authenticated applicant user
     * @param userCtrl Controller for user operations
     * @param projCtrl Controller for project operations
     * @param appCtrl  Controller for application operations
     * @param enqCtrl  Controller for enquiry operations
     * @param authCtrl Controller for authentication operations
     * @param scanner  Scanner for reading user input
     * @throws NullPointerException if any parameter is null
     */
    public ApplicantUI(Applicant user,
            UserController userCtrl,
            ProjectController projCtrl,
            ApplicationController appCtrl,
            EnquiryController enqCtrl,
            AuthenticationController authCtrl,
            Scanner scanner) {
        super(scanner);
        this.user = Objects.requireNonNull(user);
        this.userController = Objects.requireNonNull(userCtrl);
        this.projectController = Objects.requireNonNull(projCtrl);
        this.applicationController = Objects.requireNonNull(appCtrl);
        this.enquiryController = Objects.requireNonNull(enqCtrl);
        this.authController = Objects.requireNonNull(authCtrl);
        this.accountUIHelper = new AccountUIHelper(this, authCtrl);
        this.projectUIHelper = new ProjectUIHelper(this, userCtrl, projCtrl);
        this.enquiryUIHelper = new EnquiryUIHelper(this, userCtrl, projCtrl);
        this.applicationUIHelper = new ApplicationUIHelper(this, appCtrl, projCtrl, userCtrl);
        this.currentProjectFilters = new HashMap<>();
    }

    /**
     * Displays the main menu for applicant users and handles their selections.
     * <p>
     * This method shows a menu of options available to applicants and delegates
     * to specific handler methods based on user input. It runs in a loop until
     * the user chooses to log out.
     * </p>
     */
    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            if (user != null) {
                displayHeader("Applicant Menu - Welcome " + user.getName() + " (" + user.getAge() + ", "
                        + user.getMaritalStatus() + ")");
            } else {
                displayHeader("Applicant Menu - Welcome User (Age, Marital Status)");
            }

            System.out.println("--- BTO Projects & Application ---");
            System.out.println("[1] View & Apply for Available Projects");
            System.out.println("[2] View My Application Status & Request Withdrawal");
            System.out.println("----------------------------------");
            System.out.println("--- Enquiries ---");
            System.out.println("[3] Submit Enquiry");
            System.out.println("[4] Manage My Enquiries (View/Edit/Delete)");
            System.out.println("----------------------------------");
            System.out.println("--- Account ---");
            System.out.println("[5] Change Password");
            System.out.println("----------------------------------");
            System.out.println("[0] Logout");
            System.out.println("==================================");

            int choice = promptForInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        handleViewAndApplyProjects();
                        break;
                    case 2:
                        handleViewAndWithdrawApplication();
                        break;
                    case 3:
                        handleSubmitEnquiry();
                        break;
                    case 4:
                        handleManageMyEnquiries();
                        break;
                    case 5:
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

    /**
     * Handles the workflow for viewing and applying for BTO projects.
     * Restrict certain users (based on application status) from applying to
     * projects.
     * <p>
     * This method allows applicants to:
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
                    boolean isSingle = (this.user != null && this.user.getMaritalStatus() == MaritalStatus.SINGLE);
                    this.currentProjectFilters = projectUIHelper.promptForProjectFilters(false, isSingle); // Get new filters
                    break;
                case 0:
                default: // Includes Back or invalid choice
                    displayMessage("Returning to main menu.");
                    return; // Exit the handleView method
            }
        } else {
            // No filters were active, ask if they want to apply some now
            if (promptForConfirmation("Apply filters before viewing?:")) {
                boolean isSingle = (this.user != null && this.user.getMaritalStatus() == MaritalStatus.SINGLE);
                this.currentProjectFilters = projectUIHelper.promptForProjectFilters(false, isSingle);
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
     * Delegates to the ApplicationUIHelper for displaying the applicant's
     * applications and handling withdrawal requests.
     * </p>
     */
    private void handleViewAndWithdrawApplication() {
        applicationUIHelper.performViewAndWithdraw(this.user);
    }

    /**
     * Handles the workflow for submitting a new enquiry.
     * <p>
     * This method allows applicants to:
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
     * Handles the workflow for managing existing enquiries.
     * <p>
     * This method allows applicants to:
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
     * Handles the action of editing an existing enquiry.
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
     * Handles the action of deleting an existing enquiry.
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
     * Handles the password change workflow for the applicant.
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