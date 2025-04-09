package com.ntu.fdae.group1.bto.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;

/**
 * Helper class to manage common UI tasks related to BTO Application viewing,
 * submission (including preference selection), and withdrawal requests.
 * Used via composition by ApplicantUI and HDBOfficerUI (for applicant actions).
 */
public class ApplicationUIHelper {

    private final BaseUI baseUI;
    private final ApplicationController applicationController;
    private final ProjectController projectController; // Needed for preference check

    public ApplicationUIHelper(BaseUI baseUI, ApplicationController appCtrl, ProjectController projCtrl) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null");
        this.applicationController = Objects.requireNonNull(appCtrl, "ApplicationController cannot be null");
        this.projectController = Objects.requireNonNull(projCtrl, "ProjectController cannot be null");
    }

    /**
     * Guides the user through submitting an application for a specific project,
     * including handling flat type preferences based on eligibility.
     * Assumes this is called after the user has selected a project.
     *
     * @param applicant The applicant user submitting the application.
     * @param projectId The ID of the project they are applying for.
     */
    public void performApplicationSubmission(Applicant applicant, String projectId) {
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
                    && isApplicantEligibleForFlatType(applicant, FlatType.TWO_ROOM)) {
                eligibleTypes.add(FlatType.TWO_ROOM);
            }
            if (project.getFlatTypes().containsKey(FlatType.THREE_ROOM)
                    && isApplicantEligibleForFlatType(applicant, FlatType.THREE_ROOM)) {
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

                // Use the enhanced promptForEnum from BaseUI
                // It returns null if user cancels or chooses the 'no preference' option
                determinedPreference = baseUI.promptForEnum(
                        "Select your preferred flat type:",
                        FlatType.class, // Pass the Enum class
                        eligibleTypes // Pass the list of ONLY the valid choices
                );

                if (determinedPreference == null) {
                    baseUI.displayMessage("Cancelling application submission.");
                    return; // Exit the submission process
                }
            }

            // 5. Final Confirmation
            String confirmationPrompt = "Confirm application submission for project " + project.getProjectName() + " ("
                    + projectId + ")";
            if (determinedPreference != null) {
                confirmationPrompt += " (Preference/Target: " + baseUI.formatEnumName(determinedPreference)
                        + ")? (yes/no): ";
            } else {
                confirmationPrompt += " (Specific type based on eligibility or no preference indicated)? (yes/no): ";
            }

            // If promptForConfirmation returns false, user cancelled.
            if (!baseUI.promptForConfirmation(confirmationPrompt)) {
                baseUI.displayMessage("Application submission cancelled.");
                return; // Exit the submission process
            }

            // 6. Call Controller/Service with the *determined* preference
            Application app = applicationController.submitApplication(applicant, projectId, determinedPreference);

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
            e.printStackTrace(); // Helpful during development
        }
    }

    /**
     * Displays the details of the applicant's current or most recent application
     * and provides an option to request withdrawal if applicable based on status.
     *
     * @param applicant The applicant user whose application is being viewed.
     */
    public void performViewAndWithdraw(Applicant applicant) {
        baseUI.displayHeader("View My BTO Application Status");
        try {
            Application app = applicationController.getMyApplication(applicant);

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
                    performWithdrawalAction(applicant); // Call the separate withdrawal logic
                }
                // If 0 or other, method ends, returning to previous menu loop
            } else {
                baseUI.displayMessage("Withdrawal is not applicable for the current application status ("
                        + baseUI.formatEnumName(app.getStatus()) + ").");
                // Optionally add a prompt to go back if needed, or let the main loop pause
                // handle it.
                // System.out.println("\n[0] Back");
                // baseUI.promptForInt("Enter 0 to go back: ");
            }
        } catch (Exception e) {
            baseUI.displayError("Error retrieving application status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the confirmation and controller call for withdrawal request.
     * Assumes called only when withdrawal is deemed possible.
     *
     * @param applicant The applicant requesting withdrawal.
     */
    private void performWithdrawalAction(Applicant applicant) {
        if (baseUI.promptForConfirmation(
                "Are you sure you want to request withdrawal? This may change your application status to Unsuccessful. (yes/no): ")) {
            try {
                boolean success = applicationController.requestWithdrawal(applicant);
                // Based on FAQ, assume normal success leads to status change (handled by
                // service)
                if (success) {
                    baseUI.displayMessage(
                            "Withdrawal request submitted. Your application status should now be Unsuccessful.");
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
                // e.printStackTrace();
            }
        } else {
            baseUI.displayMessage("Withdrawal request cancelled.");
        }
    }

    /**
     * Helper method to check if an applicant is eligible for a specific flat type.
     * Note: This logic might ideally live in an EligibilityService.
     *
     * @param applicant The applicant.
     * @param flatType  The flat type to check.
     * @return true if eligible, false otherwise.
     */
    private boolean isApplicantEligibleForFlatType(Applicant applicant, FlatType flatType) {
        if (applicant == null || flatType == null)
            return false;
        int age = applicant.getAge();
        MaritalStatus status = applicant.getMaritalStatus();
        if (status == MaritalStatus.SINGLE && age >= 35) {
            return flatType == FlatType.TWO_ROOM;
        } else if (status == MaritalStatus.MARRIED && age >= 21) {
            return flatType == FlatType.TWO_ROOM || flatType == FlatType.THREE_ROOM;
        } else {
            return false;
        }
    }
}