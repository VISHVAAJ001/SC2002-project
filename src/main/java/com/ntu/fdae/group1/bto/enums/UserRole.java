package com.ntu.fdae.group1.bto.enums;

/**
 * Defines the roles that users can have within the BTO system.
 * 
 * Each role represents a different level of access and permissions within the
 * system:
 * <ul>
 * <li>{@code APPLICANT} - Regular users who can apply for BTO housing
 * projects</li>
 * <li>{@code HDB_OFFICER} - Staff members who manage day-to-day operations</li>
 * <li>{@code HDB_MANAGER} - Administrative users with the highest level of
 * access</li>
 * </ul>
 * 
 * <p>
 * The role hierarchy determines what actions users can perform, which views
 * they can access,
 * and what data they can modify within the system.
 * </p>
 * 
 * @see com.ntu.fdae.group1.bto.models.user.User
 */
public enum UserRole {
    /**
     * Represents a regular user who can browse BTO projects and submit
     * applications.
     * Applicants have the most restricted access in the system.
     */
    APPLICANT,

    /**
     * Represents an HDB officer who processes applications and manages project
     * operations.
     * Officers have elevated privileges compared to applicants but less than
     * managers.
     */
    HDB_OFFICER,

    /**
     * Represents an HDB manager with administrative access to the entire system.
     * Managers have the highest level of permissions, including system
     * configuration.
     */
    HDB_MANAGER
}
