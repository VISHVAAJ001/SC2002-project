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

public class HDBManagerUI extends BaseUI {
    private final HDBManager user;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final OfficerRegistrationController officerRegController;
    private final EnquiryController enquiryController;
    private final ReportController reportController;
    private final AuthenticationController authController; // For password change

    public HDBManagerUI(HDBManager user,
            ProjectController projCtrl,
            ApplicationController appCtrl,
            OfficerRegistrationController offRegCtrl,
            EnquiryController enqCtrl,
            ReportController reportCtrl,
            AuthenticationController authCtrl,
            Scanner scanner) {
        super(scanner);
        // Basic null checks
        this.user = user;
        this.projectController = projCtrl;
        this.applicationController = appCtrl;
        this.officerRegController = offRegCtrl;
        this.enquiryController = enqCtrl;
        this.reportController = reportCtrl;
        this.authController = authCtrl;
    }

    /**
     * Displays the main menu for HDB Managers and handles user actions.
     */
    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            displayHeader("HDB Manager Menu - " + (user != null ? user.getName() : "User"));

            System.out.println("--- Project Management ---");
            System.out.println("[1] Create New BTO Project");
            System.out.println("[2] Edit Existing BTO Project");
            System.out.println("[3] Delete BTO Project");
            System.out.println("[4] Toggle Project Visibility");
            System.out.println("[5] View All Projects (Filterable)");
            System.out.println("----------------------------------");
            System.out.println("--- Approvals & Reviews ---");
            System.out.println("[6] Review Officer Registrations");
            System.out.println("[7] Review BTO Applications");
            System.out.println("[8] Review Application Withdrawals");
            System.out.println("----------------------------------");
            System.out.println("--- Reporting ---");
            System.out.println("[9] Generate Booking Report");
            System.out.println("----------------------------------");
            System.out.println("--- Enquiries ---");
            System.out.println("[10] View All Enquiries");
            System.out.println("[11] Reply to Enquiry");
            System.out.println("----------------------------------");
            System.out.println("--- Account ---");
            System.out.println("[12] Change Password");
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
                        handleEditProject();
                        break;
                    case 3:
                        handleDeleteProject();
                        break;
                    case 4:
                        handleToggleVisibility();
                        break;
                    case 5:
                        handleViewAllProjects();
                        break;
                    case 6:
                        handleReviewOfficerRegistrations();
                        break;
                    case 7:
                        handleReviewApplications();
                        break;
                    case 8:
                        handleReviewWithdrawals();
                        break;
                    case 9:
                        handleGenerateReport();
                        break;
                    case 10:
                        handleViewAllEnquiries();
                        break;
                    case 11:
                        handleReplyToEnquiry();
                        break;
                    case 12:
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

    // --- Skeleton Handler Methods ---

    private void handleCreateProject() {
        displayHeader("Create New BTO Project");
        System.out.println("Calling projectController.createProject...");
        // TODO: Implement logic:
        // 1. Prompt for Project Name, Neighbourhood, Flat Types (2/3 room) and units,
        // Opening/Closing Dates, Officer Slots.
        // 2. Validate inputs.
        // 3. Create Map<FlatType, ProjectFlatInfo>.
        // 4. Call projectController.createProject(user, name, neighborhood,
        // flatInfoMap, openDate, closeDate, officerSlots);
        // 5. Display success message with Project ID or error message.
        System.out.println("[Placeholder: Create a new project listing]");
    }

    private void handleEditProject() {
        displayHeader("Edit Existing BTO Project");
        System.out.println("Calling projectController.editProject...");
        // TODO: Implement logic:
        // 1. Prompt for Project ID to edit.
        // 2. Fetch project details to show current values (maybe optional).
        // 3. Prompt for new details (Name, Neighbourhood, Dates, Slots - Cannot edit
        // flats/units easily once created?).
        // 4. Validate inputs.
        // 5. Call projectController.editProject(user, projectId, name, neighborhood,
        // openDate, closeDate, officerSlots);
        // 6. Display success or error message.
        System.out.println("[Placeholder: Edit details of an existing project]");
    }

    private void handleDeleteProject() {
        displayHeader("Delete BTO Project");
        System.out.println("Calling projectController.deleteProject...");
        // TODO: Implement logic:
        // 1. Prompt for Project ID to delete.
        // 2. Prompt for confirmation (WARN about consequences).
        // 3. If confirmed, call projectController.deleteProject(user, projectId);
        // 4. Display success or error message. Consider constraints (e.g., cannot
        // delete if applications exist?).
        System.out.println("[Placeholder: Delete a project listing]");
    }

    private void handleToggleVisibility() {
        displayHeader("Toggle Project Visibility");
        System.out.println("Calling projectController.toggleVisibility...");
        // TODO: Implement logic:
        // 1. Prompt for Project ID.
        // 2. Call projectController.findProjectById(projectId) to show current status.
        // 3. Prompt for confirmation to toggle.
        // 4. Call projectController.toggleVisibility(user, projectId);
        // 5. Display success message showing the new visibility status or error
        // message.
        System.out.println("[Placeholder: Change visibility of a project]");
    }

    private void handleViewAllProjects() {
        displayHeader("View All Projects");
        System.out.println("Calling projectController.getAllProjects...");
        // TODO: Implement logic:
        // 1. Ask for optional filters (Neighbourhood, Flat Type, Visibility, Own
        // Projects Only?).
        // 2. Call projectController.getAllProjects(user); // Controller/Service applies
        // filters based on map/params.
        // 3. Display the list of projects matching the filters.
        System.out.println("[Placeholder: List all projects with optional filtering]");
    }

    private void handleReviewOfficerRegistrations() {
        displayHeader("Review Officer Registrations");
        System.out.println("Calling officerRegController.getPendingRegistrations and reviewRegistration...");
        // TODO: Implement logic:
        // 1. Call officerRegController.getPendingRegistrations(user).
        // 2. Display list of pending registrations (Reg ID, Officer NRIC, Project ID,
        // Date).
        // 3. If list not empty, prompt Manager to select a Registration ID.
        // 4. Prompt for Approve/Reject action.
        // 5. Call officerRegController.reviewRegistration(user, registrationId,
        // approveBoolean);
        // 6. Display success or error message.
        System.out.println("[Placeholder: View pending officer registrations and approve/reject]");
    }

    private void handleReviewApplications() {
        displayHeader("Review BTO Applications");
        System.out.println("Calling applicationController.getApplicationsByStatus(PENDING) and reviewApplication...");
        // TODO: Implement logic:
        // 1. Call applicationController.getApplicationsByStatus(user,
        // ApplicationStatus.PENDING).
        // 2. Display list of pending applications (App ID, Applicant NRIC, Project ID,
        // Date).
        // 3. If list not empty, prompt Manager to select an Application ID.
        // 4. Prompt for Approve/Reject action.
        // 5. Call applicationController.reviewApplication(user, applicationId,
        // approveBoolean);
        // 6. Display success or error message (e.g., success, or "no units left").
        System.out.println("[Placeholder: View pending applications and approve/reject]");
    }

    private void handleReviewWithdrawals() {
        displayHeader("Review Application Withdrawals");
        System.out.println(
                "Calling applicationController.getApplicationsByStatus(WITHDRAWAL_REQUESTED) and reviewWithdrawal...");
        // TODO: Implement logic:
        // 1. Call applicationController.getApplicationsByStatus(user,
        // ApplicationStatus.WITHDRAWAL_REQUESTED).
        // 2. Display list of withdrawal requests (App ID, Applicant NRIC, Project ID).
        // 3. If list not empty, prompt Manager to select an Application ID.
        // 4. Prompt for Approve/Reject action.
        // 5. Call applicationController.reviewWithdrawal(user, applicationId,
        // approveBoolean);
        // 6. Display success or error message.
        System.out.println("[Placeholder: View withdrawal requests and approve/reject]");
    }

    private void handleGenerateReport() {
        displayHeader("Generate Applicant Booking Report");
        System.out.println("Calling reportController.generateBookingReport...");
        // TODO: Implement logic:
        // 1. Prompt for optional filters (Project Name, Flat Type, Marital Status, Age
        // Range?).
        // 2. Create a filter map or object.
        // 3. Call reportController.generateBookingReport(user, filters);
        // 4. Display the returned report string (which should be pre-formatted by the
        // service).
        // 5. Handle case where no applicants match filters.
        System.out.println("[Placeholder: Generate and display report based on filters]");
    }

    private void handleViewAllEnquiries() {
        displayHeader("View All Enquiries");
        System.out.println("Calling enquiryController.viewAllEnquiries...");
        // TODO: Implement logic:
        // 1. Call enquiryController.viewAllEnquiries(user).
        // 2. Display list of all enquiries (ID, Submitter, Project, Date, Replied
        // Status, Content Snippet).
        // 3. Handle case where no enquiries exist.
        System.out.println("[Placeholder: List all enquiries in the system]");
    }

    private void handleReplyToEnquiry() {
        displayHeader("Reply to Enquiry");
        System.out.println("Calling enquiryController.replyToEnquiry...");
        // TODO: Implement logic:
        // 1. Prompt for Enquiry ID to reply to.
        // 2. Fetch enquiry details (optional, to show content before replying).
        // 3. Check if already replied.
        // 4. If not replied, prompt for reply content. Validate not empty.
        // 5. Call enquiryController.replyToEnquiry(user, enquiryId, replyContent);
        // 6. Display success or error message.
        System.out.println("[Placeholder: Reply to a specific enquiry by ID]");
    }

    private void handleChangePassword() {
        displayHeader("Change Password");
        System.out.println("Calling authController.changePassword...");
        // TODO: Implement logic: (Same as in HDBOfficerUI)
        // 1. Prompt for new password.
        // 2. Prompt for confirmation.
        // 3. Check if passwords match and are not empty.
        // 4. Call authController.changePassword(user, newPassword);
        // 5. Display success or error message.
        System.out.println("[Placeholder: Change user's password]");
    }

}