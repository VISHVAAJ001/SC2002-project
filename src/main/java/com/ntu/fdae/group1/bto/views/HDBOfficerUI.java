package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.user.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.controllers.booking.BookingController;
import com.ntu.fdae.group1.bto.controllers.booking.ReceiptController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController; // Added
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.project.*; // Import project models
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.User; // Needed for receipt generation
import com.ntu.fdae.group1.bto.exceptions.*; // Import custom exceptions
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HDBOfficerUI extends BaseUI {
    private final HDBOfficer user;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final OfficerRegistrationController officerRegController;
    private final BookingController bookingController;
    private final ReceiptController receiptController;
    private final EnquiryController enquiryController;
    private final AuthenticationController authController;
    private final ProjectUIHelper projectUIHelper; // Use the helper
    private AccountUIHelper accountUIHelper;

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
        this.user = Objects.requireNonNull(user);
        this.projectController = Objects.requireNonNull(projCtrl);
        this.applicationController = Objects.requireNonNull(appCtrl);
        this.officerRegController = Objects.requireNonNull(offRegCtrl);
        this.bookingController = Objects.requireNonNull(bookCtrl);
        this.receiptController = Objects.requireNonNull(receiptCtrl);
        this.enquiryController = Objects.requireNonNull(enqCtrl);
        this.authController = Objects.requireNonNull(authCtrl);
        this.projectUIHelper = new ProjectUIHelper(this); // Initialize helper
        this.accountUIHelper = new AccountUIHelper(this, authController); // Initialize account helper
    }

    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            displayHeader("HDB Officer Menu - Welcome " + (user != null ? user.getName() : "User"));

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
                        handleChangePassword();
                        keepRunning = false; // Change password, then exit
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

    // --- Handler Methods ---

    // Duplicated/Similar Applicant Methods (Could potentially be shared via a
    // helper if identical)
    private void handleViewAndApplyProjects() {
        // TODO: Implement identical flow as ApplicantUI.handleViewAndApplyProjects
        System.out.println("[Placeholder: Officer viewing/applying like Applicant]");
        // Remember service layer needs to check Officer eligibility (e.g., not handling
        // project applying for)
        displayHeader("View Available BTO Projects");
        List<Project> projects = projectController.getVisibleProjectsForUser(this.user);

        Project selectedProject = projectUIHelper.selectProjectFromList(projects,
                "Select Project to View Details & Apply");

        if (selectedProject != null) {
            projectUIHelper.displayApplicantProjectDetails(selectedProject); // Show applicant view

            System.out.println("\nOptions:");
            System.out.println("[1] Apply for " + selectedProject.getProjectName());
            System.out.println("[0] Back");
            int actionChoice = promptForInt("Enter option: ");
            if (actionChoice == 1) {
                handleSubmitApplicationAction(selectedProject.getProjectId());
            }
        }
    }

    private void handleSubmitApplicationAction(Project projectToApply) {
        // TODO: Implement identical flow as ApplicantUI.handleSubmitApplicationAction

        Objects.requireNonNull(projectToApply, "Project to apply for cannot be null.");
        String projectId = projectToApply.getProjectId();

        // CRITICAL OFFICER CHECK: Cannot apply if handling this project
        OfficerRegStatus handlingStatus = officerRegController.getMyRegistrationStatus(this.user, projectId);
        if (handlingStatus == OfficerRegStatus.APPROVED) {
            throw new ApplicationException("You cannot apply for a project you are approved to handle.");
        }
        // Check PENDING status too
        if (handlingStatus == OfficerRegStatus.PENDING) {
            throw new ApplicationException("You cannot apply for a project you have a pending registration for.");
        }

        /* 
        cannot directly pass an HDBOfficer where an Applicant is required
        controller method getMyApplication is designed for an Applicant viewing their own application
        modify to take in nric instead? 


        // Check if already has an application
        if (applicationController.getMyApplication(this.user) != null) {
            throw new ApplicationException("You already have an existing BTO application.");
        }
        
        Application myApp = applicationController.getApplicationForUser(this.user.getNric());

        */


        displayMessage("Applying for project: " + projectToApply.getProjectName());

        // Determine applicable flat types based on user and project
        Map<FlatType, ProjectFlatInfo> availableFlats = projectToApply.getFlatTypes();
        List<FlatType> eligibleTypes = new ArrayList<>();
        if (availableFlats != null) { // Check if map exists
            if (user.getMaritalStatus() == MaritalStatus.SINGLE && user.getAge() >= 35) {
                if (availableFlats.containsKey(FlatType.TWO_ROOM)) eligibleTypes.add(FlatType.TWO_ROOM);
            } else if (user.getMaritalStatus() == MaritalStatus.MARRIED && user.getAge() >= 21) {
                // Check both types explicitly
                if (availableFlats.containsKey(FlatType.TWO_ROOM)) eligibleTypes.add(FlatType.TWO_ROOM);
                if (availableFlats.containsKey(FlatType.THREE_ROOM)) eligibleTypes.add(FlatType.THREE_ROOM);
            }
        }

        if (eligibleTypes.isEmpty()) {
            throw new ApplicationException("No eligible flat types available for you in this project based on your profile.");
        }


        FlatType preferredType = null;
        if (eligibleTypes.size() > 1) { 
            boolean validInput = false;
            while(!validInput) {
                FlatType tempSelection = promptForEnum("Select preferred flat type", FlatType.class); // Use BaseUI method

                if (tempSelection == null) {
                    if (!promptForConfirmation("Invalid type entered. Try again? (Y/N)")) {
                        displayMessage("Application cancelled."); return;
                    }
                    continue;
                }
                if (eligibleTypes.contains(tempSelection)) {
                    preferredType = tempSelection;
                    validInput = true;
                } else {
                    displayError("Selected flat type '" + tempSelection + "' is not eligible/available for you in this project.");
                     if (!promptForConfirmation("Try again? (Y/N)")) {
                        displayMessage("Application cancelled."); return;
                    }
                }
            }
        } else { // Auto-select if only one option (size must be 1)
            preferredType = eligibleTypes.get(0);
            displayMessage("Auto-selecting eligible flat type: " + preferredType);
        }
        // preferredType should now be valid and non-null if we reached here

        if (promptForConfirmation("Confirm application for " + projectToApply.getProjectName() + " (Preference: " + preferredType + ")? (Y/N)")) {

        if (promptForConfirmation("Confirm application for " + projectToApply.getProjectName() + " (Preference: " + preferredType + ")? (Y/N)")) {
            Application submittedApp = applicationController.submitApplication(this.user, projectId, preferredType);
            // Service layer should handle throwing exception on failure
            displayMessage("Application submitted successfully! Application ID: " + submittedApp.getApplicationId());
        } else {
            displayMessage("Application cancelled.");
        }

        System.out.println("[Placeholder: Officer submitting application like Applicant]");
        // Prompt for preference etc. Call appController.submitApplication(...)
    }

    private void handleViewAndWithdrawApplication() {
        // TODO: Implement identical flow as
        // ApplicantUI.handleViewAndWithdrawApplication
        System.out.println("[Placeholder: Officer viewing/withdrawing own application like Applicant]");
    }

    private void handleSubmitEnquiry() {
        // TODO: Implement identical flow as ApplicantUI.handleSubmitEnquiry
        System.out.println("[Placeholder: Officer submitting own enquiry like Applicant]");
    }

    private void handleManageMyEnquiries() {
        // TODO: Implement identical flow as ApplicantUI.handleManageMyEnquiries
        System.out.println("[Placeholder: Officer managing own enquiries like Applicant]");
    }
    // ----------------------------------------------------------------------------

    private void handleRequestRegistration() {
        displayHeader("Register for Project Handling");
        // TODO: Prompt for Project ID, Confirm, Call
        // officerRegController.requestRegistration(...)
        System.out.println("[Placeholder: Request registration for project]");
    }

    private void handleViewRegistrationStatus() {
        displayHeader("View My Project Registration Status");
        // TODO: Prompt for Project ID (or show all?), Call officerRegController
        // method(s), Display status(es)
        System.out.println("[Placeholder: Display my registration status for project(s)]");
    }

    private void handleManageHandlingProject() {
        displayHeader("Manage Project Being Handled");
        // TODO: Determine which project the officer is *currently approved* to handle.
        // This is CRITICAL logic. Needs service/controller support.
        String handlingProjectId = null; // = findHandlingProjectIdForOfficer(this.user); // Needs implementation

        if (handlingProjectId == null) {
            displayMessage("You are not currently approved to handle a specific project.");
            return;
        }

        Project handlingProject = projectController.findProjectById(handlingProjectId);
        if (handlingProject == null) {
            displayError("Could not retrieve details for the project you are handling (ID: " + handlingProjectId + ")");
            return;
        }

        // Display project details (using the staff view)
        projectUIHelper.displayStaffProjectDetails(handlingProject);

        // --- Contextual Actions for Handling Officer ---
        System.out.println("\nActions for Project " + handlingProject.getProjectName() + ":");
        System.out.println("[1] Book Flat for Successful Applicant");
        System.out.println("[2] Generate Booking Receipt for Applicant");
        System.out.println("[3] View Enquiries for this Project");
        System.out.println("[4] Reply to Enquiry for this Project");
        System.out.println("[0] Back to Main Menu");

        int actionChoice = promptForInt("Enter option: ");
        switch (actionChoice) {
            case 1:
                handlePerformBookingAction(handlingProjectId);
                break;
            case 2:
                handleGenerateReceiptAction(handlingProjectId);
                break; // May need applicant NRIC first
            case 3:
                handleViewProjectEnquiriesAction(handlingProjectId);
                break;
            case 4:
                handleReplyToProjectEnquiryAction(handlingProjectId);
                break;
            // Default or 0: Do nothing
        }
    }

    // Extracted Action Logic Helpers for Handling Project
    private void handlePerformBookingAction(String projectId) {
        System.out.println("Initiating booking for Project ID: " + projectId);
        // TODO: Implement booking logic:
        // 1. Prompt for Applicant NRIC.
        // 2. Verify application is SUCCESSFUL via applicationController.
        // 3. Display available flats for projectId.
        // 4. **Prompt Officer for the FlatType chosen by applicant.**
        // 5. Confirm. Call bookingController.createBooking(...)
        // 6. Display result.
        System.out.println("[Placeholder: Book flat action]");
    }

    private void handleGenerateReceiptAction(String projectId) {
        System.out.println("Generating receipt for Project ID: " + projectId);
        // TODO: Implement receipt generation:
        // 1. Prompt for Applicant NRIC (or maybe Booking ID?).
        // 2. Find the corresponding Booking record.
        // 3. Call receiptController.getBookingReceiptInfo(...)
        // 4. Display formatted receipt.
        System.out.println("[Placeholder: Generate receipt action]");
    }

    private void handleViewProjectEnquiriesAction(String projectId) {
        System.out.println("Viewing enquiries for Project ID: " + projectId);
        // TODO: Implement enquiry viewing:
        // 1. Call enquiryController.viewProjectEnquiries(user, projectId).
        // 2. Display list.
        System.out.println("[Placeholder: View project enquiries action]");
    }

    private void handleReplyToProjectEnquiryAction(String projectId) {
        System.out.println("Replying to enquiry for Project ID: " + projectId);
        // TODO: Implement reply logic:
        // 1. Optionally list unreplied enquiries for this project first.
        // 2. Prompt for Enquiry ID to reply to. Validate it belongs to this project.
        // 3. Prompt for reply content.
        // 4. Call enquiryController.replyToEnquiry(...)
        // 5. Display result.
        System.out.println("[Placeholder: Reply to project enquiry action]");
    }
    // ------------------------------------------------

    private void handleChangePassword() {
        accountUIHelper.handlePasswordChange(this.user);
    }
}