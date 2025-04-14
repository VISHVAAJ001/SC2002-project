package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.utils.ValidationUtil;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;

import java.util.Arrays;
import java.util.Scanner;

/**
 * LoginUI class for handling the login process in the BTO Management System.
 * <p>
 * This class is responsible for displaying the login prompt, collecting user
 * credentials, and delegating authentication to the AuthenticationController.
 * It handles initial authentication before directing users to their
 * role-specific UI.
 * </p>
 * <p>
 * The class extends BaseUI to leverage common UI components and input handling
 * methods, providing a consistent user experience.
 * </p>
 */
public class LoginUI extends BaseUI {

    /**
     * Controller for handling authentication operations.
     */
    private final AuthenticationController authController;

    /**
     * Constructs a new LoginUI with the specified authentication controller and
     * scanner.
     *
     * @param authController The controller for handling authentication operations
     * @param scanner        The scanner for reading user input
     * @throws IllegalArgumentException if authController is null
     */
    public LoginUI(AuthenticationController authController, Scanner scanner) {
        super(scanner);
        if (authController == null) {
            throw new IllegalArgumentException("AuthenticationController cannot be null");
        }
        this.authController = authController;
    }

    /**
     * Displays the login prompt and handles the login process.
     * 
     * @return The authenticated User object, or null if the user chooses to exit.
     */
    public User displayLogin() {
        displayHeader("Login");
        while (true) {
            String nric = promptForInput("Enter NRIC (or type 'exit' to quit):");
            if ("exit".equalsIgnoreCase(nric)) {
                return null; // Signal exit
            }

            // Basic NRIC format check
            if (!ValidationUtil.isValidNric(nric)) {
                displayError("Invalid NRIC format.");
                continue;
            }

            // String password = promptForInput("Enter Password:"); // Basic password input
            // Masked password input
            String password = promptForPassword("Enter Password:"); // Basic password input

            try {
                User user = authController.login(nric, password);
                if (user != null) {
                    displayMessage("Login successful!");
                    return user; // Return authenticated user
                }
                // If login returns null without exception, it shouldn't happen based on
                // controller logic
                displayError("Login failed. Please check your credentials."); // Generic error if no exception
            } catch (AuthenticationException e) {
                displayError(e.getMessage()); // Display specific error from controller/service
            } catch (Exception e) {
                displayError("An unexpected error occurred during login: " + e.getMessage());
            }
        }
    }

    /**
     * Displays the registration prompt and handles the registration process.
     * <p>
     * This method collects user information, validates it, and attempts to register
     * the user through the AuthenticationController.
     * </p>
     */
    public void displayRegister() {
        displayHeader("Register");
        while (true) {
            String nric = promptForInput("Enter NRIC (or type 'exit' to quit):");
            if ("exit".equalsIgnoreCase(nric)) {
                break;
            }

            // Basic NRIC format check
            if (!ValidationUtil.isValidNric(nric)) {
                displayError("Invalid NRIC format.");
                continue;
            }

            String password = promptForPassword("Enter Password:"); // Basic password input
            String confirmPassowrd = promptForPassword("Confirm Password:"); // Basic password input

            // Check if password and confirm password match
            if (!password.equals(confirmPassowrd)) {
                displayError("Passwords do not match.");
                continue;
            }

            // Continue to ask for other fields (name, age, marital status, etc.)
            String name = promptForInput("Enter Name:");

            // Validate name
            if (name == null || name.trim().isEmpty()) {
                displayError("Name cannot be empty.");
                continue;
            }

            int age = promptForInt("Enter Age:");

            // Validate age
            if (age < 21 || age > 120) {
                displayError("Invalid age, Applicant must be at least 21 years old.");
                continue;
            }

            MaritalStatus maritalStatus = promptForEnum("Enter Marital Status", MaritalStatus.class,
                    Arrays.asList(MaritalStatus.values()));

            // Validate marital status
            if (maritalStatus == null) {
                displayError("Invalid marital status. Please select a valid option.");
                continue;
            }

            try {
                boolean success = authController.registerApplicant(nric, password, name, age, maritalStatus);

                if (success) { // Only reachable if no exception was thrown
                    displayMessage("\nRegistration successful!");
                    displayMessage("Please select 'Login' from the main menu to continue.");
                    break;
                }

            } catch (AuthenticationException | DataAccessException e) {
                displayError("Registration Error: " + e.getMessage());
            } catch (Exception e) {
                displayError("An unexpected error occurred during registration: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}