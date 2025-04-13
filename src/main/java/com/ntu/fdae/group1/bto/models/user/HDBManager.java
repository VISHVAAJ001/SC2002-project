package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

/**
 * Represents an HDB Manager in the BTO system.
 * <p>
 * HDB Managers are senior staff members with the highest level of
 * administrative
 * privileges in the system. They oversee BTO projects and have management
 * responsibilities
 * over HDB Officers and system operations.
 *
 * </p>
 * Managers typically have access to all administrative functions, including:
 * <ul>
 * <li>Managing system-wide settings and configurations</li>
 * <li>Creating and managing BTO projects</li>
 * <li>Overseeing officer assignments to projects</li>
 * <li>Accessing comprehensive reports and analytics</li>
 * </ul>
 * 
 * @see HDBStaff
 * @see UserRole#HDB_MANAGER
 */
public class HDBManager extends HDBStaff {

    /**
     * Constructs a new HDB Manager with the specified details.
     *
     * @param nric          The NRIC (National Registration Identity Card) number of
     *                      the manager
     * @param passwordHash  The hashed password for authentication
     * @param name          The full name of the manager
     * @param age           The age of the manager
     * @param maritalStatus The marital status of the manager
     */
    public HDBManager(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
        super(nric, passwordHash, name, age, maritalStatus);
    }

    /**
     * Gets the user role of this HDB Manager.
     * <p>
     * This implementation returns the HDB_MANAGER role, which grants the highest
     * level of system privileges.
     * </p>
     *
     * @return The HDB_MANAGER user role
     */
    @Override
    public UserRole getRole() {
        return UserRole.HDB_MANAGER;
    }
}
