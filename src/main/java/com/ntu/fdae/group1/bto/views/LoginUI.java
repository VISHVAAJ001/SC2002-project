package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.utils.ValidationUtil;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;

import java.util.Scanner;

public class LoginUI extends BaseUI {

    private AuthenticationController authController;

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

            String password = promptForInput("Enter Password:"); // Basic password input

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
}