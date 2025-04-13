package com.ntu.fdae.group1.bto.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;

/**
 * Helper class for application-related UI operations in the BTO Management
 * System.
 * <p>
 * This class provides reusable UI components for handling BTO application
 * workflows,
 * including application submission, status viewing, withdrawal requests, and
 * listing
 * applications for administrative review.
 * </p>
 * <p>
 * The helper follows a composition pattern, working with a BaseUI instance for
 * common UI operations and controllers for application and project data access.
 * It is designed to be used by both applicant-focused UIs (ApplicantUI) and
 * administrative UIs (HDBOfficerUI, HDBManagerUI) to maintain consistent user
 * experience and reduce code duplication.
 * </p>
 */
public class ApplicationUIHelper {

    /**
     * The base UI component for common UI operations.
     */
    private final BaseUI baseUI;

    /**
     * The controller for application-related operations.
     */
    private final ApplicationController applicationController;

    /**
     * The controller for project-related operations, needed for fetching project
     * details during application processes.
     */
    private final ProjectController projectController;

    /**
     * Constructs a new ApplicationUIHelper with the specified dependencies.
     *
     * @param baseUI   An instance of BaseUI for console I/O operations
     * @param appCtrl  Controller for application-related operations
     * @param projCtrl Controller for project-related operations
     * @throws NullPointerException if any parameter is null
     */
    public ApplicationUIHelper(BaseUI baseUI, ApplicationController appCtrl, ProjectController projCtrl) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null");
        this.applicationController = Objects.requireNonNull(appCtrl, "ApplicationController cannot be null");
        this.projectController = Objects.requireNonNull(projCtrl, "ProjectController cannot be null");
    }

    /**
     * Guides the user through submitting an application for a specific project,
     * including handling flat type preferences based on eligibility.
     * <p>
     * This method:
     * - Retrieves the project details
     * - Determines eligible flat types for the user
     * - Prompts for preference selection if multiple eligible types exist
     * - Confirms submission with the user
     * - Submits the application via the ApplicationController
     * - Displays success confirmation or error messages
     * </p>
     *
     * @param user      The applicant user submitting the application
     * @param projectId The ID of the project they are applying for
     */
    public void performApplicationSubmission(User user, String projectId) {
        baseUI.displayMessage("\nPreparing application for Project ID: " + projectId + "...");

        try {
            // 1. Fetch Project Details (to check available flat types for preference
            // prompt)
            Project project = projectController.findProjectById(projectId);
            if (project == null) {
                baseUI.displayError("Could not retrieve project details (ID: " + projectId + ") to proceed.");
                return;
            }
            if (project.getFlatTypes() == null || project.getFlatTypes().isEmpty()) {
                baseUI.displayError("Selected project '" + project.getProjectName() + "' has no flat types defined.");
                return;
            }

            // 2. Determine Eligible Flat Types for THIS user and THIS project
            List<FlatType> eligibleTypes = new ArrayList<>();
            if (project.getFlatTypes().containsKey(FlatType.TWO_ROOM)
                    && isApplicantEligibleForFlatType(user, FlatType.TWO_ROOM)) {
                eligibleTypes.add(FlatType.TWO_ROOM);
            }
            if (project.getFlatTypes().containsKey(FlatType.THREE_ROOM)
                    && isApplicantEligibleForFlatType(user, FlatType.THREE_ROOM)) {
                eligibleTypes.add(FlatType.THREE_ROOM);
            }
            // Add checks for other flat types if needed

            // 3. Determine Preference: Implicitly set or prompt if multiple options
            FlatType determinedPreference = null;
            boolean requiresChoice = false;

            if (eligibleTypes.isEmpty()) {
                // This check is defensive; getVisibleProjects should prevent this scenario
                baseUI.displayError(
                        "Error: No eligible flat types found for you in project '" + project.getProjectName() + "'.");
                return;
            } else if (eligibleTypes.size() == 1) {
                // Implicitly set if only one eligible type offered
                determinedPreference = eligibleTypes.get(0);
                baseUI.displayMessage("Based on eligibility, your application targets: "
                        + baseUI.formatEnumName(determinedPreference));
            } else {
                // Multiple eligible types offered, user needs to choose
                requiresChoice = true;
            }

            // 4. Prompt for Preference ONLY if required
            if (requiresChoice) {
                baseUI.displayMessage("This project offers multiple flat types you are eligible for.");

                determinedPreference = baseUI.promptForEnum(
                        "Select your preferred flat type:",
                        FlatType.class, // Pass the Enum class
                        eligibleTypes // Pass the list of ONLY the valid choices
                );

                // If user cancels the selection, exit the submission process
                if (determinedPreference == null) {
                    baseUI.displayMessage("Cancelling application submission.");
                    return; // Exit the submission process
                }
            }

            // 5. Final Confirmation
            String confirmationPrompt = "Confirm application submission for project " + project.getProjectName() + " ("
                    + projectId + ")";
            if (determinedPreference != null) {
                confirmationPrompt += " (Preference: " + baseUI.formatEnumName(determinedPreference)
                        + ")?";
            } else {
                confirmationPrompt += " (Specific type based on eligibility or no preference indicated)?";
            }

            // If promptForConfirmation returns false, user cancelled.
            if (!baseUI.promptForConfirmation(confirmationPrompt)) {
                baseUI.displayMessage("Application submission cancelled.");
                return; // Exit the submission process
            }

            // 6. Call Controller/Service with the *determined* preference
            Application app = applicationController.submitApplication(user, projectId, determinedPreference);

            // 7. Display Success Feedback
            baseUI.displayMessage("----------------------------------");
            baseUI.displayMessage("Application Submitted Successfully!");
            baseUI.displayMessage("----------------------------------");
            baseUI.displayMessage("Application ID: " + app.getApplicationId());
            baseUI.displayMessage("Project Name:   " + project.getProjectName()); // Display project name for context
            baseUI.displayMessage("Initial Status: " + app.getStatus());
            if (app.getPreferredFlatType() != null) {
                baseUI.displayMessage("Preference Recorded: " + baseUI.formatEnumName(app.getPreferredFlatType()));
            } else {
                baseUI.displayMessage("Preference Recorded: None / Single Type Target");
            }
            baseUI.displayMessage("----------------------------------");

        } catch (ApplicationException e) {
            // Catch specific exceptions from the service layer
            baseUI.displayError("Application Failed: " + e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            baseUI.displayError("An unexpected error occurred during application: " + e.getMessage());
        }
    }

    /**
     * Displays the details of the applicant's current or most recent application
     * and provides an option to request withdrawal if applicable.
     * <p>
     * This method:
     * - Retrieves the user's application via the ApplicationController
     * - Displays comprehensive application details (ID, project, status, etc.)
     * - Determines if withdrawal is allowed based on the application status
     * - Presents withdrawal option when applicable
     * - Delegates to performWithdrawalAction if withdrawal is chosen
     * </p>
     *
     * @param user The applicant user whose application is being viewed
     */
    public void performViewAndWithdraw(User user) {
        baseUI.displayHeader("View My BTO Application Status");
        try {
            Application app = applicationController.getMyApplication(user);

            if (app == null) {
                baseUI.displayMessage("You have no active or previous BTO application.");
                return; // Nothing more to do
            }

            // Display Application Details
            // Fetch project name for better context
            String projectName = "N/A";
            Project project = projectController.findProjectById(app.getProjectId());
            if (project != null) {
                projectName = project.getProjectName();
            }

            baseUI.displayMessage("Application ID:    " + app.getApplicationId());
            baseUI.displayMessage("Project ID:        " + app.getProjectId() + " (" + projectName + ")");
            baseUI.displayMessage("Submission Date: " + app.getSubmissionDate()); // Add formatting if needed
            baseUI.displayMessage("Current Status:    " + baseUI.formatEnumName(app.getStatus()));
            if (app.getPreferredFlatType() != null) {
                baseUI.displayMessage("Preference/Target: " + baseUI.formatEnumName(app.getPreferredFlatType()));
            }
            // Display withdrawal date if tracked?
            // if (app.getRequestedWithdrawalDate() != null) { ... }
            baseUI.displayMessage("----------------------------------");

            // Determine if withdrawal is possible based on the 4 allowed statuses
            // Withdrawal allowed from Pending, Successful, Booked?
            // Leads to UNSUCCESSFUL status.
            boolean canWithdraw = (app.getStatus() == ApplicationStatus.PENDING ||
                    app.getStatus() == ApplicationStatus.SUCCESSFUL ||
                    app.getStatus() == ApplicationStatus.BOOKED);

            if (canWithdraw) {
                System.out.println("\nOptions:");
                System.out.println("[1] Request Application Withdrawal");
                System.out.println("[0] Back");

                int actionChoice = baseUI.promptForInt("Enter option: ");
                if (actionChoice == 1) {
                    performWithdrawalAction(user); // Call the separate withdrawal logic
                }
            } else {
                baseUI.displayMessage("Withdrawal is not applicable for the current application status ("
                        + baseUI.formatEnumName(app.getStatus()) + ").");
            }
        } catch (Exception e) {
            baseUI.displayError("Error retrieving application status: " + e.getMessage());
        }
    }

    /**
     * Handles the confirmation and controller call for application withdrawal
     * request.
     * <p>
     * This method:
     * - Prompts for final withdrawal confirmation
     * - Submits the request via the ApplicationController if confirmed
     * - Displays appropriate success or error messages to the user
     * </p>
     * <p>
     * This method is intended to be called only when withdrawal is deemed possible
     * based on the application's current status.
     * </p>
     *
     * @param user The applicant requesting application withdrawal
     */
    private void performWithdrawalAction(User user) {
        if (baseUI.promptForConfirmation(
                "Are you sure you want to request withdrawal?")) {
            try {
                boolean success = applicationController.requestWithdrawal(user);
                // Based on FAQ, assume normal success leads to status change (handled by
                // service)
                if (success) {
                    baseUI.displayMessage(
                            "Withdrawal request submitted.");
                    baseUI.displayMessage("Please check the status again if needed.");
                } else {
                    // This might indicate an unexpected state or error in service logic
                    baseUI.displayError(
                            "Failed to submit withdrawal request. The application status might not allow it.");
                }
            } catch (ApplicationException e) {
                baseUI.displayError("Error requesting withdrawal: " + e.getMessage());
            } catch (Exception e) {
                baseUI.displayError("An unexpected error occurred during withdrawal: " + e.getMessage());
            }
        } else {
            baseUI.displayMessage("Withdrawal request cancelled.");
        }
    }

    /**
     * Determines if an applicant is eligible for a specific flat type based on
     * BTO application rules.
     * <p>
     * The eligibility rules applied are:
     * - Single applicants aged 35 or older: eligible only for 2-Room flats
     * - Married applicants aged 21 or older: eligible for both 2-Room and 3-Room
     * flats
     * - All other applicants: not eligible
     * </p>
     *
     * @param user     The applicant user to check eligibility for
     * @param flatType The flat type to check eligibility against
     * @return true if the applicant is eligible for the flat type, false otherwise
     */
    private boolean isApplicantEligibleForFlatType(User user, FlatType flatType) {
        if (user == null || flatType == null)
            return false;
        int age = user.getAge();
        MaritalStatus status = user.getMaritalStatus();
        if (status == MaritalStatus.SINGLE && age >= 35) {
            return flatType == FlatType.TWO_ROOM;
        } else if (status == MaritalStatus.MARRIED && age >= 21) {
            return flatType == FlatType.TWO_ROOM || flatType == FlatType.THREE_ROOM;
        } else {
            return false;
        }
    }

    /**
     * Displays a formatted list of applications and returns a map for selection.
     * <p>
     * This method presents a comprehensive list of applications with key details:
     * - Application ID
     * - Applicant NRIC
     * - Project name and ID
     * - Application status (including withdrawal requests)
     * - Flat type preference
     * - Submission date
     * </p>
     * <p>
     * The method returns a map that associates displayed index numbers with their
     * corresponding Application objects, facilitating easy selection in calling
     * methods.
     * </p>
     * 
     * @param apps  List of applications to display
     * @param title Title for the list header
     * @return A map where keys are display indices and values are Application
     *         objects
     *         Returns an empty map if the input list is null or empty
     */
    public Map<Integer, Application> displayApplicationList(List<Application> apps, String title) {
        baseUI.displayHeader(title); // Use injected BaseUI
        Map<Integer, Application> appMap = new HashMap<>();
        if (apps == null || apps.isEmpty()) {
            baseUI.displayMessage("No applications to display in this list.");
            return appMap;
        }

        int index = 1;
        for (Application app : apps) {
            Project proj = projectController.findProjectById(app.getProjectId());
            String projName = (proj != null) ? proj.getProjectName() : "Unknown/Deleted";
            String withdrawalStatus = app.getRequestedWithdrawalDate() != null ? " (Withdrawal Req.)" : ""; // Shorter
            String preference = (app.getPreferredFlatType() != null) ? app.getPreferredFlatType().name() : "N/A";

            // Format the string for display
            String formattedString = String.format(
                    "[%d] AppID: %-10s | Applicant: %-9s | Project: %s (%s) | Status: %-12s%s | Pref: %-8s | Date: %s",
                    index,
                    app.getApplicationId(),
                    app.getApplicantNric(),
                    projName, // Display name
                    app.getProjectId(), // Display ID
                    app.getStatus(), // Enum name is usually fine
                    withdrawalStatus,
                    preference,
                    baseUI.formatDateSafe(app.getSubmissionDate()));
            baseUI.displayMessage(formattedString); // Use injected BaseUI
            appMap.put(index, app);
            index++;
        }
        baseUI.displayMessage("[0] Back / Cancel"); // Use injected BaseUI
        return appMap;
    }
}