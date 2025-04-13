package com.ntu.fdae.group1.bto.models.project;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;

import java.time.LocalDate;

/**
 * Represents an application for a BTO housing project in the system.
 * <p>
 * This class models a user's application to a specific BTO project, tracking
 * its
 * submission details, status, and progress through the application workflow.
 * Applications move through various states from submission to decision-making,
 * and potentially through withdrawal processes.
 * </p>
 *
 * The application lifecycle typically involves:
 * <ul>
 * <li>Initial submission by an applicant</li>
 * <li>Review by HDB managers</li>
 * <li>Status updates (PENDING → SUCCESSFUL/UNSUCCESSFUL)</li>
 * <li>Potential withdrawal processing</li>
 * <li>Progression to booking if successful</li>
 * </ul>
 *
 */
public class Application {
    /**
     * Unique identifier for the application.
     */
    private String applicationId;

    /**
     * NRIC of the applicant who submitted this application.
     */
    private String applicantNric;

    /**
     * ID of the BTO project being applied for.
     */
    private String projectId;

    /**
     * Date when the application was submitted.
     */
    private LocalDate submissionDate;

    /**
     * Current status of the application in the workflow.
     */
    private ApplicationStatus status = ApplicationStatus.PENDING;

    /**
     * The flat type preferred by the applicant (e.g., 3-ROOM, 4-ROOM).
     */
    private FlatType preferredFlatType;

    /**
     * Date when a withdrawal was requested by the applicant, or null if no
     * withdrawal requested.
     */
    private LocalDate requestedWithdrawalDate;

    /**
     * Constructs a new Application with the specified details.
     *
     * @param applicationId  Unique identifier for the application
     * @param applicantNric  NRIC of the applicant
     * @param projectId      ID of the BTO project being applied for
     * @param submissionDate Date when the application was submitted
     */
    public Application(String applicationId, String applicantNric, String projectId, LocalDate submissionDate) {
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
        this.projectId = projectId;
        this.submissionDate = submissionDate;
    }

    /**
     * Gets the unique identifier for this application.
     *
     * @return The application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the NRIC of the applicant who submitted this application.
     *
     * @return The applicant's NRIC
     */
    public String getApplicantNric() {
        return applicantNric;
    }

    /**
     * Gets the ID of the BTO project being applied for.
     *
     * @return The project ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Gets the date when the application was submitted.
     *
     * @return The submission date
     */
    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Gets the current status of this application in the workflow.
     * <p>
     * Possible statuses include PENDING, SUCCESSFUL, UNSUCCESSFUL, and BOOKED.
     * </p>
     *
     * @return The current application status
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of this application.
     * <p>
     * This method is used to update the application's state as it progresses
     * through the application workflow, from PENDING to either SUCCESSFUL or
     * UNSUCCESSFUL, and potentially to BOOKED.
     * </p>
     *
     * @param status The new application status
     */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    /**
     * Gets the flat type preferred by the applicant.
     *
     * @return The preferred flat type, or null if no preference was specified
     */
    public FlatType getPreferredFlatType() {
        return preferredFlatType;
    }

    /**
     * Sets the flat type preferred by the applicant.
     *
     * @param preferredFlatType The preferred flat type
     */
    public void setPreferredFlatType(FlatType preferredFlatType) {
        this.preferredFlatType = preferredFlatType;
    }

    /**
     * Gets the date when a withdrawal was requested by the applicant.
     *
     * @return The withdrawal request date, or null if no withdrawal was requested
     */
    public LocalDate getRequestedWithdrawalDate() {
        return requestedWithdrawalDate;
    }

    /**
     * Sets the date when a withdrawal was requested by the applicant.
     * <p>
     * Setting this to a date indicates that the applicant has requested to withdraw
     * their application. Setting it to null indicates that no withdrawal is
     * requested
     * or that a previous withdrawal request has been cancelled.
     * </p>
     *
     * @param requestedWithdrawalDate The withdrawal request date, or null
     */
    public void setRequestedWithdrawalDate(LocalDate requestedWithdrawalDate) {
        this.requestedWithdrawalDate = requestedWithdrawalDate;
    }
}
