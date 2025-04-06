package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.booking.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.booking.Application;

import java.util.Collection;

public interface IEligibilityService {
    boolean canApplicantApply(Applicant applicant, Project project);
    boolean canOfficerRegister(HDBOfficer officer, Project project, Collection<OfficerRegistration> allRegs, Collection<Application> allApps);
	boolean canOfficerRegister(HDBOfficer officer, Project project, Collection<OfficerRegistration> allRegs);
}
