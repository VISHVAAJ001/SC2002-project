package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;

/**
 * Abstract base class representing an HDB staff member in the BTO system.
 * <p>
 * This class serves as a common parent for different types of HDB staff roles,
 * providing shared functionality and attributes for all HDB employees. It
 * extends
 * the base User class with functionality specific to staff members.
 * </p>
 * <p>
 * HDB staff members typically have access to administrative functions in the
 * system
 * that are not available to regular applicants, with specific permissions
 * determined
 * by concrete subclasses.
 * </p>
 * 
 * @see HDBManager
 * @see HDBOfficer
 */
public abstract class HDBStaff extends User {
    /**
     * Constructs a new HDB staff member with the specified details.
     *
     * @param nric          The NRIC (National Registration Identity Card) number of
     *                      the staff member
     * @param passwordHash  The hashed password for authentication
     * @param name          The full name of the staff member
     * @param age           The age of the staff member
     * @param maritalStatus The marital status of the staff member
     */
    public HDBStaff(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
        super(nric, passwordHash, name, age, maritalStatus);
    }
}
