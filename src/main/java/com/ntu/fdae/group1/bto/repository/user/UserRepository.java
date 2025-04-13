package com.ntu.fdae.group1.bto.repository.user;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;
import com.ntu.fdae.group1.bto.repository.util.CsvRepositoryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the IUserRepository interface that persists User entities
 * to a CSV file.
 * <p>
 * This repository manages user data for all user types in the BTO Management
 * System,
 * including Applicants, HDB Officers, and HDB Managers. It provides CRUD
 * operations
 * and handles the persistence of user data to a CSV file, with in-memory
 * caching
 * for efficient access.
 * </p>
 * <p>
 * The repository uses a CsvRepositoryHelper to handle the serialization and
 * deserialization of user data between Java objects and CSV format. It
 * maintains
 * data consistency between the in-memory cache and the persistent storage.
 * </p>
 */
public class UserRepository implements IUserRepository {
    /**
     * File path for the CSV file that stores user data.
     */
    private static final String USER_FILE_PATH = "resources/users.csv";

    /**
     * Column headers for the CSV file that stores user data.
     * The order of these headers must match the order of values in the serialized
     * user data.
     */
    private static final String[] USER_CSV_HEADER = new String[] {
            "nric", "passwordHash", "name", "age", "maritalStatus", "role"
    };

    /**
     * In-memory cache of users, keyed by their NRIC.
     * This cache improves performance by reducing the need to read from the CSV
     * file.
     */
    private Map<String, User> users;

    /**
     * Helper that handles CSV file operations for user data.
     * This encapsulates the common CSV operations and provides type-safe
     * conversion.
     */
    private final CsvRepositoryHelper<String, User> csvHelper;

    /**
     * Constructs a new UserRepository.
     * <p>
     * Initializes the repository with a CsvRepositoryHelper configured for
     * User entities and attempts to load existing user data from the CSV file.
     * If the initial data load fails, an empty user collection is created.
     * </p>
     */
    public UserRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                USER_FILE_PATH,
                USER_CSV_HEADER,
                this::deserializeUsers,
                this::serializeUsers);
        // Load initial data
        try {
            this.users = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial user load failed: " + e.getMessage());
            this.users = new HashMap<>(); // Start with empty map on failure
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves a user by their NRIC from the in-memory cache.
     * Returns null if no user exists with the specified NRIC.
     * </p>
     */
    @Override
    public User findById(String id) {
        return users.get(id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a defensive copy of the users map to prevent external
     * modification of the repository's internal state. Users are
     * keyed by their NRIC.
     * </p>
     */
    @Override
    public Map<String, User> findAll() {
        return new HashMap<>(users);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves a user to both the in-memory cache and the CSV file.
     * The user's NRIC is used as the key in the users map.
     * </p>
     * <p>
     * If the user or their NRIC is null, the method logs an error and
     * returns without saving. If saving to the persistent store fails,
     * an error is logged and the exception is propagated.
     * </p>
     */
    @Override
    public void save(User entity) {
        if (entity == null || entity.getNric() == null) {
            System.err.println("Attempted to save null user or user with null NRIC");
            return;
        }
        users.put(entity.getNric(), entity); // Use NRIC as the key
        try {
            csvHelper.saveData(users);
        } catch (DataAccessException e) {
            System.err.println("Failed to save user " + entity.getNric() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Replaces all existing users with the provided collection and
     * persists them to the CSV file. Creates a defensive copy of the
     * provided map to maintain repository encapsulation.
     * </p>
     * <p>
     * If saving to the persistent store fails, an error is logged and
     * the exception is propagated.
     * </p>
     */
    @Override
    public void saveAll(Map<String, User> entities) {
        this.users = new HashMap<>(entities); // Replace with a copy
        try {
            csvHelper.saveData(users);
        } catch (DataAccessException e) {
            System.err.println("Failed to save all users: " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reloads all user data from the CSV file into the in-memory cache,
     * replacing any existing data. Returns a defensive copy of the loaded
     * users map.
     * </p>
     */
    @Override
    public Map<String, User> loadAll() {
        this.users = csvHelper.loadData();
        return new HashMap<>(users);
    }

    /**
     * Deserializes CSV data into User objects of the appropriate subtype.
     * <p>
     * This method converts each CSV row into a User object (Applicant, HDBOfficer,
     * or HDBManager) based on the role field. It handles validation and error
     * checking for required fields and data formats.
     * </p>
     * 
     * @param csvData List of string arrays representing user data from CSV
     * @return A map of deserialized User objects, keyed by their NRICs
     */
    private Map<String, User> deserializeUsers(List<String[]> csvData) {
        Map<String, User> userMap = new HashMap<>();
        if (csvData == null)
            return userMap;

        for (String[] row : csvData) {
            if (row.length < 6) {
                System.err.println("Skipping invalid user row: " + String.join(",", row));
                continue;
            }
            try {
                String nric = row[0];
                String passwordHash = row[1];
                String name = row[2];
                // Use parseIntOrDefault for safer parsing
                int age = FileUtil.parseIntOrDefault(row[3], 0);
                // Use parseEnum with null default, handle null later if status is required
                MaritalStatus maritalStatus = FileUtil.parseEnum(MaritalStatus.class, row[4], null);
                // Role is critical for determining the object type
                UserRole role = FileUtil.parseEnum(UserRole.class, row[5], null);

                if (nric == null || nric.trim().isEmpty()) {
                    System.err.println("Skipping user row due to missing NRIC.");
                    continue;
                }
                if (role == null) {
                    System.err
                            .println("Skipping user row for NRIC " + nric + " due to invalid/missing role: " + row[5]);
                    continue;
                }
                if (maritalStatus == null) { // Example: Decide if MaritalStatus can be null or requires a default
                    System.err.println("Warning: User row for NRIC " + nric
                            + " has invalid/missing marital status. Setting default or skipping.");
                    continue; // Skip if marital status is required
                }

                User user;
                switch (role) {
                    case APPLICANT:
                        user = new Applicant(nric, passwordHash, name, age, maritalStatus);
                        break;
                    case HDB_OFFICER:
                        user = new HDBOfficer(nric, passwordHash, name, age, maritalStatus);
                        break;
                    case HDB_MANAGER:
                        user = new HDBManager(nric, passwordHash, name, age, maritalStatus);
                        break;
                    default:
                        // Should not happen if role parsing worked, but good defensive check
                        System.err.println("Unsupported user role encountered during instantiation: " + role);
                        continue;
                }
                userMap.put(nric, user);
            } catch (Exception e) {
                System.err.println("Error parsing user row: " + String.join(",", row) + " - " + e.getMessage());
            }
        }
        return userMap;
    }

    /**
     * Serializes User objects into CSV format for persistence.
     * <p>
     * This method converts each User object into a string array suitable for
     * writing to a CSV file. It handles null values safely and ensures all
     * user types (Applicant, HDBOfficer, HDBManager) are properly serialized.
     * </p>
     * 
     * @param usersToSerialize Map of User objects to serialize, keyed by NRIC
     * @return List of string arrays representing users in CSV format
     */
    private List<String[]> serializeUsers(Map<String, User> usersToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
        if (usersToSerialize == null)
            return serializedData;

        for (User user : usersToSerialize.values()) {
            // Defensive checks for null fields before calling toString()
            String nric = user.getNric() != null ? user.getNric() : "";
            String hash = user.getPasswordHash() != null ? user.getPasswordHash() : "";
            String name = user.getName() != null ? user.getName() : "";
            String age = String.valueOf(user.getAge()); // Age is primitive, no null check needed
            String maritalStatus = user.getMaritalStatus() != null ? user.getMaritalStatus().toString() : "";
            String role = user.getRole() != null ? user.getRole().name() : "";

            serializedData.add(new String[] { nric, hash, name, age, maritalStatus, role });
        }
        return serializedData;
    }
}
