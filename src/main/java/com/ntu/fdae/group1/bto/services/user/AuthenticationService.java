package com.ntu.fdae.group1.bto.services.user;

import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.utils.PasswordUtil;
import com.ntu.fdae.group1.bto.utils.ValidationUtil;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;

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

    /**
     * Registers a new applicant user.
     * <p>
     * Validates the provided information, checks for NRIC uniqueness,
     * hashes the password,
     * creates a new Applicant object, and saves it to the repository.
     * </p>
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
    @Override
    public boolean registerApplicant(String nric, String plainPassword, String name, int age,
            MaritalStatus maritalStatus)
            throws AuthenticationException, DataAccessException {

        // 1. Validation
        if (!ValidationUtil.isValidNric(nric)) {
            throw new AuthenticationException("Invalid NRIC format provided.");
        }
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new AuthenticationException("Name cannot be empty.");
        }
        if (age < 21) {
            throw new AuthenticationException("Applicant must be at least 21 years old.");
        }
        if (maritalStatus == null) {
            throw new AuthenticationException("Marital status must be provided.");
        }

        // 2. Check NRIC Uniqueness
        if (userRepository.findById(nric) != null) {
            throw new AuthenticationException("NRIC '" + nric + "' already exists. Cannot register.");
        }

        // 3. Hash Password
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        // 4. Create Applicant Object (By default, all user who registers is Applicant)
        Applicant newApplicant = new Applicant(nric, hashedPassword, name.trim(), age, maritalStatus);

        // 5. Save to Repository
        try {
            userRepository.save(newApplicant);
            System.out.println("Service: Successfully registered Applicant: " + nric);
            return true;
        } catch (DataAccessException e) {
            System.err.println("Registration Error: Failed to save new applicant " + nric + ": " + e.getMessage());
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            System.err.println("Registration Error: Unexpected error saving applicant " + nric + ": " + e.getMessage());
            throw new DataAccessException("Unexpected error saving user.", e);
        }
    }
}