package com.ntu.fdae.group1.bto.services.user;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

/**
 * Implementation of the IUserService interface that provides user management
 * functionality for the BTO Management System.
 * <p>
 * This service is responsible for retrieving user information, including:
 * - Finding users by their NRIC
 * - Retrieving names for a collection of users
 * </p>
 * <p>
 * The service acts as an intermediary between controllers and the user
 * repository,
 * providing a clean API for user-related operations and handling any necessary
 * business logic or data transformations.
 * </p>
 */
public class UserService implements IUserService {
    /**
     * Repository for accessing and manipulating user data.
     */
    private final IUserRepository userRepository;

    /**
     * Constructs a new UserService with the specified user repository.
     *
     * @param userRepository Repository for accessing user data
     */
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to the user repository to find a user by their NRIC.
     * Returns null if no user is found with the specified NRIC.
     * </p>
     */
    @Override
    public User findUserById(String nric) {
        return userRepository.findById(nric); // Handle null if necessary
    }

    /**
     * {@inheritDoc}
     * <p>
     * Creates a mapping of NRICs to user names for the specified collection of
     * NRICs.
     * If a user cannot be found for a given NRIC, or if the user's name is null,
     * the value "N/A" is used instead.
     * </p>
     * <p>
     * Returns an empty map if the input collection is null or empty.
     * </p>
     */
    @Override
    public Map<String, String> findUserNames(Collection<String> nrics) {
        if (nrics == null || nrics.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> nameMap = new HashMap<>();
        for (String nric : nrics) {
            if (nric != null && !nric.isEmpty()) {
                User user = userRepository.findById(nric);
                nameMap.put(nric, (user != null && user.getName() != null) ? user.getName() : "N/A");
            }
        }
        return nameMap;
    }
}