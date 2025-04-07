package com.ntu.fdae.group1.bto.services.booking; // Or your appropriate services package

import com.ntu.fdae.group1.bto.models.project.Application; // Assuming Application entity exists
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.OfficerRegistration; // Assuming OfficerRegistration entity exists

import java.time.LocalDate;
import java.util.Collection;

/**
 * Interface defining eligibility checks for various BTO system actions.
 */
public interface IEligibilityService {

    /**
     * Checks if an Applicant meets the basic criteria (age, marital status, flat type availability)
     * to apply for a specific BTO project.
     *
     * @param applicant The applicant applying.
     * @param project   The project being applied for.
     * @return true if the applicant is eligible based on basic rules, false otherwise.
     */
    boolean checkApplicationEligibility(Applicant applicant, Project project);

    /**
     * Checks if an HDB Officer is eligible to register to handle a specific BTO project.
     * Considers rules like not applying for the same project, not handling multiple projects
     * within overlapping application periods, etc.
     *
     * @param officer          The officer requesting registration.
     * @param project          The project they want to register for.
     * @param allRegistrations All existing officer registrations (needed for concurrency checks).
     * @param allApplications  All existing BTO applications (needed to check if officer applied).
     * @return true if the officer is eligible to register, false otherwise.
     */
    boolean checkOfficerRegistrationEligibility(HDBOfficer officer, Project project,
                                                Collection<OfficerRegistration> allRegistrations,
                                                Collection<Application> allApplications);


    /**
     * Checks if an HDB Manager is eligible to create/handle a new project based on concurrency rules.
     * Managers can only handle one project where the application periods overlap.
     *
     * @param manager            The manager creating/handling the project.
     * @param newProjectOpenDate The opening date of the project being considered.
     * @param newProjectCloseDate The closing date of the project being considered.
     * @param allExistingProjects A collection of all projects currently in the system.
     * @return true if the manager can handle this new project concurrently, false otherwise.
     */
    boolean checkManagerProjectHandlingEligibility(HDBManager manager, LocalDate newProjectOpenDate,
                                                   LocalDate newProjectCloseDate, Collection<Project> allExistingProjects);

    // Add other eligibility check methods as needed (e.g., for booking)

}