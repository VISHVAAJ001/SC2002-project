package com.ntu.fdae.group1.bto.views;

import java.util.Scanner;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.models.user.User;

/**
 * MainMenuUI class for handling the main menu of the BTO Management System.
 * <p>
 * This class is responsible for displaying the main menu options to the user.
 * It provides options for logging in or exiting the application. The main menu
 * serves as the entry point to the system.
 * </p>
 * <p>
 * The class extends BaseUI to leverage common UI components and input handling
 * methods, providing a consistent user experience.
 * </p>
 */
public class MainMenuUI extends BaseUI {
    /**
     * loginUI instance for handling user login.
     */
    private final LoginUI loginUI;

    /**
     * Constructs a new MainMenuUI with the specified authentication controller and
     * scanner.
     *
     * @param authController The controller for handling authentication operations
     * @param scanner        The scanner for reading user input
     * @throws IllegalArgumentException if authController is null
     */
    public MainMenuUI(AuthenticationController authController, Scanner scanner) {
        super(scanner);
        if (authController == null) {
            throw new IllegalArgumentException("AuthenticationController cannot be null");
        }
        this.loginUI = new LoginUI(authController, scanner);
    }

    /**
     * Displays the main menu and handles user input.
     * 
     * @return The authenticated User object if login is successful, or null if the
     *         user chooses to exit.
     */
    public User displayMainMenu() {
        while (true) {
            displayHeader("Main Menu");
            System.out.println("[1] Login");
            System.out.println("[2] Register");
            System.out.println("[0] Exit");

            String choice = promptForInput("Please select an option:");

            switch (choice) {
                case "1":
                    User user = loginUI.displayLogin();
                    if (user != null) {
                        return user; // Return the authenticated user
                    } else {
                        break;
                    }
                case "2":
                    loginUI.displayRegister();
                    break;
                case "0":
                    return null;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
