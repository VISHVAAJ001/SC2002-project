package com.ntu.fdae.group1.bto.models.user;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;

public class HDBOfficer extends HDBStaff {
    public HDBOfficer(String nric, String passwordHash, String name, int age, MaritalStatus maritalStatus) {
        super(nric, passwordHash, name, age, maritalStatus);
    }

    @Override
    public UserRole getRole() {
        return UserRole.HDB_OFFICER;
    }
}
