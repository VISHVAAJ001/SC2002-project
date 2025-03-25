package com.ntu.fdae.group1.bto.models.user;

import java.util.List;
import com.ntu.fdae.group1.bto.models.project.Project;

public class HDBManager extends User {
    List<Project> managedProjects;

    public HDBManager(String userId, String password, int age, String maritalStatus) {
        super(userId, password, age, maritalStatus);
    }

}
