package com.ntu.fdae.group1.bto.models.project;

import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import java.time.LocalDate;

/**
 * Represents an officer registration request for a project.
 */
public class OfficerRegistration {
	private String registrationId;
	private String officerNric;
	private String projectId;
	private LocalDate requestDate;
	private OfficerRegStatus status = OfficerRegStatus.PENDING;

	/**
	 * Constructs a new OfficerRegistration
	 *
	 * @param regId       Unique identifier for the registration request
	 * @param officerNric NRIC of the officer making the registration request
	 * @param projId      ID of the project for which the officer is registering
	 * @param requestDate Date when the registration request was made
	 */
	public OfficerRegistration(String regId, String officerNric, String projId, LocalDate requestDate) {
		this.registrationId = regId;
		this.officerNric = officerNric;
		this.projectId = projId;
		this.requestDate = requestDate;
	}

	/**
	 * Gets the registration ID
	 * 
	 * @return the registration ID
	 */
	public String getRegistrationId() {
		return registrationId;
	}

	/**
	 * Gets the officer's NRIC
	 * 
	 * @return the officer's NRIC
	 */
	public String getOfficerNric() {
		return officerNric;
	}

	/**
	 * Gets the project ID
	 * 
	 * @return the project ID
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Gets the request date
	 * 
	 * @return the request date
	 */
	public LocalDate getRequestDate() {
		return requestDate;
	}

	/**
	 * Gets the registration status
	 * 
	 * @return the registration status
	 */
	public OfficerRegStatus getStatus() {
		return status;
	}

	/**
	 * Sets the registration status
	 * Called by OfficerRegistrationService
	 * 
	 * @param newStatus the new status to set
	 */
	public void setStatus(OfficerRegStatus newStatus) {
		this.status = newStatus;
	}
}