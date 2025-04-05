package com.ntu.fdae.group1.bto.utils;

public class PasswordUtil {
    public static String hashPassword(String password) {
        // Implement your password hashing logic here
        // For example, you can use BCrypt or PBKDF2 for hashing
        return password; // Placeholder, replace with actual hashing
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        // Implement your password verification logic here
        // For example, you can use BCrypt or PBKDF2 for verification
        return password.equals(hashedPassword); // Placeholder, replace with actual verification
    }
}
