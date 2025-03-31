package com.ntu.fdae.group1.bto.services;

import java.util.Collection;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;

public interface IEligibilityService {
    /**
     * Checks if an applicant is eligible to apply for a project
     * 
     * @param applicant The applicant to check
     * @param project   The project to apply for
     * @return true if the applicant is eligible, false otherwise
     */
    boolean canApplicantApply(Applicant applicant, Project project);

    /**
     * Checks if an officer can register for a project
     * 
     * @param officer          The officer to check
     * @param project          The project to register for
     * @param allRegistrations All existing officer registrations
     * @param allApplications  All existing applications
     * @return true if the officer can register, false otherwise
     */
    boolean canOfficerRegister(HDBOfficer officer, Project project,
            Collection<OfficerRegistration> allRegistrations,
            Collection<Application> allApplications);
}
