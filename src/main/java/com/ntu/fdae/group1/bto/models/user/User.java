package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

/**
 * Abstract base class representing a user in the BTO system.
 * <p>
 * This class serves as the foundation for all user types in the system,
 * providing common attributes and functionality shared by all users regardless
 * of their specific role. User authentication, personal details, and role-based
 * access control are managed through this class hierarchy.
 * </p>
 * <p>
 * All users in the system have basic identifying information including NRIC,
 * name, age, and marital status. Authentication is handled through password
 * hashing for security.
 * </p>
 * 
 * @see Applicant
 * @see HDBStaff
 * @see HDBOfficer
 * @see HDBManager
 */
public abstract class User {
    /**
     * The NRIC (National Registration Identity Card) number that uniquely
     * identifies this user.
     */
    private String nric;

    /**
     * The hashed password used for authentication.
     */
    private String passwordHash;

    /**
     * The full name of this user.
     */
    private String name;

    /**
     * The age of this user.
     */
    private int age;

    /**
     * The marital status of this user, which may affect eligibility for certain
     * housing options.
     */
    private MaritalStatus maritalStatus;

    /**
     * Constructs a new User with the specified details.
     *
     * @param nric          The NRIC number that uniquely identifies this user
     * @param passwordHash  The hashed password used for authentication
     * @param name          The full name of this user
     * @param age           The age of this user
     * @param maritalStatus The marital status of this user
     */
    public User(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
        this.nric = nric;
        this.passwordHash = passwordHash;
        this.name = name;
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    /**
     * Gets the NRIC number of this user.
     *
     * @return The NRIC number
     */
    public String getNric() {
        return nric;
    }

    /**
     * Gets the hashed password of this user.
     *
     * @return The hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets a new hashed password for this user.
     *
     * @param passwordHash The new hashed password
     */
    public void updatePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the full name of this user.
     *
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the age of this user.
     *
     * @return The user's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the marital status of this user.
     *
     * @return The user's marital status
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the role of this user in the system.
     * <p>
     * This abstract method must be implemented by all concrete user classes to
     * define
     * their specific role, which determines their permissions and access rights
     * within
     * the system.
     * </p>
     *
     * @return The user's role
     */
    public abstract UserRole getRole();
}
