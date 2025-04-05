package com.ntu.fdae.group1.bto.views;

// Import necessary controllers
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.project.ReportController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController; // Added

// Import necessary models
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.project.*; // Import project models
import com.ntu.fdae.group1.bto.enums.*; // Import enums

// Import exceptions if needed for specific handling in UI
// import com.ntu.fdae.group1.bto.exceptions.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate; // For creating project dates

import java.util.Objects;
// Import other necessary Entity/Enum/Exception types as needed

import java.util.List;
import java.util.Map; // For filters maybe
import java.util.Scanner;
import java.util.Objects;
// Import other necessary types

public class HDBManagerUI extends BaseUI {
    private final HDBManager user;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final OfficerRegistrationController officerRegController;
    private final EnquiryController enquiryController;
    private final ReportController reportController;
    private final AuthenticationController authController;
    private final ProjectUIHelper projectUIHelper; // Use the helper

    public HDBManagerUI(HDBManager user,
            ProjectController projCtrl,
            ApplicationController appCtrl,
            OfficerRegistrationController offRegCtrl,
            EnquiryController enqCtrl,
            ReportController reportCtrl,
            AuthenticationController authCtrl,
            Scanner scanner) {
        super(scanner);
        this.user = Objects.requireNonNull(user);
        this.projectController = Objects.requireNonNull(projCtrl);
        this.applicationController = Objects.requireNonNull(appCtrl);
        this.officerRegController = Objects.requireNonNull(offRegCtrl);
        this.enquiryController = Objects.requireNonNull(enqCtrl);
        this.reportController = Objects.requireNonNull(reportCtrl);
        this.authController = Objects.requireNonNull(authCtrl);
        this.projectUIHelper = new ProjectUIHelper(this); // Initialize helper
    }

    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            displayHeader("HDB Manager Menu - Welcome " + (user != null ? user.getName() : "User"));

            System.out.println("--- Project Management ---");
            System.out.println("[1] Create New BTO Project");
            System.out.println("[2] View/Manage Projects (Edit/Delete/Visibility)"); // Combined view & manage
            System.out.println("----------------------------------");
            System.out.println("--- Approvals & Reviews ---");
            System.out.println("[3] Review Pending Officer Registrations"); // View->Review flow
            System.out.println("[4] Review Pending BTO Applications"); // View->Review flow
            System.out.println("[5] Review Pending Application Withdrawals"); // View->Review flow
            System.out.println("----------------------------------");
            System.out.println("--- Reporting ---");
            System.out.println("[6] Generate Booking Report");
            System.out.println("----------------------------------");
            System.out.println("--- Enquiries ---");
            System.out.println("[7] View & Reply to Enquiries"); // Combined view & manage
            System.out.println("----------------------------------");
            System.out.println("--- Account ---");
            System.out.println("[8] Change Password");
            System.out.println("----------------------------------");
            System.out.println("[0] Logout");
            System.out.println("==================================");

            int choice = promptForInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        handleCreateProject();
                        break;
                    case 2:
                        handleViewAndManageProjects();
                        break;
                    case 3:
                        handleReviewOfficerRegistrations();
                        break;
                    case 4:
                        handleReviewApplications();
                        break;
                    case 5:
                        handleReviewWithdrawals();
                        break;
                    case 6:
                        handleGenerateReport();
                        break;
                    case 7:
                        handleViewAndReplyToEnquiries();
                        break;
                    case 8:
                        handleChangePassword();
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

    private void handleCreateProject() {
        displayHeader("Create New BTO Project");
        System.out.println("Calling projectController.createProject...");
        // TODO: Implement input gathering & controller call
        System.out.println("[Placeholder: Create a new project listing]");
    }

    private void handleViewAndManageProjects() {
        displayHeader("View/Manage Projects");
        // TODO: Implement filter prompting if desired
        // Map<String, String> filters = projectUIHelper.promptForProjectFilters();
        // List<Project> projects = projectController.getAllProjects(this.user /* ,
        // filters */); // Manager sees all

        // Project selectedProject = projectUIHelper.selectProjectFromList(projects,
        // "Select Project to View Details/Manage");

        // if (selectedProject != null) {
        // // Use the STAFF view helper method
        // projectUIHelper.displayStaffProjectDetails(selectedProject);

        // // --- Contextual Actions for Manager ---
        // System.out.println("\nOptions for Project " + selectedProject.getProjectId()
        // + ":");
        // System.out.println("[1] Edit Project Details");
        // System.out.println("[2] Delete Project");
        // System.out
        // .println("[3] Toggle Visibility (Currently " + (selectedProject.isVisible() ?
        // "ON" : "OFF") + ")");
        // System.out.println("[0] Back");

        // int actionChoice = promptForInt("Enter option: ");
        // switch (actionChoice) {
        // case 1:
        // handleEditProjectAction(selectedProject);
        // break;
        // case 2:
        // handleDeleteProjectAction(selectedProject.getProjectId());
        // break;
        // case 3:
        // handleToggleVisibilityAction(selectedProject.getProjectId());
        // break;
        // // Default or 0: Do nothing, loop will pause and show menu again
        // }
        // }
    }

    // Extracted Action Logic Helpers
    private void handleEditProjectAction(Project project) {
        System.out.println("Editing Project ID: " + project.getProjectId());
        // TODO: Prompt for new Name, Neighbourhood, Dates, Slots. Get current values
        // from 'project'.
        // TODO: Call projectController.editProject(...)
        System.out.println("[Placeholder: Edit project action]");
    }

    private void handleDeleteProjectAction(String projectId) {
        System.out.println("Deleting Project ID: " + projectId);
        // TODO: Confirm deletion. Call projectController.deleteProject(...)
        System.out.println("[Placeholder: Delete project action]");
    }

    private void handleToggleVisibilityAction(String projectId) {
        System.out.println("Toggling visibility for Project ID: " + projectId);
        // TODO: Confirm toggle. Call projectController.toggleVisibility(...)
        System.out.println("[Placeholder: Toggle visibility action]");
    }
    // --------------------

    private void handleReviewOfficerRegistrations() {
        displayHeader("Review Pending Officer Registrations");
        // TODO: Implement View->Select->Detail->Action flow
        // 1. Call officerRegController.getPendingRegistrations(user).
        // 2. Display list of pending registrations.
        // 3. Prompt selection.
        // 4. If selected:
        // a. Display details of the registration (Officer NRIC, Project ID, Date).
        // b. Display Actions: "[1] Approve", "[2] Reject", "[0] Back".
        // c. Get action choice.
        // d. If Approve/Reject: Call officerRegController.reviewRegistration(user,
        // regId, isApproved).
        // e. Display success/error.
        System.out.println("[Placeholder: List pending regs -> Select -> Approve/Reject]");
    }

    private void handleReviewApplications() {
        displayHeader("Review Pending BTO Applications");
        // TODO: Implement View->Select->Detail->Action flow
        // 1. Call applicationController.getApplicationsByStatus(user,
        // ApplicationStatus.PENDING).
        // 2. Display list of pending applications.
        // 3. Prompt selection.
        // 4. If selected:
        // a. Display details (App ID, Applicant NRIC, Project ID, Pref?).
        // b. Display Actions: "[1] Approve", "[2] Reject", "[0] Back".
        // c. Get action choice.
        // d. If Approve/Reject: Call applicationController.reviewApplication(user,
        // appId, isApproved).
        // e. Display success/error.
        System.out.println("[Placeholder: List pending apps -> Select -> Approve/Reject]");
    }

    private void handleReviewWithdrawals() {
        displayHeader("Review Pending Application Withdrawals");
        // TODO: Implement View->Select->Detail->Action flow
        // 1. Call applicationController.getApplicationsByStatus(user,
        // ApplicationStatus.WITHDRAWAL_REQUESTED).
        // 2. Display list.
        // 3. Prompt selection.
        // 4. If selected:
        // a. Display details.
        // b. Display Actions: "[1] Approve Withdrawal", "[2] Reject Withdrawal", "[0]
        // Back".
        // c. Get action choice.
        // d. If Approve/Reject: Call applicationController.reviewWithdrawal(user,
        // appId, isApproved).
        // e. Display success/error.
        System.out.println("[Placeholder: List withdrawal reqs -> Select -> Approve/Reject]");
    }

    private void handleGenerateReport() {
        displayHeader("Generate Applicant Booking Report");
        // TODO: Implement input gathering for filters & controller call
        System.out.println("[Placeholder: Generate and display report based on filters]");
    }

    private void handleViewAndReplyToEnquiries() {
        displayHeader("View & Reply to Enquiries");
        // TODO: Implement View->Select->Detail->Action flow
        // 1. Call enquiryController.viewAllEnquiries(user). (Manager sees all FAQ p57)
        // 2. Display list.
        // 3. Prompt selection.
        // 4. If selected:
        // a. Display full details (Content, Submitter, Project?, Reply if exists).
        // b. If not replied, display Actions: "[1] Reply", "[0] Back".
        // c. If Reply: Prompt for reply content, call
        // enquiryController.replyToEnquiry(user, enquiryId, replyContent).
        // d. Display success/error.
        System.out.println("[Placeholder: List all enquiries -> Select -> Reply]");
    }

    private void handleChangePassword() {
        displayHeader("Change Password");
        // TODO: Implement logic (same as other UIs)
        System.out.println("[Placeholder: Change user's password]");
    }
}