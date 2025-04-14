package com.ntu.fdae.group1.bto;

// Essential Imports
import java.util.Scanner;

// Models (Specific User types needed for casting)
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBManager;

// Views
import com.ntu.fdae.group1.bto.views.MainMenuUI;
import com.ntu.fdae.group1.bto.views.ApplicantUI;
import com.ntu.fdae.group1.bto.views.HDBOfficerUI;
import com.ntu.fdae.group1.bto.views.HDBManagerUI;

// Controllers (All controllers needed by different UIs)
import com.ntu.fdae.group1.bto.controllers.ControllerContainer;
import com.ntu.fdae.group1.bto.controllers.user.*;
import com.ntu.fdae.group1.bto.controllers.project.*;
import com.ntu.fdae.group1.bto.controllers.booking.*;
import com.ntu.fdae.group1.bto.controllers.enquiry.*;

// Services (Interfaces and Concrete implementations needed for initialisation)
import com.ntu.fdae.group1.bto.services.user.*;
import com.ntu.fdae.group1.bto.utils.IdGenerator;
import com.ntu.fdae.group1.bto.services.project.*;
import com.ntu.fdae.group1.bto.services.booking.*;
import com.ntu.fdae.group1.bto.services.enquiry.*;

// Repositories (Interfaces and Concrete implementations needed for initialisation)
import com.ntu.fdae.group1.bto.repository.user.*;
import com.ntu.fdae.group1.bto.repository.project.*;
import com.ntu.fdae.group1.bto.repository.booking.*;
import com.ntu.fdae.group1.bto.repository.enquiry.*;

// Exceptions
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;

/**
 * Main entry point for the BTO Management System application.
 * Handles initialisation, the main application loop, and routing to user
 * interfaces based on role.
 * <p>
 * This class implements the MVC architecture pattern by acting as the bootstrap
 * component that connects controllers with views and manages the application
 * lifecycle.
 * </p>
 * 
 * @author Group 1
 * @version 1.0
 */
public class App {
    /**
     * Container holding all controllers used by the application.
     * Provides centralized access to controller instances.
     */
    private final ControllerContainer controllerContainer;

    /**
     * Scanner instance for handling user input throughout the application.
     */
    private final Scanner scanner;

    /**
     * UI component responsible for handling main menu.
     */
    private final MainMenuUI mainMenuUI;

    /**
     * Represents the currently authenticated user.
     * Null when no user is logged in.
     */
    private User currentUser = null;

    /**
     * Constructs a new App instance with the specified controller container and
     * scanner.
     * Initializes the login UI component.
     *
     * @param controllerContainer Container with all the application controllers
     * @param scanner             Scanner instance for handling user input
     */
    public App(ControllerContainer controllerContainer, Scanner scanner) {
        this.controllerContainer = controllerContainer;
        this.scanner = scanner;
        this.mainMenuUI = new MainMenuUI(controllerContainer.authController, scanner);
    }

    /**
     * Initialises all application components (Repositories, Services, Controllers).
     * Handles potential critical errors during initialisation.
     * <p>
     * This method follows a specific initialization order:
     * 1. Repositories are created first
     * 2. Data is loaded from persistent storage
     * 3. Services are created with their repository dependencies
     * 4. Controllers are created with their service dependencies
     * 5. A controller container is created to manage all controllers
     * </p>
     *
     * @return A configured ControllerContainer or null if initialisation fails
     */
    private static ControllerContainer initialiseComponents() {
        try {
            // 1. initialise Repositories
            IUserRepository userRepository = new UserRepository();
            IProjectRepository projectRepository = new ProjectRepository();
            IApplicationRepository applicationRepository = new ApplicationRepository();
            IBookingRepository bookingRepository = new BookingRepository();
            IEnquiryRepository enquiryRepository = new EnquiryRepository();
            IOfficerRegistrationRepository officerRegRepository = new OfficerRegistrationRepository();
            // System.out.println("Repositories initialised.");

            try {
                userRepository.loadAll();
                projectRepository.loadAll();
                applicationRepository.loadAll();
                bookingRepository.loadAll();
                enquiryRepository.loadAll();
                officerRegRepository.loadAll();

                IdGenerator.initialise(
                        projectRepository,
                        applicationRepository,
                        bookingRepository,
                        enquiryRepository,
                        officerRegRepository);
                // System.out.println("Data loaded successfully.");
            } catch (DataAccessException e) {
                System.err.println("FATAL: Failed to load initial data: " + e.getMessage());
                // Optionally: Create default files or handle differently
                // For now, we might continue with empty repositories or exit
                System.err.println("Continuing with potentially empty data stores.");
            }

            // 2. initialise Services (Inject Repositories and other Services)
            EligibilityService eligibilityService = new EligibilityService(projectRepository);

            // Standalone or simple dependencies
            AuthenticationService authService = new AuthenticationService(userRepository);
            UserService userService = new UserService(userRepository);
            ProjectService projectService = new ProjectService(projectRepository, eligibilityService,
                    applicationRepository, officerRegRepository);
            EnquiryService enquiryService = new EnquiryService(enquiryRepository);
            ApplicationService applicationService = new ApplicationService(
                    applicationRepository, projectRepository, eligibilityService,
                    officerRegRepository);
            OfficerRegistrationService officerRegService = new OfficerRegistrationService(
                    officerRegRepository, projectRepository, applicationRepository,
                    eligibilityService);
            BookingService bookingService = new BookingService(
                    applicationRepository, projectRepository, bookingRepository, userRepository);
            ReceiptService receiptService = new ReceiptService(userRepository, projectRepository);
            ReportService reportService = new ReportService(bookingRepository, projectRepository, userRepository);
            // System.out.println("Services initialised.");

            // 3. initialise Controllers (Inject Services)
            AuthenticationController authController = new AuthenticationController(authService);
            UserController userController = new UserController(userService);
            ProjectController projectController = new ProjectController(projectService);
            ApplicationController appController = new ApplicationController(applicationService);
            OfficerRegistrationController officerRegController = new OfficerRegistrationController(officerRegService,
                    projectService);
            BookingController bookingController = new BookingController(bookingService);
            ReceiptController receiptController = new ReceiptController(receiptService);
            EnquiryController enquiryController = new EnquiryController(enquiryService, officerRegService);
            ReportController reportController = new ReportController(reportService);
            // System.out.println("Controllers initialised.");

            // 4. Create Controller Container
            ControllerContainer container = new ControllerContainer(
                    authController, userController, projectController, appController, officerRegController,
                    bookingController, receiptController, enquiryController, reportController);

            // System.out.println("Initialisation complete.");
            return container;

        } catch (Exception e) { // Catch broader exceptions during setup
            System.err.println("FATAL: Application initialisation failed: " + e.getMessage());
            return null; // Indicate failure
        }
    }

    /**
     * Starts and runs the main application loop.
     * <p>
     * This method controls the application's main lifecycle:
     * 1. Displays a welcome message
     * 2. Shows the main menu UI if no user is logged in
     * 3. Routes logged-in users to their role-specific UI
     * 4. Handles logout by resetting the current user
     * 5. Performs cleanup when the application terminates
     * </p>
     */
    public void run() {
        System.out.println("   ___    _____    ___   __  __    ___  ");
        System.out.println("  | _ )  |_   _|  / _ \\ |  \\/  |  / __| ");
        System.out.println("  | _ \\    | |   | (_) || |\\/| |  \\__ \\ ");
        System.out.println("  |___/   _|_|_   \\___/ |_|__|_|  |___/ ");
        System.out.println("_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|");
        System.out.println("\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'");

        System.out.println("\nWelcome to the BTO Management System!");

        boolean isApplicationRunning = true;
        while (isApplicationRunning) {
            if (currentUser == null) {
                currentUser = mainMenuUI.displayMainMenu();

                if (currentUser == null) {
                    // If displayMainMenu returns null, it means the user chose to exit.
                    System.out.println("Exiting application.");
                    isApplicationRunning = false;
                }
            } else {
                // If a user is logged in, route them to their specific UI
                routeToRoleUI(currentUser);
                System.out.println("\nYou have been logged out.");
                currentUser = null; // Reset currentUser to null
            }
        }

        // Cleanup before exiting
        scanner.close();
        System.out.println("\nThank you for using the BTO Management System!");
    }

    /**
     * Routes the user to the appropriate UI based on their role.
     * <p>
     * This method implements role-based access control by:
     * 1. Determining the user's role
     * 2. Instantiating the appropriate UI for that role
     * 3. Displaying the main menu for that UI
     * 4. Handling any errors that occur during UI routing
     * </p>
     *
     * @param user The authenticated user to route to the appropriate UI
     */
    private void routeToRoleUI(User user) {
        try {
            switch (user.getRole()) {
                case APPLICANT:
                    ApplicantUI applicantUI = new ApplicantUI(
                            (Applicant) user,
                            controllerContainer.userController,
                            controllerContainer.projectController,
                            controllerContainer.appController,
                            controllerContainer.enquiryController,
                            controllerContainer.authController, scanner);
                    applicantUI.displayMainMenu();
                    break;

                case HDB_OFFICER:
                    HDBOfficerUI officerUI = new HDBOfficerUI(
                            (HDBOfficer) user, // Cast user to HDBOfficer
                            controllerContainer.userController,
                            controllerContainer.projectController,
                            controllerContainer.appController,
                            controllerContainer.officerRegController,
                            controllerContainer.bookingController,
                            controllerContainer.receiptController,
                            controllerContainer.enquiryController,
                            controllerContainer.authController,
                            scanner);
                    officerUI.displayMainMenu();
                    break;

                case HDB_MANAGER:
                    HDBManagerUI managerUI = new HDBManagerUI(
                            (HDBManager) user,
                            controllerContainer.userController,
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
        } catch (Exception e) {
            System.err.println("An unexpected error occurred in the user interface. Logging out.");
        }
    }

    /**
     * Main method that serves as the entry point for the BTO Management System.
     * <p>
     * This method:
     * 1. Initializes all application components
     * 2. Creates a Scanner for user input
     * 3. Instantiates the App class
     * 4. Starts the application by calling run()
     * 5. Handles any critical initialization failures
     * </p>
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        ControllerContainer controllers = initialiseComponents();
        if (controllers == null) {
            System.err.println("FATAL: Application initialisation failed. Exiting.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        App app = new App(controllers, scanner);
        app.run();
    }
}