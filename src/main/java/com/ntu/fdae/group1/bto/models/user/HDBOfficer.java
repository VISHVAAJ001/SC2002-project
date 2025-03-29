package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.models.project.Project;

public class HDBOfficer extends User {
    private Project assignedProject;

    public HDBOfficer(String userId, String password, int age, String maritalStatus) {
        super(userId, password, age, maritalStatus);
    }

	public Project getAssignedProject() {
		// TODO Auto-generated method stub
		return null;
	}

}
