package com.ntu.fdae.group1.bto.enums;

/**
 * Defines the possible statuses for BTO housing applications in the system.
 * <p>
 * This enum tracks the lifecycle of a housing application from submission
 * through
 * completion. Applications follow a specific workflow with valid status
 * transitions:
 * </p>
 * 
 * Valid status transitions:
 * <ul>
 * <li>PENDING → SUCCESSFUL → BOOKED</li>
 * <li>PENDING → UNSUCCESSFUL</li>
 * <li>SUCCESSFUL → UNSUCCESSFUL (withdrawal before booking)</li>
 * <li>BOOKED → UNSUCCESSFUL (withdrawal after booking)</li>
 * </ul>
 * 
 * 
 * Invalid status transitions:
 * <ul>
 * <li>PENDING → BOOKED (must go through SUCCESSFUL first)</li>
 * <li>SUCCESSFUL → PENDING (cannot revert to previous status)</li>
 * </ul>
 * 
 */
public enum ApplicationStatus {
    /**
     * Represents an application that has been submitted but not yet processed.
     * This is the initial state of all applications.
     */
    PENDING,

    /**
     * Represents an application that has been processed and selected for a flat.
     * Applications in this state have passed the ballot but haven't yet booked a
     * flat.
     */
    SUCCESSFUL,

    /**
     * Represents an application that was rejected, withdrawn, or otherwise did not
     * result in a flat booking.
     * Applications can enter this state from PENDING (direct rejection) or from
     * SUCCESSFUL/BOOKED (withdrawal by applicant).
     */
    UNSUCCESSFUL,

    /**
     * Represents an application where the applicant has successfully selected and
     * booked a flat.
     * This is the final successful state in the application process.
     */
    BOOKED
}
