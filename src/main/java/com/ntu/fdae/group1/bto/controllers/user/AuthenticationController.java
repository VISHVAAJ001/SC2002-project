package com.ntu.fdae.group1.bto.controllers.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.user.IAuthenticationService;

/**
 * Controller for authentication-related operations
 */
public class AuthenticationController {
    private final IAuthenticationService authService;

    /**
     * Constructs a new AuthenticationController
     * 
     * @param authService The authentication service to use
     */
    public AuthenticationController(IAuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * Attempts to log in a user
     * 
     * @param nric     User's NRIC
     * @param password User's password
     * @return The authenticated user if successful
     * @throws AuthenticationException if authentication fails
     */
    public User login(String nric, String password) throws AuthenticationException {
        return authService.login(nric, password);
    }

    /**
     * Changes a user's password
     * 
     * @param user        The user whose password to change
     * @param newPassword The new password
     * @return true if password change was successful, false otherwise
     */
    public boolean changePassword(User user, String newPassword) {
        return authService.changePassword(user, newPassword);
    }

    /**
     * Registers a new applicant
     * 
     * @param nric          NRIC of the new user (must be unique)
     * @param plainPassword The desired password (will be hashed)
     * @param name          Full name of the user
     * @param age           Age of the user
     * @param maritalStatus Marital status of the user
     * @return true if registration was successful, false otherwise
     * @throws AuthenticationException if NRIC already exists or validation fails.
     * @throws DataAccessException     if saving fails.
     */
    public boolean registerApplicant(String nric, String name, int age,
            MaritalStatus maritalStatus)
            throws AuthenticationException, DataAccessException {
        return authService.registerApplicant(nric, name, age, maritalStatus);
    }
}
