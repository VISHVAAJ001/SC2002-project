package com.ntu.fdae.group1.bto;

// Essential Imports
import java.util.Scanner;

// Models (Specific User types needed for casting)
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.models.user.Applicant;
// import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
// import com.ntu.fdae.group1.bto.models.user.HDBManager;

// Views
import com.ntu.fdae.group1.bto.views.LoginUI; // Need LoginUI specifically
import com.ntu.fdae.group1.bto.views.ApplicantUI;
// import com.ntu.fdae.group1.bto.views.HDBOfficerUI;
// import com.ntu.fdae.group1.bto.views.HDBManagerUI;

// Controllers (All controllers needed by different UIs)
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
// import com.ntu.fdae.group1.bto.controllers.user.UserController; // If exists and needed globally
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
// import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
// import com.ntu.fdae.group1.bto.controllers.booking.BookingController;
// import com.ntu.fdae.group1.bto.controllers.booking.ReceiptController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;

// Services (Interfaces and Concrete implementations needed for initialization)
import com.ntu.fdae.group1.bto.services.user.*;
// import com.ntu.fdae.group1.bto.services.project.*;
// import com.ntu.fdae.group1.bto.services.booking.*;
// import com.ntu.fdae.group1.bto.services.enquiry.*;

// Repositories (Interfaces and Concrete implementations needed for initialization)
import com.ntu.fdae.group1.bto.repository.user.*;
// import com.ntu.fdae.group1.bto.repository.project.*;
// import com.ntu.fdae.group1.bto.repository.booking.*;
// import com.ntu.fdae.group1.bto.repository.enquiry.*;

// Exceptions
// import com.ntu.fdae.group1.bto.exceptions.DataAccessException;

/**
 * Main entry point for the BTO Management System application.
 * Handles initialization, the main application loop, and routing to user
 * interfaces.
 */
public class App {

    private Scanner scanner;
    private User currentUser; // Holds the currently logged-in user

    // --- Controllers ---
    // These are needed to be passed to the various UI classes
    private AuthenticationController authController;
    // private UserController userController; // If needed
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;

    // --- UI Components ---
    private LoginUI loginUI;

    /**
     * Constructor: Initializes the scanner and sets up application components.
     */
    public App() {
        this.scanner = new Scanner(System.in);
        initializeComponents(); // Setup dependencies
    }

    /**
     * Initializes all necessary components like repositories, services, and
     * controllers.
     * Performs dependency injection manually.
     */
    private void initializeComponents() {
        System.out.println("Initializing BTO Management System...");
        try {
            // --- 1. Repositories ---
            // Create concrete repository instances (using File-based implementation)
            IUserRepository userRepository = new FileUserRepository();

            // --- 2. Load Initial Data ---
            // Crucial to load data before services might need it
            System.out.println("Loading data...");
            userRepository.loadAll(); // Assumes loadAll populates internal state
            System.out.println("Data loaded successfully.");

            // --- 3. Services ---
            // Instantiate services, injecting repositories (dependencies)
            // User Domain
            AuthenticationService authService = new AuthenticationService(userRepository);
            // UserService userService = new UserService(userRepository);

            // Project Domain
            // ProjectService projectService = new ProjectService(projectRepository);

            // --- 4. Controllers ---
            // Instantiate controllers, injecting services
            this.authController = new AuthenticationController(authService);
            // this.userController = new UserController(userService); // If exists
            // this.projectController = new ProjectController(projectService);

            // --- 5. Core UI Components ---
            // Create the initial Login UI
            this.loginUI = new LoginUI(this.authController);

            System.out.println("Initialization complete.");
        } catch (Exception e) {
            // Catch any other unexpected initialization errors
            System.err.println("FATAL ERROR: An unexpected error occurred during initialization.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Starts and runs the main application loop.
     */
    public void run() {
        System.out.println("\nWelcome to the BTO Management System!");
        boolean running = true;

        while (running) {
            if (currentUser == null) {
                // If no user is logged in, display the login screen
                currentUser = loginUI.displayLogin(); // This method handles the login logic via AuthController

                if (currentUser == null) {
                    // If displayLogin returns null, it typically means the user chose to exit.
                    System.out.println("Exiting application.");
                    running = false; // Set flag to break the loop
                }
            } else {
                // If a user is logged in, route them to their specific UI
                routeToRoleUI(currentUser);

                // When the role-specific UI returns, it means the user logged out.
                System.out.println("\nYou have been logged out.");
                currentUser = null; // Reset currentUser to force login screen next iteration
            }
        }

        // Cleanup before exiting
        scanner.close();
        System.out.println("Thank you for using the BTO Management System!");
        // Maybe can add a saveAll() call here if repositories don't save automatically
        // e.g., userRepository.saveAll(); projectRepository.saveAll(); etc.
        // However, saving on modification within services is generally safer.
    }

    private void routeToRoleUI(User user) {
        System.out.printf("\nWelcome, %s (%s)! Routing to your dashboard...\n", user.getName(), user.getRole());

        // Ensure dependencies are passed correctly to each UI constructor
        try {
            switch (user.getRole()) {
                case APPLICANT:
                    // Applicant UI needs controllers relevant to applicant actions
                    ApplicantUI applicantUI = new ApplicantUI(
                            (Applicant) user, // Cast user to Applicant
                            projectController,
                            applicationController,
                            enquiryController);
                    applicantUI.displayMainMenu(); // This method runs until the user logs out
                    break;

                // case HDB_OFFICER:
                // // HDB Officer UI needs a broader set of controllers, pass whatever is needed
                // HDBOfficerUI officerUI = new HDBOfficerUI(
                // (HDBOfficer) user, // Cast user to HDBOfficer
                // projectController,
                // // applicationController, // To view application details before booking
                // officerRegController, // To manage their registrations
                // // bookingController, // To perform bookings
                // // receiptController, // To generate receipts
                // enquiryController, // To handle enquiries
                // );
                // officerUI.displayMainMenu();
                // break;

                // case HDB_MANAGER:
                // // HDB Manager UI needs controllers for management tasks
                // HDBManagerUI managerUI = new HDBManagerUI(
                // (HDBManager) user, // Cast user to HDBManager
                // projectController, // Manage projects
                // applicationController, // Approve/reject applications/withdrawals
                // officerRegController, // Approve/reject registrations
                // enquiryController, // View/reply to all enquiries
                // // reportController, // If reporting is implemented
                // scanner);
                // managerUI.displayMainMenu();
                // break;

                default:
                    // Should not happen with proper enum usage, but handle defensively
                    System.err.println("Error: Unknown user role encountered: " + user.getRole());
                    System.out.println("Logging out due to role error.");
                    // currentUser will be set to null in the main loop, prompting login again
                    break;
            }
        } catch (ClassCastException cce) {
            System.err.println("Error: Role mismatch during UI routing. Logging out.");
            cce.printStackTrace(); // Helps debugging if this happens
            // currentUser will be set to null in the main loop
        } catch (Exception e) {
            System.err.println("An unexpected error occurred in the user interface. Logging out.");
            e.printStackTrace();
            // currentUser will be set to null in the main loop
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}