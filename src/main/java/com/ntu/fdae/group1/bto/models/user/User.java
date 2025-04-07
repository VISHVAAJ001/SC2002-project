package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

public abstract class User {
    protected String nric;
    protected String passwordHash;
    protected String name;
    protected int age;
    protected MaritalStatus maritalStatus;
    protected UserRole role;

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

    public String getPasswordHash() {
        return this.passwordHash;
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

    public void updatePasswordHash(String newHash) {
        this.passwordHash = newHash;
    }
}
