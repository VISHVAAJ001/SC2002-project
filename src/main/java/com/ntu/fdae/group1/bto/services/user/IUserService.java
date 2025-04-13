package com.ntu.fdae.group1.bto.services.user;

import com.ntu.fdae.group1.bto.models.user.User;

import java.util.Collection;
import java.util.Map;

/**
 * Service interface for user-related operations in the BTO Management System.
 * <p>
 * This interface defines the contract for user management services, providing
 * methods to retrieve user information by ID or in bulk. It serves as an
 * abstraction layer between controllers and the user repository.
 * </p>
 */
public interface IUserService {
    /**
     * Finds a user by their NRIC (National Registration Identity Card).
     *
     * @param nric The NRIC of the user to find
     * @return The user with the specified NRIC, or null if not found
     */
    User findUserById(String nric);

    /**
     * Retrieves names for a collection of users identified by their NRICs.
     * <p>
     * This method is particularly useful for UI displays that need to show user
     * names alongside references to users by their IDs.
     * </p>
     *
     * @param nrics A collection of NRICs for which to retrieve names
     * @return A map of NRICs to user names
     */
    Map<String, String> findUserNames(Collection<String> nrics);
}
