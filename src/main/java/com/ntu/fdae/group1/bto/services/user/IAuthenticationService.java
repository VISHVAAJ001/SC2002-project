package com.ntu.fdae.group1.bto.services.user;

import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.models.user.User;

/**
 * Service interface for authentication operations in the BTO Management System.
 * <p>
 * This interface defines the contract for authentication services, providing
 * methods to authenticate users and perform password-related operations.
 * It serves as an abstraction layer between controllers and user credential
 * management.
 * </p>
 */
public interface IAuthenticationService {
    /**
     * Authenticates a user with the provided credentials.
     *
     * @param nric     The NRIC of the user
     * @param password The password provided by the user
     * @return The authenticated User object if credentials are valid
     * @throws AuthenticationException If authentication fails due to invalid
     *                                 credentials
     */
    User login(String nric, String password) throws AuthenticationException;

    /**
     * Changes a user's password.
     *
     * @param user        The user whose password should be changed
     * @param newPassword The new password to set
     * @return true if the password was successfully changed, false otherwise
     */
    boolean changePassword(User user, String newPassword);
}
