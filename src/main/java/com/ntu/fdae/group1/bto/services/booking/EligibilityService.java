package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class EligibilityService implements IEligibilityService {
    private final IProjectRepository projectRepository;

    public EligibilityService(IProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository,
                "Project Repository cannot be null for EligibilityService");
    }

    @Override
    public boolean canApplicantApply(Applicant applicant, Project project) {
        if (applicant == null || project == null)
            return false;
        int age = applicant.getAge();
        MaritalStatus status = applicant.getMaritalStatus();
        Map<FlatType, ProjectFlatInfo> flats = project.getFlatTypes();
        if (flats == null || flats.isEmpty())
            return false;
        if (status == MaritalStatus.SINGLE && age >= 35)
            return flats.containsKey(FlatType.TWO_ROOM);
        else if (status == MaritalStatus.MARRIED && age >= 21)
            return true;
        else
            return false;
    }

    @Override
    public boolean isApplicantEligibleForFlatType(Applicant applicant, FlatType flatType) {
        int age = applicant.getAge();
        MaritalStatus status = applicant.getMaritalStatus();

        if (status == MaritalStatus.SINGLE && age >= 35) {
            // Singles >= 35 ONLY eligible for 2-Room
            return flatType == FlatType.TWO_ROOM;
        } else if (status == MaritalStatus.MARRIED && age >= 21) {
            // Married >= 21 eligible for any offered flat type (2 or 3 Room)
            return flatType == FlatType.TWO_ROOM || flatType == FlatType.THREE_ROOM;
        } else {
            // Not meeting either criteria
            return false;
        }
    }

    @Override
    public boolean canOfficerRegister(HDBOfficer officer, Project project,
            Collection<OfficerRegistration> allRegistrations,
            Collection<Application> allApplications) {
        if (officer == null || project == null || allRegistrations == null || allApplications == null) {
            System.err.println("Eligibility Error: Null parameter provided for officer registration check.");
            return false;
        }
        // Ensure project dates are not null before using them
        if (project.getOpeningDate() == null || project.getClosingDate() == null) {
            System.err.println("Eligibility Error: Target project " + project.getProjectId() + " has null dates.");
            return false; // Cannot perform date checks
        }

        LocalDate projectOpen = project.getOpeningDate();
        LocalDate projectClose = project.getClosingDate();
        String officerNric = officer.getNric();
        String projectId = project.getProjectId();

        // Rule 1: No intention to apply for the project as an Applicant
        boolean appliedForThisProject = allApplications.stream()
                .anyMatch(app -> officerNric.equals(app.getApplicantNric()) && projectId.equals(app.getProjectId()));
        if (appliedForThisProject) {
            System.out.println(
                    "Eligibility Fail (Officer " + officerNric + "): Has applied for target project " + projectId);
            return false;
        }

        // Rule 2: Not an HDB Officer (PENDING or APPROVED) for another project
        // within the *same application period* (inclusive dates).
        boolean handlingAnotherProjectInPeriod = allRegistrations.stream()
                .filter(reg -> officerNric.equals(reg.getOfficerNric()) && !projectId.equals(reg.getProjectId()))
                .filter(reg -> reg.getStatus() == OfficerRegStatus.PENDING
                        || reg.getStatus() == OfficerRegStatus.APPROVED)
                .anyMatch(otherReg -> {
                    // <<< USE Injected Repository to find the other project >>>
                    Project otherProject = projectRepository.findById(otherReg.getProjectId());
                    // Check if other project exists and has valid dates
                    if (otherProject != null && otherProject.getOpeningDate() != null
                            && otherProject.getClosingDate() != null) {
                        LocalDate otherOpen = otherProject.getOpeningDate();
                        LocalDate otherClose = otherProject.getClosingDate();

                        // <<< USE projectOpen and projectClose in the overlap check >>>
                        // Check overlap: (StartA <= EndB) and (EndA >= StartB)
                        boolean startABeforeEndB = !projectOpen.isAfter(otherClose);
                        boolean endAAfterStartB = !projectClose.isBefore(otherOpen);

                        return startABeforeEndB && endAAfterStartB; // True if they overlap
                    }
                    // If other project details aren't found, assume no overlap for this specific
                    // check
                    System.err
                            .println("Eligibility Warning: Could not find valid project details for other registration "
                                    + otherReg.getRegistrationId() + ". Assuming no overlap for concurrency check.");
                    return false; // Cannot confirm overlap, so assume false for this registration
                });

        if (handlingAnotherProjectInPeriod) {
            System.out.println("Eligibility Fail (Officer " + officerNric
                    + "): Already handling another project with overlapping application period with project "
                    + projectId);
            return false;
        }

        // All checks passed
        return true;
    }

    @Override
    public boolean checkManagerProjectHandlingEligibility(HDBManager manager, LocalDate newProjectOpenDate,
            LocalDate newProjectCloseDate, Collection<Project> allExistingProjects) {
        if (manager == null || newProjectOpenDate == null || newProjectCloseDate == null || allExistingProjects == null)
            return false;
        String managerNric = manager.getNric();
        boolean overlaps = allExistingProjects.stream()
                .filter(existingProject -> managerNric.equals(existingProject.getManagerNric()))
                .filter(existingProject -> existingProject.getOpeningDate() != null
                        && existingProject.getClosingDate() != null)
                .anyMatch(existingProject -> {
                    LocalDate existingOpen = existingProject.getOpeningDate();
                    LocalDate existingClose = existingProject.getClosingDate();
                    boolean startABeforeEndB = !newProjectOpenDate.isAfter(existingClose);
                    boolean endAAfterStartB = !newProjectCloseDate.isBefore(existingOpen);
                    return startABeforeEndB && endAAfterStartB;
                });
        // if (overlaps) System.out.println("Eligibility Fail: Manager " + managerNric +
        // " already manages overlapping project."); // Optional logging
        return !overlaps;
    }
}
