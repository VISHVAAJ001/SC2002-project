package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.exceptions.WeakPasswordException;
import com.ntu.fdae.group1.bto.utils.ValidationUtil;
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
     * Handles the interactive workflow for a logged-in user to change their password,
     * including password strength validation via the controller.
     *
     * @param currentUser        The user whose password to change
     * @return true if the password was successfully changed, false otherwise (due to validation errors, user cancellation, or system errors).
    */
    public boolean handlePasswordChange(User currentUser) {
        // --- 1. Display Info & Get Input ---
        baseUI.displayHeader("Change Password");
        baseUI.displayMessage("Password Requirements:");
        baseUI.displayMessage("- 8 to 16 characters long");
        baseUI.displayMessage("- At least one uppercase letter (A-Z)");
        baseUI.displayMessage("- At least one lowercase letter (a-z)");
        baseUI.displayMessage("- At least one digit (0-9)");
        baseUI.displayMessage("- At least one special symbol (" + ValidationUtil.ALLOWED_SPECIAL_CHARS + ")");
        baseUI.displayMessage("- Cannot contain spaces");

        String newPassword = null;
        String confirmPassword = null;

        try {
            // *** Use the new toggle method ***
            newPassword = baseUI.promptForPasswordWithToggle("Enter NEW password:"); // <-- Changed method
            if (newPassword == null) { // Handle potential null return if input reading fails
                 baseUI.displayError("Failed to read password input.");
                 return false;
            }
    
            confirmPassword = baseUI.promptForPasswordWithToggle("Confirm NEW password:"); // <-- Changed method
             if (confirmPassword == null) {
                 baseUI.displayError("Failed to read password confirmation.");
                 return false;
            }

            // --- 2. Basic UI-Level Checks ---
            if (newPassword.isEmpty()) {
                baseUI.displayError("Password cannot be empty.");
                return false;
            }
            if (!newPassword.equals(confirmPassword)) {
                baseUI.displayError("Passwords do not match.");
                return false;
            }

            // --- 3. Delegate to Controller & Handle Outcome ---
            authController.changePassword(currentUser, newPassword);

            baseUI.displayMessage("Password changed successfully.");
            return true;

        } catch (WeakPasswordException e) {
            baseUI.displayError("Password change failed: " + e.getMessage());
            return false;
        } catch (DataAccessException e) {
            baseUI.displayError("Password change failed due to a system error saving the data. Please try again later.");
            return false;
        } catch (Exception e) {
            // Display generic error to user
            baseUI.displayError("An unexpected error occurred during the password change process. Please contact support.");
            return false;
        }
    }
}