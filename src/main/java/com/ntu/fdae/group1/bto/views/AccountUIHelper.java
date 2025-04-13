package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.models.user.User;
import java.util.Objects;

/**
 * Helper class for account-related UI operations in the BTO Management System.
 * <p>
 * This class provides reusable UI components and methods for account management
 * operations such as password changes. It encapsulates the interaction between
 * the user interface and the authentication controller, providing a clean API
 * for account-related UI workflows.
 * </p>
 * <p>
 * The helper follows a composition pattern, working with a BaseUI instance for
 * common UI operations and an AuthenticationController for business logic.
 * </p>
 */
public class AccountUIHelper {
    /**
     * The base UI component for common UI operations.
     */
    private final BaseUI baseUI;

    /**
     * The controller for authentication and account-related operations.
     */
    private final AuthenticationController authController;

    /**
     * Constructs a new AccountUIHelper with the specified dependencies.
     *
     * @param baseUI         The base UI component for common UI operations
     * @param authController The controller for authentication operations
     * @throws NullPointerException if either parameter is null
     */
    public AccountUIHelper(BaseUI baseUI, AuthenticationController authController) {
        this.baseUI = Objects.requireNonNull(baseUI);
        this.authController = Objects.requireNonNull(authController);
    }

    /**
     * Handles the workflow for a logged-in user to change their password.
     *
     * This method guides the user through the password change process by:
     * <ol>
     * <li>Prompting for a new password</li>
     * <li>Requesting confirmation of the new password</li>
     * <li>Validating that the password is not empty</li>
     * <li>Verifying that the confirmation matches the new password</li>
     * <li>Delegating to the authentication controller to perform the change</li>
     * </ol>
     *
     * @param currentUser The currently logged-in user
     * @return true if the password was successfully changed, false otherwise
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