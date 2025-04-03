package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.controllers.booking.BookingController;
import com.ntu.fdae.group1.bto.controllers.booking.ReceiptController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController; // Added
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.project.*; // Import project models
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.User; // Needed for receipt generation
import com.ntu.fdae.group1.bto.exceptions.*; // Import custom exceptions
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

// Import other necessary Entity/Enum/Exception types as needed

public class HDBOfficerUI extends BaseUI {
    private final HDBOfficer user;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final OfficerRegistrationController officerRegController;
    private final BookingController bookingController;
    private final ReceiptController receiptController;
    private final EnquiryController enquiryController;
    private final AuthenticationController authController; // For password change

    public HDBOfficerUI(HDBOfficer user,
            ProjectController projCtrl,
            ApplicationController appCtrl,
            OfficerRegistrationController offRegCtrl,
            BookingController bookCtrl,
            ReceiptController receiptCtrl,
            EnquiryController enqCtrl,
            AuthenticationController authCtrl,
            Scanner scanner) {
        super(scanner);
        // Basic null checks (can add more robust checks later)
        this.user = user;
        this.projectController = projCtrl;
        this.applicationController = appCtrl;
        this.officerRegController = offRegCtrl;
        this.bookingController = bookCtrl;
        this.receiptController = receiptCtrl;
        this.enquiryController = enqCtrl;
        this.authController = authCtrl;
    }

    /**
     * Displays the main menu for HDB Officers and handles user actions.
     * This is the main loop for this UI until the user logs out.
     */
    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            // 1. Display Header
            displayHeader("HDB Officer Menu - Welcome " + (user != null ? user.getName() : "User")); // Basic header

            // 2. Display Menu Options
            System.out.println("--- Applicant Actions ---");
            System.out.println("1. View Available Projects (For Applying)");
            System.out.println("2. Submit BTO Application (As Applicant)");
            System.out.println("3. View My BTO Application Status");
            System.out.println("4. Request BTO Application Withdrawal");
            System.out.println("--- Officer Registration ---");
            System.out.println("5. Request Project Handling Registration");
            System.out.println("6. View My Project Registration Status");
            System.out.println("--- Project Handling ---");
            System.out.println("7. View Details of Project Handling");
            System.out.println("8. Perform Flat Booking for Applicant");
            System.out.println("9. Generate Booking Receipt for Applicant");
            System.out.println("--- Enquiries ---");
            System.out.println("10. Submit Enquiry");
            System.out.println("11. View/Edit/Delete My Enquiries");
            System.out.println("12. View Enquiries for Project Handling");
            System.out.println("13. Reply to Project Enquiry");
            System.out.println("--- Account ---");
            System.out.println("14. Change Password");
            System.out.println("--- Exit ---");
            System.out.println("0. Logout");
            System.out.println("-----------------------------------------");

            // 3. Get User Choice
            int choice = promptForInt("Enter your choice: ");

            // 4. Handle Choice using Switch
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
                        handleRequestRegistration();
                        break;
                    case 6:
                        handleViewRegistrationStatus();
                        break;
                    case 7:
                        handleViewHandlingProjectDetails();
                        break;
                    case 8:
                        handlePerformBooking();
                        break;
                    case 9:
                        handleGenerateReceipt();
                        break;
                    case 10:
                        handleSubmitEnquiry();
                        break;
                    case 11:
                        handleManageMyEnquiries();
                        break;
                    case 12:
                        handleViewProjectEnquiries();
                        break;
                    case 13:
                        handleReplyToEnquiry();
                        break;
                    case 14:
                        handleChangePassword();
                        break;
                    case 0:
                        keepRunning = false; // Exit the loop for this UI
                        break;
                    default:
                        displayError("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                // Basic catch-all for now. Refine later with specific exceptions.
                displayError("An error occurred: " + e.getMessage());
                // Optionally print stack trace during development: e.printStackTrace();
            }

            // 5. Pause before repeating loop (unless logging out)
            if (keepRunning && choice != 0) {
                pause(); // Assumes pause() exists in BaseUI
            }
        }
        // Loop ends, control returns to MainApp which will handle logout message.
    }

    // --- Skeleton Handler Methods ---
    // These methods will contain the logic to get input, call controllers, and
    // display output.

    private void handleViewAvailableProjects() {
        displayHeader("View Available BTO Projects");
        System.out.println("Calling projectController.getVisibleProjects...");
        // TODO: Implement logic:
        // 1. Call projectController.getVisibleProjects(user);
        // 2. Check if list is empty, display message.
        // 3. If not empty, iterate and display project details using displayList or
        // similar.
        System.out.println("[Placeholder: List available projects]");
    }

    private void handleSubmitApplication() {
        displayHeader("Submit BTO Application");
        System.out.println("Calling applicationController.submitApplication...");
        // TODO: Implement logic:
        // 1. Prompt for Project ID.
        // 2. Validate Project ID format (basic).
        // 3. Prompt for confirmation.
        // 4. If confirmed, call applicationController.submitApplication(user,
        // projectId);
        // 5. Display success message with Application ID/Status or error message.
        System.out.println("[Placeholder: Submit application for a project]");
    }

    private void handleViewMyApplication() {
        displayHeader("View My BTO Application Status");
        System.out.println("Calling applicationController.getMyApplication...");
        // TODO: Implement logic:
        // 1. Call applicationController.getMyApplication(user);
        // 2. If null, display "No application found".
        // 3. If found, display Application ID, Project ID, Submission Date, Status.
        System.out.println("[Placeholder: Display my application details]");
    }

    private void handleRequestWithdrawal() {
        displayHeader("Request BTO Application Withdrawal");
        System.out.println("Calling applicationController.requestWithdrawal...");
        // TODO: Implement logic:
        // 1. Call applicationController.getMyApplication(user) to check if app exists
        // and status.
        // 2. If withdrawal is possible, prompt for confirmation.
        // 3. If confirmed, call applicationController.requestWithdrawal(user);
        // 4. Display success or error message.
        System.out.println("[Placeholder: Request withdrawal for my application]");
    }

    private void handleRequestRegistration() {
        displayHeader("Request Project Handling Registration");
        System.out.println("Calling officerRegController.requestRegistration...");
        // TODO: Implement logic:
        // 1. Prompt for Project ID.
        // 2. Validate Project ID format.
        // 3. Prompt for confirmation.
        // 4. If confirmed, call officerRegController.requestRegistration(user,
        // projectId);
        // 5. Display success message with Registration ID/Status or error message.
        System.out.println("[Placeholder: Request registration for a project]");
    }

    private void handleViewRegistrationStatus() {
        displayHeader("View Project Registration Status");
        System.out.println("Calling officerRegController.getMyRegistrationStatus...");
        // TODO: Implement logic:
        // 1. Prompt for Project ID (or maybe view all?).
        // 2. Call appropriate officerRegController method (e.g.,
        // getMyRegistrationStatus(user, projectId) or getMyRegistrations(user)).
        // 3. Display the status(es) found or "No registration found".
        System.out.println("[Placeholder: Display my registration status for project(s)]");
    }

    private void handleViewHandlingProjectDetails() {
        displayHeader("View Details of Project Handling");
        System.out.println("Finding project officer is handling and calling projectController.findProjectById...");
        // TODO: Implement logic:
        // 1. ***Determine the Project ID the officer is handling*** (CRITICAL logic
        // needed).
        // 2. If found, call projectController.findProjectById(handlingProjectId);
        // 3. If project found, display all details (Name, Dates, Flats, etc.).
        // 4. If not handling a project or project not found, display appropriate
        // message.
        System.out.println("[Placeholder: Display details of the specific project being handled]");
    }

    private void handlePerformBooking() {
        displayHeader("Perform Flat Booking for Applicant");
        System.out.println("Calling bookingController.createBooking...");
        // TODO: Implement logic:
        // 1. Prompt for Applicant NRIC. Validate format.
        // 2. Call applicationController.getApplicationForUser(applicantNric) to check
        // status (must be SUCCESSFUL).
        // 3. Get Project ID from the application.
        // 4. Call projectController.findProjectById(projectId) to get flat info.
        // Display available flats.
        // 5. Prompt officer to select Flat Type. Validate choice against available
        // types/units.
        // 6. Prompt for confirmation.
        // 7. If confirmed, call bookingController.createBooking(user, applicantNric,
        // selectedFlatType);
        // 8. Display success message with Booking ID/details or error message.
        System.out.println("[Placeholder: Perform booking for an eligible applicant]");
    }

    private void handleGenerateReceipt() {
        displayHeader("Generate Booking Receipt");
        System.out.println("Calling receiptController.getBookingReceiptInfo...");
        // TODO: Implement logic:
        // 1. Prompt for Applicant NRIC.
        // 2. ***Find the relevant Booking object*** (e.g., via applicant
        // NRIC/Application ID - needs controller/service support).
        // 3. If booking found, call receiptController.getBookingReceiptInfo(user,
        // booking);
        // 4. Display the formatted receipt details from the returned BookingReceiptInfo
        // object.
        // 5. Handle cases where booking is not found.
        System.out.println("[Placeholder: Generate and display booking receipt]");
    }

    private void handleSubmitEnquiry() {
        displayHeader("Submit Enquiry");
        System.out.println("Calling enquiryController.createEnquiry...");
        // TODO: Implement logic:
        // 1. Prompt if project-specific. Get Project ID if yes.
        // 2. Prompt for enquiry content. Validate not empty.
        // 3. Call enquiryController.createEnquiry(user, projectId, content);
        // 4. Display success message with Enquiry ID.
        System.out.println("[Placeholder: Submit a general or project-specific enquiry]");
    }

    private void handleManageMyEnquiries() {
        displayHeader("Manage My Enquiries");
        System.out.println("Calling enquiryController.viewMyEnquiries and potential edit/delete...");
        // TODO: Implement logic:
        // 1. Call enquiryController.viewMyEnquiries(user).
        // 2. Display the list of enquiries.
        // 3. Prompt user to select an Enquiry ID for action (view details, edit,
        // delete).
        // 4. If selected:
        // a. Display full details.
        // b. If eligible (e.g., not replied), offer Edit/Delete options.
        // c. Get new content for edit or confirmation for delete.
        // d. Call enquiryController.editMyEnquiry(...) or deleteMyEnquiry(...).
        // e. Display success/error messages.
        System.out.println("[Placeholder: View, Edit, Delete own enquiries]");
    }

    private void handleViewProjectEnquiries() {
        displayHeader("View Enquiries for Project Handling");
        System.out.println("Finding handling project and calling enquiryController.viewProjectEnquiries...");
        // TODO: Implement logic:
        // 1. ***Determine the Project ID the officer is handling***.
        // 2. If found, call enquiryController.viewProjectEnquiries(user,
        // handlingProjectId);
        // 3. Display list of enquiries for that project (showing submitter NRIC, date,
        // content snippet, replied status).
        // 4. Handle case where officer isn't handling a project or no enquiries exist.
        System.out.println("[Placeholder: View enquiries related to the handled project]");
    }

    private void handleReplyToEnquiry() {
        displayHeader("Reply to Project Enquiry");
        System.out.println(
                "Finding handling project, unreplied enquiries, and calling enquiryController.replyToEnquiry...");
        // TODO: Implement logic:
        // 1. ***Determine the Project ID the officer is handling***.
        // 2. If found, call enquiryController.viewProjectEnquiries(...) and filter for
        // unreplied ones.
        // 3. Display the list of unreplied enquiries.
        // 4. Prompt officer to select an Enquiry ID to reply to. Validate selection.
        // 5. Prompt for reply content. Validate not empty.
        // 6. Call enquiryController.replyToEnquiry(user, enquiryId, replyContent);
        // 7. Display success or error message.
        System.out.println("[Placeholder: Reply to an enquiry for the handled project]");
    }

    private void handleChangePassword() {
        displayHeader("Change Password");
        System.out.println("Calling authController.changePassword...");
        // TODO: Implement logic:
        // 1. Prompt for new password.
        // 2. Prompt for confirmation.
        // 3. Check if passwords match and are not empty.
        // 4. Call authController.changePassword(user, newPassword);
        // 5. Display success or error message.
        System.out.println("[Placeholder: Change user's password]");
    }

}