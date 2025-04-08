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
import com.ntu.fdae.group1.bto.models.user.User; // Needed for receipt generation
import com.ntu.fdae.group1.bto.exceptions.*; // Import custom exceptions
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class HDBOfficerUI extends BaseUI {
    private final HDBOfficer user;
    private final UserController userController;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final OfficerRegistrationController officerRegController;
    private final BookingController bookingController;
    private final ReceiptController receiptController;
    private final EnquiryController enquiryController;
    private final AuthenticationController authController;
    private final ProjectUIHelper projectUIHelper; // Use the helper
    private final AccountUIHelper accountUIHelper;
    private final EnquiryUIHelper enquiryUIHelper; // Use the helper

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
        this.projectUIHelper = new ProjectUIHelper(this, userController);
        this.accountUIHelper = new AccountUIHelper(this, authController);
        this.enquiryUIHelper = new EnquiryUIHelper(this, userController);
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

    private void handleSubmitApplicationAction(String projectId) {
        // TODO: Implement identical flow as ApplicantUI.handleSubmitApplicationAction
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

    /**
     * Handles the sub-menu for managing the specific project the officer is
     * approved to handle. Finds the project, displays its details, and offers
     * relevant management actions like booking, receipts, and enquiry handling.
     */
    private void handleManageHandlingProject() {
        displayHeader("Manage Project Being Handled");

        // --- Step 1: Find the project the officer is handling ---
        Project handlingProject = null;
        try {
            // Assumes a controller method exists to find the project based on APPROVED
            // registrations
            // This might return null if the officer isn't approved for any.
            handlingProject = officerRegController.findApprovedHandlingProject(this.user); // CRITICAL: Implement this
                                                                                           // controller method
        } catch (Exception e) {
            // Handle potential exceptions during lookup
            displayError("Error finding the project you are handling: " + e.getMessage());
            return; // Cannot proceed
        }

        if (handlingProject == null) {
            displayMessage("You are not currently approved to handle any specific project.");
            displayMessage("Please ensure your registration request has been approved by a manager.");
            return; // Go back to main menu
        }

        // --- Step 2: Display Project Details and Sub-Menu Loop ---
        boolean keepManaging = true;
        while (keepManaging) {
            clearConsole(); // Optional: Clear screen for better sub-menu visibility
            displayMessage("You are managing Project: " + handlingProject.getProjectName() + " ("
                    + handlingProject.getProjectId() + ")");
            displayMessage("--------------------------------------------------");

            // Display full project details for context using the STAFF view helper
            projectUIHelper.displayStaffProjectDetails(handlingProject);

            // Display Contextual Actions Sub-Menu
            System.out.println("\n--- Management Actions for this Project ---");
            System.out.println("[1] Book Flat for Successful Applicant");
            System.out.println("[2] Generate Booking Receipt for Applicant");
            System.out.println("[3] View / Reply Enquiries for this Project");
            System.out.println("-------------------------------------------");
            System.out.println("[0] Back to Main Officer Menu");
            System.out.println("===========================================");

            int choice = promptForInt("Enter action for this project: ");

            try { // Catch exceptions specific to actions within this sub-menu
                switch (choice) {
                    case 1:
                        handlePerformBookingAction(handlingProject);
                        break;
                    case 2:
                        handleGenerateReceiptAction(handlingProject);
                        break;
                    case 3:
                        handleViewAndReplyProjectEnquiriesAction(handlingProject.getProjectId());
                        break;
                    case 0:
                        keepManaging = false;
                        break;
                    default:
                        displayError("Invalid choice.");
                        break;
                }
            } catch (DataAccessException e) {
                // Catch exceptions relevant to booking, receipt, enquiry actions
                displayError("Operation failed: " + e.getMessage());
            } catch (Exception e) {
                displayError("An unexpected error occurred: " + e.getMessage());
                // e.printStackTrace();
            }

            if (keepManaging && choice != 0) {
                pause();
            }
        }
    }

    /**
     * Handles booking by first listing eligible (SUCCESSFUL) applicants for the
     * project.
     * 
     * @param project The project being managed.
     */
    private void handlePerformBookingAction(Project project)
            throws BookingException, InvalidInputException, DataAccessException {
        displayHeader("Book Flat for Applicant (Project: " + project.getProjectId() + ")");

        // 1. Get ALL applications for this project
        List<Application> allProjectApps = applicationController.getProjectApplications(this.user,
                project.getProjectId());

        // 2. Filter for SUCCESSFUL status within the UI method
        List<Application> successfulApps = allProjectApps.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
                .collect(Collectors.toList());

        if (successfulApps.isEmpty()) {
            displayMessage("No applicants with 'SUCCESSFUL' status found for this project.");
            return;
        }

        // 2. Display list of eligible applicants
        displayMessage("--- Applicants Ready for Booking ---");
        AtomicInteger counter = new AtomicInteger(1);
        successfulApps.forEach(app -> {
            // Optional: Fetch applicant name for better display
            String applicantName = userController.getUserName(app.getApplicantNric()); // Use helper
            String preference = (app.getPreferredFlatType() != null) ? "Pref: " + app.getPreferredFlatType()
                    : "Pref: N/A";
            displayMessage(String.format("[%d] AppID: %s | NRIC: %s (%s) | %s",
                    counter.getAndIncrement(),
                    app.getApplicationId(),
                    app.getApplicantNric(),
                    applicantName,
                    preference));
        });
        displayMessage("[0] Cancel Booking Process");
        displayMessage("----------------------------------");

        // 3. Prompt Officer to select an applicant
        int choice = promptForInt("Select applicant number to book for: ");
        if (choice <= 0 || choice > successfulApps.size()) {
            if (choice != 0)
                displayError("Invalid selection.");
            displayMessage("Booking cancelled.");
            return;
        }
        Application selectedApp = successfulApps.get(choice - 1);
        String selectedApplicantNric = selectedApp.getApplicantNric();
        FlatType applicantPreference = selectedApp.getPreferredFlatType();

        // 4. Display available flats for *this* project (refresh data)
        Project currentProject = projectController.findProjectById(project.getProjectId());
        if (currentProject == null)
            throw new DataAccessException("Cannot find project " + project.getProjectId() + " for booking.", null);

        displayMessage("\n--- Available Flats for Project " + project.getProjectId() + " ---");
        boolean flatsAvailable = false;
        for (Map.Entry<FlatType, ProjectFlatInfo> entry : currentProject.getFlatTypes().entrySet()) {
            // Display only if remaining > 0 ? Or show all? Let's show all for clarity.
            displayMessage(String.format("  Type: %s | Remaining: %d",
                    entry.getKey().name(), entry.getValue().getRemainingUnits()));
            if (entry.getValue().getRemainingUnits() > 0)
                flatsAvailable = true;
        }
        if (!flatsAvailable) {
            displayMessage("\nWarning: No flats seem available according to current data!");
            // Maybe still allow trying? Controller should have final check.
        }
        if (applicantPreference != null) {
            displayMessage("Applicant's Preference: " + applicantPreference);
        } else {
            displayMessage("Applicant Preference: Not specified.");
        }
        displayMessage("----------------------------------");

        // 5. Prompt Officer for the Flat Type chosen by the applicant (validated
        // against preference)
        FlatType finalFlatType = promptForEnum("Enter FINAL Flat Type chosen by applicant: ", FlatType.class);

        // 6. Confirmation
        if (!promptForConfirmation(String.format("Confirm booking of %s flat for %s in project %s? (yes/no): ",
                finalFlatType, selectedApplicantNric, project.getProjectId()))) {
            displayMessage("Booking cancelled.");
            return;
        }

        // 7. Call the Booking Controller
        Booking booking = bookingController.createBooking(this.user, selectedApplicantNric, finalFlatType);

        // 8. Display Success
        displayMessage("Booking successful!");
        displayMessage("Booking ID: " + booking.getBookingId());
        displayMessage("Booked Flat Type: " + booking.getBookedFlatType());
        displayMessage("Applicant " + selectedApplicantNric + "'s application status updated to BOOKED.");
    }

    /**
     * Handles receipt generation by first listing completed bookings for the
     * project.
     * 
     * @param project The project being managed.
     */
    private void handleGenerateReceiptAction(Project project) throws DataAccessException, InvalidInputException {
        displayHeader("Generate Booking Receipt (Project: " + project.getProjectId() + ")");

        // 1. Get completed bookings for this project
        // Assumes bookingController has a method like getBookingsForProject
        List<Booking> projectBookings;
        try {
            projectBookings = bookingController.getBookingsForProject(project.getProjectId());
        } catch (BookingException e) {
            displayError("Failed to retrieve bookings for project " + project.getProjectId());
            return; // Cannot proceed
        }

        if (projectBookings.isEmpty()) {
            displayMessage("No completed bookings found for this project.");
            return;
        }

        // 2. Display list of completed bookings
        displayMessage("--- Completed Bookings for Project " + project.getProjectId() + " ---");
        AtomicInteger counter = new AtomicInteger(1);
        projectBookings.forEach(booking -> {
            String applicantName = userController.getUserName(booking.getApplicantNric()); // Use helper
            displayMessage(String.format("[%d] BookingID: %s | NRIC: %s (%s) | Flat: %s | Date: %s",
                    counter.getAndIncrement(),
                    booking.getBookingId(),
                    booking.getApplicantNric(),
                    applicantName,
                    booking.getBookedFlatType(),
                    booking.getBookingDate() // Add formatting
            ));
        });
        displayMessage("[0] Cancel Receipt Generation");
        displayMessage("----------------------------------");

        // 3. Prompt Officer to select a booking
        int choice = promptForInt("Select booking number to generate receipt for: ");
        if (choice <= 0 || choice > projectBookings.size()) {
            if (choice != 0)
                displayError("Invalid selection.");
            displayMessage("Receipt generation cancelled.");
            return;
        }
        Booking selectedBooking = projectBookings.get(choice - 1);

        // 4. Call Receipt Controller
        BookingReceiptInfo receiptInfo = receiptController.getBookingReceiptInfo(this.user, selectedBooking);

        // 5. Display Receipt
        displayMessage("\n--- Booking Receipt ---");
        displayMessage("Booking ID: " + receiptInfo.getBookingId());
        displayMessage("Booking Date: " + receiptInfo.getBookingDate()); // Add formatting
        displayMessage("-----------------------");
        displayMessage("Applicant Name: " + receiptInfo.getApplicantName());
        displayMessage("Applicant NRIC: " + receiptInfo.getApplicantNric());
        displayMessage("Applicant Age: " + receiptInfo.getApplicantAge());
        displayMessage("Marital Status: " + receiptInfo.getApplicantMaritalStatus());
        displayMessage("-----------------------");
        displayMessage("Project Name: " + receiptInfo.getProjectName());
        displayMessage("Neighbourhood: " + receiptInfo.getProjectNeighborhood());
        displayMessage("Booked Flat Type: " + receiptInfo.getBookedFlatType());
        displayMessage("--- End of Receipt ---\n");
    }

    /**
     * Handles viewing enquiries for the project being handled and provides an
     * option
     * to reply if an enquiry is selected and unreplied.
     * Uses EnquiryUIHelper for display tasks.
     * 
     * @param projectId The ID of the project being managed.
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
            // Message ("No enquiries" or "Invalid selection") handled by helper or loop
            // continues
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
                    // Pause to allow user to see error before returning to sub-menu
                    pause();
                    return;
                }

                // Call Enquiry Controller
                try {
                    if (enquiryController.replyToEnquiry(this.user, selectedEnquiry.getEnquiryId(), replyContent)) {
                        displayMessage("Reply sent successfully.");
                        // Optionally: You might want to immediately re-fetch and re-display the detail
                        // view to show the updated status, or just let the user see it next time.
                    } else {
                        displayError("Failed to send reply. Please check or try again.");
                    }
                } catch (Exception e) { // Catch specific exceptions if controller throws them
                    displayError("Error sending reply: " + e.getMessage());
                }
                // Pause after action before potentially re-displaying sub-menu
                pause();
            }
            // If 0 or invalid, do nothing, loop in handleManageHandlingProject will show
            // sub-menu again.

        } else {
            // Already replied, just offer to go back
            System.out.println("\n[0] Back");
            promptForInt("Enter 0 to go back: "); // Just wait for back input
        }
        // After handling the selected enquiry, control returns to the loop in
        // handleManageHandlingProject
    }

    private void handleChangePassword() {
        accountUIHelper.handlePasswordChange(this.user);
    }
}