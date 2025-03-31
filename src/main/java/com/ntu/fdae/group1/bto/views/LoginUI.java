/* package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
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
        return authController.login(nric, password);
    }

    public void displayChangePassword(User user) {
        // Add later
    }
}

*/

//yet to decide if needed