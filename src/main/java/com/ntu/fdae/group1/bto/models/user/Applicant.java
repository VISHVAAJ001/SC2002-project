package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.models.project.Flat;
import com.ntu.fdae.group1.bto.models.project.Project;

public class Applicant extends User {
    private String applicationStatus;
    private Project appliedProject;
    private Flat bookedFlat;

    public Applicant(String userId, String password, int age, String maritalStatus) {
        super(userId, password, age, maritalStatus);
    }

}
