package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;

import java.util.Collection;

public class EligibilityService implements IEligibilityService {

    @Override
    public boolean canApplicantApply(Applicant applicant, Project project) {
        return applicant.getAppliedProject() == null &&
               applicant.getApplicationStatus().equals("NOT_APPLIED");
    }

    @Override
    public boolean canOfficerRegister(HDBOfficer officer, Project project,
                                      Collection<OfficerRegistration> allRegistrations,
                                      Collection<Application> allApplications) {

        return allRegistrations.stream()
            .noneMatch(reg -> reg.getOfficer().equals(officer) &&
                              reg.getProject().equals(project));
    }
}
