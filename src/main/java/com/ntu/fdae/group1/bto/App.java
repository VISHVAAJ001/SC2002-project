package com.ntu.fdae.group1.bto;

// Essential Imports
import java.util.Scanner;

// Models (Specific User types needed for casting)
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBManager;

// Views
import com.ntu.fdae.group1.bto.views.LoginUI; // Need LoginUI specifically
import com.ntu.fdae.group1.bto.views.ApplicantUI;
import com.ntu.fdae.group1.bto.views.HDBOfficerUI;
import com.ntu.fdae.group1.bto.views.HDBManagerUI;

// Controllers (All controllers needed by different UIs)
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
// import com.ntu.fdae.group1.bto.controllers.user.UserController; // If exists and needed globally
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.ControllerContainer;
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
    private final ControllerContainer controllerContainer;
    private final Scanner scanner;
    private final LoginUI loginUI;
    private User currentUser = null;

    /**
     * Constructor: Initializes the scanner and sets up application components.
     */
    public App(ControllerContainer controllerContainer, Scanner scanner) {
        this.controllerContainer = controllerContainer;
        this.scanner = scanner;
        // Create LoginUI once, as it's used repeatedly
        this.loginUI = new LoginUI(controllerContainer.authController, scanner);
    }

    /**
     * Initializes all application components (Repositories, Services, Controllers).
     * Handles potential critical errors during initialization.
     *
     * @return A configured ControllerContainer or null if initialization fails.
     */
    private static ControllerContainer initializeComponents() {
        try {
            // 1. Initialize Repositories
            // Using concrete implementations directly here for setup
            IUserRepository userRepository = new UserRepository();
            // IProjectRepository projectRepository = new ProjectRepository();
            // IApplicationRepository applicationRepository = new
            // ApplicationRepository();
            // IBookingRepository bookingRepository = new BookingRepository();
            // IEnquiryRepository enquiryRepository = new EnquiryRepository();
            // IOfficerRegistrationRepository officerRegRepository = new
            // OfficerRegistrationRepository();

            System.out.println("Repositories initialized.");
            // --- Load initial data ---
            // Explicitly load after creation or handle exceptions if load is in constructor
            // Example (assuming loadAll needs to be called explicitly):
            try {
                userRepository.loadAll();
                // projectRepository.loadAll();
                // applicationRepository.loadAll();
                // bookingRepository.loadAll();
                // enquiryRepository.loadAll();
                // officerRegRepository.loadAll();
                System.out.println("Data loaded successfully.");
            } catch (DataAccessException e) {
                System.err.println("FATAL: Failed to load initial data: " + e.getMessage());
                // Optionally: Create default files or handle differently
                // For now, we might continue with empty repositories or exit
                System.err.println("Continuing with potentially empty data stores.");
            }

            // 2. Initialize Services (Inject Repositories and other Services)
            // EligibilityService eligibilityService = new EligibilityService(); //
            // Standalone or simple dependencies
            AuthenticationService authService = new AuthenticationService(userRepository);
            // ProjectService projectService = new ProjectService(projectRepository,
            // userRepository);
            // ApplicationService applicationService = new ApplicationService(
            // applicationRepository, projectRepository, eligibilityService, userRepository,
            // bookingRepository);
            // OfficerRegistrationService officerRegService = new
            // OfficerRegistrationService(
            // officerRegRepository, projectRepository, applicationRepository,
            // eligibilityService);
            // BookingService bookingService = new BookingService(
            // applicationRepository, projectRepository, bookingRepository, userRepository);
            // ReceiptService receiptService = new ReceiptService(
            // bookingRepository, userRepository, projectRepository);
            // EnquiryService enquiryService = new EnquiryService(enquiryRepository);
            // ReportService reportService = new ReportService(
            // applicationRepository, bookingRepository, projectRepository, userRepository);
            System.out.println("Services initialized.");

            // 3. Initialize Controllers (Inject Services)
            AuthenticationController authController = new AuthenticationController(authService);
            // ProjectController projectController = new ProjectController(projectService);
            // ApplicationController appController = new
            // ApplicationController(applicationService);
            // OfficerRegistrationController officerRegController = new
            // OfficerRegistrationController(officerRegService);
            // BookingController bookingController = new BookingController(bookingService);
            // ReceiptController receiptController = new ReceiptController(receiptService);
            // EnquiryController enquiryController = new EnquiryController(enquiryService);
            // ReportController reportController = new ReportController(reportService);
            System.out.println("Controllers initialized.");

            // 4. Create Controller Container
            // ControllerContainer container = new ControllerContainer(
            // authController, projectController, appController, officerRegController,
            // bookingController, receiptController, enquiryController, reportController);
            ControllerContainer container = new ControllerContainer(
                    authController, null, null, null,
                    null, null, null, null);
            System.out.println("Initialization complete.");
            return container;

        } catch (Exception e) { // Catch broader exceptions during setup
            System.err.println("FATAL: Application initialization failed: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging during development
            return null; // Indicate failure
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

        try {
            switch (user.getRole()) {
                case APPLICANT:
                    // Applicant UI needs controllers relevant to applicant actions
                    ApplicantUI applicantUI = new ApplicantUI(
                            (Applicant) user,
                            controllerContainer.projectController,
                            controllerContainer.appController,
                            controllerContainer.enquiryController,
                            controllerContainer.authController, scanner);
                    applicantUI.displayMainMenu();// This method runs until the user logs out
                    break;

                case HDB_OFFICER:
                    // HDB Officer UI needs a broader set of controllers, pass whatever is needed
                    HDBOfficerUI officerUI = new HDBOfficerUI(
                            (HDBOfficer) user, // Cast user to HDBOfficer
                            controllerContainer.projectController,
                            controllerContainer.appController,
                            controllerContainer.officerRegController,
                            controllerContainer.bookingController,
                            controllerContainer.receiptController,
                            controllerContainer.enquiryController,
                            controllerContainer.authController,
                            scanner // Pass the scanner for user input
                    );
                    officerUI.displayMainMenu();
                    break;

                case HDB_MANAGER:
                    // HDB Manager UI needs controllers for management tasks
                    HDBManagerUI managerUI = new HDBManagerUI(
                            (HDBManager) user,
                            controllerContainer.projectController,
                            controllerContainer.appController,
                            controllerContainer.officerRegController,
                            controllerContainer.enquiryController,
                            controllerContainer.reportController,
                            controllerContainer.authController,
                            scanner);
                    managerUI.displayMainMenu();
                    break;

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
        ControllerContainer controllers = initializeComponents();
        Scanner scanner = new Scanner(System.in);

        App app = new App(controllers, scanner);
        app.run();
    }
}