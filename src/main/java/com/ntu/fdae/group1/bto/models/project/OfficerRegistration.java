package com.ntu.fdae.group1.bto.models.project;

import java.time.LocalDate;

import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;

/**
 * Represents a registration request from an HDB officer to work on a specific
 * BTO project.
 * <p>
 * This class models the relationship between HDB officers and BTO projects,
 * tracking the request and approval workflow for officer assignments. Officer
 * registrations are subject to approval by project managers before officers
 * can work on projects.
 * </p>
 *
 * The registration process is part of the system's authorization model:
 * <ul>
 * <li>Officers submit registration requests for projects they wish to work
 * on</li>
 * <li>Project managers review and either approve or reject these requests</li>
 * <li>Approved officers gain access to manage applications for those
 * projects</li>
 * </ul>
 *
 * <p>
 * Each registration has a status (PENDING, APPROVED, REJECTED) that tracks its
 * position in the workflow.
 * </p>
 */
public class OfficerRegistration {
	/**
	 * Unique identifier for this registration request.
	 */
	private String registrationId;

	/**
	 * NRIC of the officer requesting to work on the project.
	 */
	private String officerNric;

	/**
	 * ID of the project the officer is requesting to work on.
	 */
	private String projectId;

	/**
	 * Date when the registration request was submitted.
	 */
	private LocalDate requestDate;

	/**
	 * Current status of this registration request in the workflow.
	 */
	private OfficerRegStatus status;

	/**
	 * Constructs a new OfficerRegistration with the specified details.
	 * <p>
	 * By default, new registrations are created with a PENDING status.
	 * </p>
	 *
	 * @param registrationId Unique identifier for this registration
	 * @param officerNric    NRIC of the officer making the request
	 * @param projectId      ID of the project the officer wants to work on
	 * @param requestDate    Date when the request was submitted
	 */
	public OfficerRegistration(String registrationId, String officerNric, String projectId, LocalDate requestDate) {
		this.registrationId = registrationId;
		this.officerNric = officerNric;
		this.projectId = projectId;
		this.requestDate = requestDate;
		this.status = OfficerRegStatus.PENDING; // Default status for new registrations
	}

	/**
	 * Gets the unique identifier for this registration.
	 *
	 * @return The registration ID
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Gets the NRIC of the officer requesting to work on the project.
	 *
	 * @return The officer's NRIC
	 */
	public String getOfficerNric() {
		return officerNric;
	}

	/**
	 * Gets the ID of the project the officer is requesting to work on.
	 *
	 * @return The project ID
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Gets the date when the registration request was submitted.
	 *
	 * @return The request date
	 */
	public LocalDate getRequestDate() {
		return requestDate;
	}

	/**
	 * Gets the current status of this registration request.
	 * <p>
	 * Possible statuses include PENDING, APPROVED, and REJECTED.
	 * </p>
	 *
	 * @return The current registration status
	 */
	public OfficerRegStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status of this registration request.
	 * <p>
	 * This method is typically called by project managers when they review
	 * and make decisions on pending registration requests.
	 * </p>
	 *
	 * @param status The new status to set
	 */
	public void setStatus(OfficerRegStatus status) {
		this.status = status;
	}

	/**
	 * Checks if this registration request has been approved.
	 *
	 * @return true if the status is APPROVED, false otherwise
	 */
	public boolean isApproved() {
		return status == OfficerRegStatus.APPROVED;
	}

	/**
	 * Checks if this registration request has been rejected.
	 *
	 * @return true if the status is REJECTED, false otherwise
	 */
	public boolean isRejected() {
		return status == OfficerRegStatus.REJECTED;
	}

	/**
	 * Checks if this registration request is still pending review.
	 *
	 * @return true if the status is PENDING, false otherwise
	 */
	public boolean isPending() {
		return status == OfficerRegStatus.PENDING;
	}
}