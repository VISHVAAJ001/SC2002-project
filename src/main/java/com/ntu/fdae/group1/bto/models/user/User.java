package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

/**
 * User class is an abstract class that represents a user in the system.
 * 
 */
public abstract class User {
    private String nric;
    private String passwordHash;
    private String name;
    private int age;
    private MaritalStatus maritalStatus;

    /**
     * Constructor for User class.
     * 
     * @param nric          The nric of the user.
     * @param passwordHash  The hashed password of the user.
     * @param name          The name of the user.
     * @param age           The age of the user.
     * @param maritalStatus The marital status of the user.
     */
    public User(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
        this.nric = nric;
        this.passwordHash = passwordHash;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public abstract UserRole getRole();

    public boolean verifyPassword(String password) {
        return this.passwordHash.equals(password);
    }

    public void updatePasswordHash(String newHash) {
        this.passwordHash = newHash;
    }

    public String getNric() {
        return nric;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }
}
