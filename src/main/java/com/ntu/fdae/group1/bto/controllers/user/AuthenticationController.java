package com.ntu.fdae.group1.bto.controllers.user;

import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.IAuthenticationService;

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
}
