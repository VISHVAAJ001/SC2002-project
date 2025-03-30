package com.ntu.fdae.group1.bto.models.project;

import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import java.time.LocalDate;

/**
 * Represents an officer registration request for a project.
 * 
 * @param registrationId Unique identifier for the registration request.
 * @param officerNRIC    NRIC of the officer making the registration request.
 * @param projectId      ID of the project for which the officer is registering.
 * @param requestDate    Date when the registration request was made.
 */
public class OfficerRegistration {
	private String registrationId;
	private String officerNRIC;
	private String projectId;
	private LocalDate requestDate;
	private OfficerRegStatus status = OfficerRegStatus.PENDING;

	public OfficerRegistration(String registrationId, String officerNRIC, String projectId, LocalDate requestDate) {
		this.registrationId = registrationId;
		this.officerNRIC = officerNRIC;
		this.projectId = projectId;
		this.requestDate = requestDate;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public String getOfficerNRIC() {
		return officerNRIC;
	}

	public String getProjectId() {
		return projectId;
	}

	public LocalDate getRequestDate() {
		return requestDate;
	}

	public OfficerRegStatus getStatus() {
		return status;
	}

	public void setStatus(OfficerRegStatus newStatus) {
		this.status = newStatus;
	}
}
