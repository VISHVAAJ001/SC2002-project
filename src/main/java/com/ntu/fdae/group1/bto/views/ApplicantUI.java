package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController; // Added
import com.ntu.fdae.group1.bto.enums.FlatType;
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

public class ApplicantUI extends BaseUI {
    private final Applicant user;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiryController enquiryController;
    private final AuthenticationController authController; // For password change

    public ApplicantUI(Applicant user,
            ProjectController projCtrl,
            ApplicationController appCtrl,
            EnquiryController enqCtrl,
            AuthenticationController authCtrl,
            Scanner scanner) {
        super(scanner);
        // Basic null checks
        this.user = user;
        this.projectController = projCtrl;
        this.applicationController = appCtrl;
        this.enquiryController = enqCtrl;
        this.authController = authCtrl;
    }

    /**
     * Displays the main menu for Applicants and handles user actions.
     */
    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            displayHeader("Applicant Menu - Welcome " + (user != null ? user.getName() : "User"));

            System.out.println("--- BTO Application ---");
            System.out.println("1. View Available Projects");
            System.out.println("2. Submit BTO Application");
            System.out.println("3. View My Application Status");
            System.out.println("4. Request Application Withdrawal");
            System.out.println("--- Enquiries ---");
            System.out.println("5. Submit Enquiry");
            System.out.println("6. View/Edit/Delete My Enquiries");
            System.out.println("--- Account ---");
            System.out.println("7. Change Password");
            System.out.println("--- Exit ---");
            System.out.println("0. Logout");
            System.out.println("-----------------------------------------");

            int choice = promptForInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        handleViewAvailableProjects();
                        break;
                    case 2:
                        handleSubmitApplication();
                        break;
                    case 3:
                        handleViewMyApplication();
                        break;
                    case 4:
                        handleRequestWithdrawal();
                        break;
                    case 5:
                        handleSubmitEnquiry();
                        break;
                    case 6:
                        handleManageMyEnquiries();
                        break;
                    case 7:
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

    private void handleViewAvailableProjects() {
        displayHeader("View Available BTO Projects");
        System.out.println("Calling projectController.getVisibleProjects...");
        // TODO: Implement logic: (Same as in HDBOfficerUI applicant part)
        // 1. Call projectController.getVisibleProjects(user);
        // 2. Check if list is empty.
        // 3. Display filtered list based on user eligibility (handled by
        // service/controller ideally).
        /*
         * -----------------------------------------
         * Available BTO Projects
         * -----------------------------------------
         * [1] Acacia Breeze (Yishun) - Apply by: 2025-03-20
         * [2] Maple Grove (Boon Lay) - Apply by: 2025-04-10
         * ...
         * [0] Back
         * 
         * Enter project number to view details or apply, or 0 to go back: 1
         */
        System.out.println("[Placeholder: List projects available to this applicant]");
    }

    private void handleSubmitApplication() {
        displayHeader("Submit BTO Application");
        System.out.println("Calling applicationController.submitApplication...");
        // TODO: Implement logic: (Same as in HDBOfficerUI applicant part)
        // 1. Prompt for Project ID. Validate.
        // 2. Check eligibility (e.g., age, marital status for flat type - maybe done by
        // service).
        // 3. Check if already has an active application (done by service).
        // 4. Confirm. Call applicationController.submitApplication(user, projectId);
        // 5. Display result.
        System.out.println("[Placeholder: Submit application for a project]");
    }

    private void handleViewMyApplication() {
        displayHeader("View My BTO Application Status");
        System.out.println("Calling applicationController.getMyApplication...");
        // TODO: Implement logic: (Same as in HDBOfficerUI applicant part)
        // 1. Call applicationController.getMyApplication(user);
        // 2. Display details if found, including status.
        // 3. Handle case where no application exists.
        System.out.println("[Placeholder: Display my application details]");
    }

    private void handleRequestWithdrawal() {
        displayHeader("Request Application Withdrawal");
        System.out.println("Calling applicationController.requestWithdrawal...");
        // TODO: Implement logic: (Same as in HDBOfficerUI applicant part)
        // 1. Check application status via getMyApplication.
        // 2. Confirm. Call applicationController.requestWithdrawal(user);
        // 3. Display result.
        System.out.println("[Placeholder: Request withdrawal for my application]");
    }

    private void handleSubmitEnquiry() {
        displayHeader("Submit Enquiry");
        System.out.println("Calling enquiryController.createEnquiry...");
        // TODO: Implement logic: (Same as in HDBOfficerUI applicant part)
        // 1. Prompt if project-specific, get Project ID.
        // 2. Prompt for content.
        // 3. Call enquiryController.createEnquiry(user, projectId, content);
        // 4. Display result.
        System.out.println("[Placeholder: Submit a general or project-specific enquiry]");
    }

    private void handleManageMyEnquiries() {
        displayHeader("Manage My Enquiries");
        System.out.println("Calling enquiryController.viewMyEnquiries and potential edit/delete...");
        // TODO: Implement logic: (Same as in HDBOfficerUI applicant part)
        // 1. Call enquiryController.viewMyEnquiries(user).
        // 2. Display list. Prompt for ID to manage.
        // 3. Display details. Offer Edit/Delete if applicable.
        // 4. Call controller for edit/delete action. Display result.
        System.out.println("[Placeholder: View, Edit, Delete own enquiries]");
    }

    private void handleChangePassword() {
        displayHeader("Change Password");
        System.out.println("Calling authController.changePassword...");
        // TODO: Implement logic: (Same as in HDBOfficerUI / HDBManagerUI)
        // 1. Prompt for new password and confirmation.
        // 2. Validate. Call authController.changePassword(user, newPassword);
        // 3. Display result.
        System.out.println("[Placeholder: Change user's password]");
    }

}