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

public class UserRepository implements IUserRepository {
    private static final String USER_FILE_PATH = "resources/users.csv";
    private static final String[] USER_CSV_HEADER = new String[] {
        "nric", "passwordHash", "name", "age", "maritalStatus", "role"
    };

    private Map<String, User> users;
    private final CsvRepositoryHelper<String, User> csvHelper;

    public UserRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                USER_FILE_PATH,
                USER_CSV_HEADER,
                this::deserializeUsers, 
                this::serializeUsers 
        );
        // Load initial data
        try {
            this.users = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial user load failed: " + e.getMessage());
            this.users = new HashMap<>(); // Start with empty map on failure
        }
    }

    @Override
    public User findById(String id) {
        return users.get(id);
    }

    @Override
    public Map<String, User> findAll() {
        return new HashMap<>(users);
    }

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

    @Override
    public Map<String, User> loadAll() {
        this.users = csvHelper.loadData();
        return new HashMap<>(users);
    }

    private Map<String, User> deserializeUsers(List<String[]> csvData) {
        Map<String, User> userMap = new HashMap<>();
        if (csvData == null) return userMap;

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
                    System.err.println("Skipping user row for NRIC " + nric + " due to invalid/missing role: " + row[5]);
                    continue;
                }
                 if (maritalStatus == null) { // Example: Decide if MaritalStatus can be null or requires a default
                    System.err.println("Warning: User row for NRIC " + nric + " has invalid/missing marital status. Setting default or skipping.");
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

    // Method signature matches the Function expected by CsvRepositoryHelper
    private List<String[]> serializeUsers(Map<String, User> usersToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
         if (usersToSerialize == null) return serializedData;

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
