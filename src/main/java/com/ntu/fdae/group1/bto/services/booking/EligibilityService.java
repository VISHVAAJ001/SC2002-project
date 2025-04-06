package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.models.booking.Application; // Check if this is the correct package for Application
import com.ntu.fdae.group1.bto.models.booking.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import java.util.Collection;

public abstract class EligibilityService implements IEligibilityService {

    @Override
    public boolean canApplicantApply(Applicant applicant, Project project) {
        if (!project.isVisible()) return false;

        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED && applicant.getAge() >= 21)
            return true;
        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && applicant.getAge() >= 35) {
            return project.getFlatInfo("2-Room") != null;
        }
        return false;
    }

    @Override
    public boolean canOfficerRegister(HDBOfficer officer, Project project, Collection<OfficerRegistration> allRegs) {
        long approvedCount = project.getApprovedOfficerNrics().size();
        if (approvedCount >= project.getMaxOfficerSlots()) return false;

        return allRegs.stream().noneMatch(r ->
            r.getOfficerNRIC1().equals(officer.getNric()) &&
            r.getProjectId1().equals(project.getProjectId())
        );
    }
}
