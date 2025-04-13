package com.ntu.fdae.group1.bto.services.user;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.utils.PasswordUtil;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;

/**
 * Implementation of the IAuthenticationService interface that provides
 * authentication
 * functionality for the BTO Management System.
 * <p>
 * This service is responsible for handling user authentication operations,
 * including:
 * - Verifying user credentials during login
 * - Managing password changes
 * - Securing user authentication data
 * </p>
 * <p>
 * The service uses the PasswordUtil class for secure password hashing and
 * verification,
 * and interacts with the user repository to access and update user data.
 * </p>
 */
public class AuthenticationService implements IAuthenticationService {

    /**
     * Repository for accessing and manipulating user data.
     */
    private final IUserRepository userRepository;

    /**
     * Constructs a new AuthenticationService with the specified user repository.
     *
     * @param userRepository Repository for accessing user data
     */
    public AuthenticationService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user with the provided credentials.
     * <p>
     * Verifies that the user exists and that the provided password matches
     * the stored password hash.
     * </p>
     *
     * @param nric     The NRIC (National Registration Identity Card) of the user
     * @param password The password provided by the user
     * @return The authenticated User object if credentials are valid
     * @throws AuthenticationException If the user doesn't exist or the password is
     *                                 incorrect
     */
    public User login(String nric, String password) throws AuthenticationException {
        User user = userRepository.findById(nric);

        if (user == null) {
            throw new AuthenticationException("Login failed: User not found.");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) { // Assuming User has getPasswordHash()
            throw new AuthenticationException("Login failed: Incorrect password.");
        }

        // Login successful
        return user;
    }

    /**
     * Changes a user's password to a new value.
     * <p>
     * Generates a new password hash from the provided password,
     * updates the user object, and persists the change to the repository.
     * </p>
     *
     * @param user        The user whose password should be changed
     * @param newPassword The new password to set
     * @return true if the password was successfully changed, false if any inputs
     *         are invalid
     */
    public boolean changePassword(User user, String newPassword) {
        if (user == null || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        // Generate the new hash using PasswordUtil
        String newHash = PasswordUtil.hashPassword(newPassword);

        // Update the hash on the User object
        user.updatePasswordHash(newHash);

        // Update the user in the repository
        userRepository.save(user);

        return true;
    }

}