package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController; // Added
import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.Objects;
// Import other necessary Entity/Enum/Exception types as needed

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Objects;
// Import other necessary types

public class ApplicantUI extends BaseUI {
    private final Applicant user;
    private final UserController userController;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiryController enquiryController;
    private final AuthenticationController authController;
    private final ProjectUIHelper projectUIHelper; // Use the helper
    private final AccountUIHelper accountUIHelper; // Use the helper
    private final EnquiryUIHelper enquiryUIHelper;

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
        this.projectUIHelper = new ProjectUIHelper(this, userCtrl);
        this.enquiryUIHelper = new EnquiryUIHelper(this, userCtrl);
    }

    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            displayHeader("Applicant Menu - Welcome " + (user != null ? user.getName() : "User"));

            System.out.println("--- BTO Projects & Application ---");
            System.out.println("[1] View & Apply for Available Projects"); // Combined View/Apply Flow
            System.out.println("[2] View My Application Status & Request Withdrawal"); // Combined View/Withdrawal Flow
            System.out.println("----------------------------------");
            System.out.println("--- Enquiries ---");
            System.out.println("[3] Submit Enquiry");
            System.out.println("[4] Manage My Enquiries (View/Edit/Delete)"); // Combined Flow
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
                        handleChangePassword();
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
                // e.printStackTrace(); // For debugging
            }

            if (keepRunning && choice != 0) {
                pause();
            }
        }
    }

    // --- Revised Handler Methods ---

    private void handleViewAndApplyProjects() {
        displayHeader("View Available BTO Projects");
        List<Project> projects = projectController.getVisibleProjectsForUser(this.user);

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
                // Call the actual application submission logic
                handleSubmitApplicationAction(selectedProject.getProjectId());
            }
            // If 0 or other, automatically goes back as method ends
        }
        // If selectedProject is null, helper handled the message/back action.
    }

    // Extracted Apply logic, called after viewing details
    private void handleSubmitApplicationAction(String projectId) {
        System.out.println("Proceeding with application for Project ID: " + projectId);
        try {
            // TODO: Implement logic to prompt for preferredFlatType IF necessary
            // 1. Get Project details: projectController.findProjectById(projectId)
            // 2. Check user eligibility against available flat types in the project
            // 3. If eligible for multiple, prompt user: promptForEnum("Select preferred
            // type...", FlatType.class, true); // Allow null/no pref
            FlatType preference = null; // Placeholder - GET PREFERENCE HERE
            // ----------

            if (promptForConfirmation("Confirm application submission?")) {
                try {
                    Application app = applicationController.submitApplication(this.user, projectId, preference);
                    displayMessage("Application submitted successfully! ID: " + app.getApplicationId() + ", Status: "
                            + app.getStatus());
                } catch (ApplicationException e) {
                    // Handle specific application exceptions
                    displayError("Application submission failed: " + e.getMessage());
                } catch (Exception e) { // Catch other exceptions
                    displayError("An error occurred while submitting the application: " + e.getMessage());
                }
            } else {
                displayMessage("Application cancelled.");
            }
        } catch (Exception e) { // Catch ApplicationException etc.
            displayError("Failed to submit application: " + e.getMessage());
        }
    }

    private void handleViewAndWithdrawApplication() {
        displayHeader("View My BTO Application Status");
        // Application app = applicationController.getMyApplication(user);

        // if (app == null) {
        // displayMessage("You have no active or previous BTO application.");
        // return;
        // }

        // // Display Application Details (similar to before)
        // displayMessage("Application ID: " + app.getApplicationId());
        // displayMessage("Project ID: " + app.getProjectId());
        // displayMessage("Submission Date: " + app.getSubmissionDate()); // Add
        // formatting
        // displayMessage("Current Status: " + app.getStatus());
        // if (app.getPreferredFlatType() != null) {
        // displayMessage("Your Preference: " + app.getPreferredFlatType());
        // }
        // displayMessage("----------------------------------");

        // // --- Contextual Action: Offer withdrawal if applicable ---
        // // Check eligibility for withdrawal based on status (e.g., PENDING,
        // SUCCESSFUL,
        // // maybe even BOOKED based on rules)
        // boolean canWithdraw = (app.getStatus() == ApplicationStatus.PENDING
        // || app.getStatus() == ApplicationStatus.SUCCESSFUL); // Adjust as per exact
        // rules
        // // FAQ p54: Withdrawals before booking (Pending->Success->Unsuccessful),
        // after
        // // booking (Pending->Success->Booked->Unsuccessful)
        // // FAQ p5: Assume withdrawal is always successful (unless manager rejects in
        // // special case)
        // canWithdraw = !(app.getStatus() == ApplicationStatus.WITHDRAWN
        // || app.getStatus() == ApplicationStatus.UNSUCCESSFUL
        // || app.getStatus() == ApplicationStatus.WITHDRAWAL_REQUESTED);

        // if (canWithdraw) {
        // System.out.println("\nOptions:");
        // System.out.println("[1] Request Application Withdrawal");
        // System.out.println("[0] Back");

        // int actionChoice = promptForInt("Enter option: ");
        // if (actionChoice == 1) {
        // handleRequestWithdrawalAction(); // Call the withdrawal logic
        // }
        // } else {
        // displayMessage(
        // "Withdrawal is not applicable for the current application status (" +
        // app.getStatus() + ").");
        // System.out.println("\n[0] Back");
        // promptForInt("Enter 0 to go back: "); // Just wait for back input
        // }
    }

    // Extracted Withdrawal logic
    private void handleRequestWithdrawalAction() {
        if (promptForConfirmation(
                "Are you sure you want to request withdrawal? This cannot be undone easily. (yes/no): ")) {
            try {
                boolean success = applicationController.requestWithdrawal(this.user);
                if (success) {
                    // FAQ p5: Assume normally successful, Manager might reject later.
                    // Status might change to WITHDRAWAL_REQUESTED or directly to
                    // WITHDRAWN/UNSUCCESSFUL depending on service impl.
                    displayMessage(
                            "Withdrawal request submitted. Check status later or assume successful based on FAQ.");
                } else {
                    displayError("Failed to submit withdrawal request (possibly due to status).");
                }
            } catch (Exception e) { // Catch ApplicationException etc.
                displayError("Error requesting withdrawal: " + e.getMessage());
            }
        } else {
            displayMessage("Withdrawal request cancelled.");
        }
    }

    private void handleSubmitEnquiry() {
        displayHeader("Submit Enquiry");
        // TODO: Implement logic:
        // 1. Prompt if project-specific, get Project ID.
        // 2. Prompt for content.
        // 3. Call enquiryController.createEnquiry(user, projectId, content);
        // 4. Display result.

        List<Project> projects = projectController.getVisibleProjectsForUser(this.user);

        Project selectedProject = projectUIHelper.selectProjectFromList(projects,
                "Select Project to Submit Enquiry");

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
            System.out.println("[Placeholder: View, Edit, Delete own enquiries]");
        }
    }

    // Helper method to handle actual edit action
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

    // Helper method to handle actual delete action
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

    private void handleChangePassword() {
        accountUIHelper.handlePasswordChange(this.user);
    }
}