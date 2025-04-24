package com.ntu.fdae.group1.bto.controllers.user;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.user.IUserService;

/**
 * Controller class for handling user-related operations in the BTO Management
 * System.
 * <p>
 * This controller acts as an intermediary between the user interface and the
 * business logic layer, providing methods for:
 * - Retrieving user information
 * - Formatting user data for display
 * - Managing user-related requests
 * </p>
 * <p>
 * The controller delegates complex business logic to the IUserService,
 * focusing on request handling and response formatting.
 * </p>
 */
public class UserController {
    /**
     * The user service that handles business logic for user operations.
     */
    private final IUserService userService;

    /**
     * Constructs a UserController with the specified user service.
     *
     * @param userService The service that provides user-related functionality
     * @throws NullPointerException if userService is null
     */
    public UserController(IUserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    /**
     * Retrieves a user by their NRIC.
     * <p>
     * This method provides a simple wrapper around the service method,
     * with additional null checking to prevent null pointer exceptions.
     * </p>
     *
     * @param nric The NRIC of the user to find
     * @return The User object, or null if not found
     */
    public User getUser(String nric) {
        // Directly call the service method designed for this
        return userService.findUserById(nric);
    }

    /**
     * Retrieves the name of a user identified by their NRIC.
     * <p>
     * This method provides a simple wrapper around the service method,
     * with additional null checking to prevent null pointer exceptions.
     * </p>
     *
     * @param nric The NRIC of the user to find
     * @return The user's name, or "N/A" if the user or their name is not found
     */
    public String getUserName(String nric) {
        User user = userService.findUserById(nric);
        return (user != null && user.getName() != null) ? user.getName() : "N/A";
    }

    /**
     * Retrieves names for a collection of users identified by their NRICs.
     * <p>
     * This method is specifically designed for UI list displays that need
     * to show user names for multiple users efficiently.
     * </p>
     *
     * @param nrics A collection of NRICs for which to retrieve names
     * @return A map of NRICs to user names
     */
    public Map<String, String> getUserNamesForList(Collection<String> nrics) {
        // Directly call the service method designed for this
        return userService.findUserNames(nrics);
    }

}
