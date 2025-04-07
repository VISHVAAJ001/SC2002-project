package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;

import java.time.LocalDate;
import java.util.Collection;

public class EligibilityService implements IEligibilityService {
    private final IProjectRepository projectRepository;

    public EligibilityService(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public boolean canApplicantApply(Applicant applicant, Project project) {
        // 1. Check Project Visibility (As per PDF Requirement pg 3)
        if (!project.isVisible()) {
            return false;
        }

        // 2. Check if project has defined flats (Defensive check)
        if (project.getFlatTypes() == null || project.getFlatTypes().isEmpty()) {
            return false; // Cannot apply if project offers no flats
        }

        // 3. Apply Eligibility Rules (As per PDF Requirement pg 3)
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED && applicant.getAge() >= 21) {
            // Married >= 21 can apply for any type (2R or 3R assumed offered if project
            // passed check #2)
            return true;
        }

        if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && applicant.getAge() >= 35) {
            return project.getFlatTypes().containsKey(FlatType.TWO_ROOM);
        }

        // 4. Default: Applicant doesn't meet criteria
        return false;
    }

    @Override
    public boolean canOfficerRegister(HDBOfficer officer, Project project,
            Collection<OfficerRegistration> allRegistrations,
            Collection<Application> allApplications) {

        // Rule 1: Check if officer has applied for THIS project as an applicant
        boolean hasAppliedForThisProject = allApplications.stream()
                .anyMatch(app -> app.getApplicantNric().equals(officer.getNric()) &&
                        app.getProjectId().equals(project.getProjectId()));
        if (hasAppliedForThisProject) {
            return false; // Officer intended to apply as applicant, cannot register to handle
        }

        // Rule 2: Check for conflicting registrations (Pending/Approved) in OTHER
        // overlapping projects
        LocalDate currentStart = project.getOpeningDate();
        LocalDate currentEnd = project.getClosingDate();

        for (OfficerRegistration reg : allRegistrations) {
            // Check if the registration is for the current officer BUT for a DIFFERENT
            // project
            if (reg.getOfficerNric().equals(officer.getNric()) && !reg.getProjectId().equals(project.getProjectId())) {

                // Check if the status is Pending or Approved (meaning actively registered or
                // trying to)
                if (reg.getStatus() == OfficerRegStatus.PENDING || reg.getStatus() == OfficerRegStatus.APPROVED) {

                    // Need details of the OTHER project associated with this registration
                    // Use the injected repository
                    Project otherProject = projectRepository.findById(reg.getProjectId()); // Assumes findById exists

                    if (otherProject != null) {
                        // Check for application period overlap
                        LocalDate otherStart = otherProject.getOpeningDate();
                        LocalDate otherEnd = otherProject.getClosingDate();

                        // Overlap condition: (StartA <= EndB) and (EndA >= StartB)
                        // Equivalent to: NOT (EndA < StartB) AND NOT (StartA > EndB)
                        boolean overlaps = !currentEnd.isBefore(otherStart) && !currentStart.isAfter(otherEnd);

                        if (overlaps) {
                            // Found an overlapping registration for another project. Officer is ineligible.
                            return false;
                        }
                    } else {
                        // Handle case where the other project ID doesn't exist? Log warning?
                        System.err.println("Warning: Registration " + reg.getRegistrationId() +
                                " references non-existent project " + reg.getProjectId());
                    }
                }
            }
        }

        // If all checks passed, the officer is eligible to register
        return true;
    }
}
