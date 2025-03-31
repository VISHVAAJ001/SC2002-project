package com.ntu.fdae.group1.bto.repository.user;

import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUserRepository implements IUserRepository {
    private static final String FILE_PATH = "data/users.csv"; // Relative to project root

    private Map<String, User> users;

    public FileUserRepository() {
        this.users = new HashMap<>();
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
        users.put(entity.getNric(), entity);
        saveAll(users);
    }

    @Override
    public void saveAll(Map<String, User> entities) {
        this.users = entities;
        try {
            FileUtils.writeCsvLines(FILE_PATH, serializeUsers(), getUserCsvHeader());
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
            // Consider adding more robust error handling here
        }
    }

    @Override
    public Map<String, User> loadAll() {
        try {
            users = deserializeUsers(FileUtils.readCsvLines(FILE_PATH));
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
            users = new HashMap<>(); // Initialize with empty map on error
            // Consider adding more robust error handling here
        }
        return users;
    }

    // Helper methods for serialization/deserialization
    private String[] getUserCsvHeader() {
        return new String[] { "Name", "NRIC", "Age", "Marital Status", "Password", "Role" };
    }

    private Map<String, User> deserializeUsers(List<String[]> csvData) {
        Map<String, User> userMap = new HashMap<>();

        if (csvData == null || csvData.isEmpty()) {
            return userMap;
        }

        for (String[] row : csvData) {
            if (row.length < 6)
                continue; // Skip invalid rows

            try {
                String name = row[0];
                String nric = row[1];
                int age = Integer.parseInt(row[2]);
                MaritalStatus maritalStatus = MaritalStatus.valueOf(row[3]);
                String passwordHash = row[4];
                UserRole role = UserRole.valueOf(row[5]);

                // Create appropriate user based on role
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
                        System.err.println("Unsupported user role: " + role);
                        continue;
                }

                userMap.put(nric, user);
            } catch (IllegalArgumentException e) {
                // Log error or handle invalid data
                System.err.println("Error parsing user data: " + e.getMessage());
            }
        }

        return userMap;
    }

    private List<String[]> serializeUsers() {
        List<String[]> serializedData = new ArrayList<>();
        for (User user : users.values()) {
            serializedData.add(new String[] {
                    user.getName(),
                    user.getNric(),
                    String.valueOf(user.getAge()),
                    user.getMaritalStatus().name(),
                    user.getPasswordHash(),
                    user.getRole().name()
            });
        }
        return serializedData;
    }
}
