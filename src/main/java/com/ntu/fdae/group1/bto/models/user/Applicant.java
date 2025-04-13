package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

/**
 * Represents an Applicant in the BTO system.
 * <p>
 * Applicants are users who can apply for BTO housing projects. They represent
 * citizens or residents who are seeking to purchase a BTO flat. This class
 * extends the base User class with functionality specific to housing
 * applicants.
 * </p>
 * 
 * Applicants can:
 * <ul>
 * <li>Browse available BTO projects</li>
 * <li>Submit applications for housing units</li>
 * <li>Check application status</li>
 * <li>Submit enquiries about projects or applications</li>
 * </ul>
 * 
 * @see User
 * @see UserRole#APPLICANT
 */
public class Applicant extends User {

	/**
	 * Constructs a new Applicant with the specified details.
	 *
	 * @param nric          The NRIC (National Registration Identity Card) number of
	 *                      the applicant
	 * @param passwordHash  The hashed password for authentication
	 * @param name          The full name of the applicant
	 * @param age           The age of the applicant
	 * @param maritalStatus The marital status of the applicant, which may affect
	 *                      eligibility for certain flat types
	 */
	public Applicant(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
		super(nric, passwordHash, name, age, maritalStatus);
	}

	/**
	 * Gets the user role of this Applicant.
	 * <p>
	 * This implementation returns the APPLICANT role, which grants access to
	 * housing application functions but not administrative capabilities.
	 * </p>
	 *
	 * @return The APPLICANT user role
	 */
	@Override
	public UserRole getRole() {
		return UserRole.APPLICANT;
	}
}
