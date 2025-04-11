package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.models.user.User;
import java.util.Objects;

public class AccountUIHelper {
    private final BaseUI baseUI;
    private final AuthenticationController authController;

    public AccountUIHelper(BaseUI baseUI, AuthenticationController authController) {
        this.baseUI = Objects.requireNonNull(baseUI);
        this.authController = Objects.requireNonNull(authController);
    }

    /**
     * Handles the workflow for a logged-in user to change their password.
     * 
     * @param currentUser The currently logged-in user.
     */
    public boolean handlePasswordChange(User currentUser) {
        baseUI.displayHeader("Change Password");
        try {
            String newPassword = baseUI.promptForInput("Enter NEW password: ");
            String confirmPassword = baseUI.promptForInput("Confirm NEW password: ");

            if (newPassword == null || newPassword.isEmpty()) {
                baseUI.displayError("Password cannot be empty.");
                return false;
            }
            if (!newPassword.equals(confirmPassword)) {
                baseUI.displayError("Passwords do not match.");
                return false;
            }

            boolean success = authController.changePassword(currentUser, newPassword);

            if (success) {
                baseUI.displayMessage("Password changed successfully.");
            } else {
                baseUI.displayError("Password change failed. Please try again.");
            }

            return success;
        } catch (Exception e) {
            baseUI.displayError("An error occurred during password change: " + e.getMessage());
        }
        return false;
    }
}