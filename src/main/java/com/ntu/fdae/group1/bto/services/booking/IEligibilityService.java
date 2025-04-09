package com.ntu.fdae.group1.bto.services.booking;

import java.time.LocalDate;
import java.util.Collection;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.enums.FlatType;

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
         * Checks if an applicant is eligible for a specific flat type
         * 
         * @param applicant The applicant to check
         * @param flatType  The flat type to check against
         * @return true if the applicant is eligible, false otherwise
         */
        boolean isApplicantEligibleForFlatType(Applicant applicant, FlatType flatType);

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

        /**
         * Checks if an HDB Manager is eligible to create/handle a new project based on
         * concurrency rules.
         * Managers can only handle one project where the application periods overlap.
         *
         * @param manager             The manager creating/handling the project.
         * @param newProjectOpenDate  The opening date of the project being considered.
         * @param newProjectCloseDate The closing date of the project being considered.
         * @param allExistingProjects A collection of all projects currently in the
         *                            system.
         * @return true if the manager can handle this new project concurrently, false
         *         otherwise.
         */
        boolean checkManagerProjectHandlingEligibility(HDBManager manager, LocalDate newProjectOpenDate,
                        LocalDate newProjectCloseDate,
                        Collection<Project> allExistingProjects);
}
