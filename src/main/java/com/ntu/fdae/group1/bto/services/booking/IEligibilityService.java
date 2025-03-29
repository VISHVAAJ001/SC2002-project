package com.ntu.fdae.group1.bto.services.booking;

import java.util.Collection;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;

public interface IEligibilityService {
    boolean canApplicantApply(Applicant applicant, Project project);
    boolean canOfficerRegister(HDBOfficer officer, Project project, Collection<OfficerRegistration> allRegistrations, Collection<Application> allApplications);
}
