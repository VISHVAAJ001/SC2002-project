package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

/**
 * Represents an HDB Officer in the BTO system.
 * <p>
 * HDB Officers are staff members responsible for the day-to-day operations of
 * BTO projects.
 * They handle customer enquiries, process applications, and manage
 * project-specific tasks
 * under the supervision of HDB Managers.
 * </p>
 * 
 * @see HDBStaff
 * @see UserRole#HDB_OFFICER
 */
public class HDBOfficer extends HDBStaff {

    /**
     * Constructs a new HDB Officer with the specified details.
     *
     * @param nric          The NRIC (National Registration Identity Card) number of
     *                      the officer
     * @param passwordHash  The hashed password for authentication
     * @param name          The full name of the officer
     * @param age           The age of the officer
     * @param maritalStatus The marital status of the officer
     */
    public HDBOfficer(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
        super(nric, passwordHash, name, age, maritalStatus);
    }

    /**
     * Gets the user role of this HDB Officer.
     * <p>
     * This implementation returns the HDB_OFFICER role, which grants the
     * appropriate
     * privileges for processing applications and managing assigned projects.
     * </p>
     *
     * @return The HDB_OFFICER user role
     */
    @Override
    public UserRole getRole() {
        return UserRole.HDB_OFFICER;
    }
}
