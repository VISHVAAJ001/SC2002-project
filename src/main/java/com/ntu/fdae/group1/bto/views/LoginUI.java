package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.models.user.User;

public class LoginUI extends BaseUI {
    private AuthenticationController authController;

    public LoginUI(AuthenticationController authController) {
        this.authController = authController;
    }

    public User displayLogin() {
        displayMessage("Login");
        String nric = promptForInput("Enter NRIC: ");
        String password = promptForInput("Enter Password: ");
        try {
            return authController.login(nric, password);
        } catch (AuthenticationException e) {
            displayMessage("Login failed: " + e.getMessage());
            return null;
        }
    }

    public void displayChangePassword(User user) {
        // Add later
    }
}
