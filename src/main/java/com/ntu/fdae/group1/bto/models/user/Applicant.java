package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

public class Applicant extends User {
	private String currentApplicationId;

	public Applicant(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
		super(nric, passwordHash, name, age, maritalStatus);
	}

	@Override
	public UserRole getRole() {
		return UserRole.APPLICANT;
	}

	public String getCurrentApplicationId() {
		return currentApplicationId;
	}

	public void setCurrentApplicationId(String currentApplicationId) {
		this.currentApplicationId = currentApplicationId;
	}
}
