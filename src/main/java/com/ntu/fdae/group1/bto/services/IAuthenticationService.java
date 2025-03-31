package com.ntu.fdae.group1.bto.services;

import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.models.user.User;

public interface IAuthenticationService {
    /**
     * Authenticates a user with their NRIC and password
     * 
     * @param nric     The user's NRIC
     * @param password The user's plain text password
     * @return The authenticated user object
     * @throws AuthenticationException If authentication fails
     */
    User login(String nric, String password) throws AuthenticationException;

    /**
     * Changes a user's password
     * 
     * @param user        The user whose password to change
     * @param newPassword The new plain text password
     * @return True if password change was successful
     */
    boolean changePassword(User user, String newPassword);
}
